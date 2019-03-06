import annotation.XmlAttribute;
import annotation.XmlObject;
import annotation.XmlTag;
import org.dom4j.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Serialization {

    private Object object;
    private String root;
    private List<Attribute> listAttributesForRoot;
    private List<ElementTag> listRoots;
    private List<Attribute> attributesWithoutTags;

    public Serialization(Object object) {
        listAttributesForRoot = new ArrayList<>();
        listRoots = new ArrayList<>();
        attributesWithoutTags = new ArrayList<>();
        this.object = object;
    }

    private boolean containTag(List<Tag> list, String name) {
        for (Tag tag : list) {
            if (tag.getName().equals(name))
                return true;
        }
        return false;
    }

    private boolean containAttribute(List<Tag> list, String attrName, String tagName) {
        for (Tag tag : list) {
            if (tag.getName().equals(tagName)) {
                for (Attribute attr : tag.getListOfAttributes()) {
                    if (attr.getName().equals(attrName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<Tag> connectLists(List<Tag> main, List<Tag> superClass) {
        List<Tag> res = main;
        for (Tag tag : superClass) {
            if (!containTag(res, tag.getName())) {
                res.add(tag);
            }
        }
        return res;
    }

    private String stringChanger(String name) {
        Pattern Section = Pattern.compile("^get.+$");
        Matcher matcher = Section.matcher(name);
        if (matcher.find()) {
            name = name.substring(3);
            return name;
        }
        return name;
    }

    public String getAttributeTag(XmlAttribute annotation, String name) {
        String res;
        if (annotation.tag().equals("")) {
            res = name;
        } else {
            res = annotation.tag();
        }
        return res;
    }

    private void addAttributeForTag(List<Tag> list, Attribute attribute, String tagName) throws XmlFormatException {
        boolean flag = false;
        if (tagName.equals(root)){
            listAttributesForRoot.add(attribute);
            return;
        }
        for (Tag tag : list) {
            if (tag.getName().equals(tagName)) {
                tag.addAttribute(attribute);
                flag = true;
            }
        }
        if (!flag){
            attributesWithoutTags.add(new Attribute(tagName, attribute.getName(), attribute.getValue()));
        }
    }

    private List<Tag> exploreSuperClass(Class clazz) throws InvocationTargetException, ClassNotFoundException, XmlFormatException, IOException, IllegalAccessException {
        List<Tag> res = new ArrayList<>();
        findField(clazz.getDeclaredFields(), res, root);
        findMethod(clazz.getDeclaredMethods(), res, root);
        return res;
    }

    public Document exploreObject() throws ClassNotFoundException, XmlFormatException, InvocationTargetException, IllegalAccessException, IOException {
        List<Class> listOfClasses = new ArrayList<>();
        List<Tag> listOfTags = new ArrayList<>();
        this.root = object.getClass().getSimpleName().toLowerCase();
        listOfTags.add(new Tag(root));
        Class clazz = object.getClass();
        Class superClass = clazz.getSuperclass();

        while (superClass.getAnnotation(XmlObject.class) != null) {
            listOfClasses.add(superClass);
            superClass = superClass.getSuperclass();
        }

        for (Class supClass : listOfClasses) {
            listOfTags = connectLists(listOfTags, exploreSuperClass(supClass));
        }
        if (clazz.getAnnotation(XmlObject.class) != null) {
            findField(clazz.getDeclaredFields(), listOfTags, clazz.getSimpleName().toLowerCase());
            findMethod(clazz.getDeclaredMethods(), listOfTags, clazz.getSimpleName().toLowerCase());
        }
        for (Attribute attribute : listAttributesForRoot){
            if (!containAttribute(listOfTags, attribute.getName(), root)){
                listOfTags.get(0).addAttribute(attribute);
            }
        }

        for (Attribute attribute : attributesWithoutTags){
            boolean flag = false;
            for (Tag tag : listOfTags) {
                if (tag.getName().equals(attribute.getTag())) {
                    tag.addAttribute(attribute);
                    flag = true;
                }
            }
            if (!flag) throw new XmlFormatException("Нет элемента с тэгом " + attribute.getTag() + " для атрибута " + attribute.getName());
        }

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(listOfTags.get(0).getName());

        for (Attribute attribute : listOfTags.get(0).getListOfAttributes()){
            root.addAttribute(attribute.getName(), attribute.getValue());
        }
        for (Tag tag : listOfTags) {
            if (!tag.getName().equals(listOfTags.get(0).getName())) {
                if (tag.isXmlObject()) {
                    for (ElementTag additionalRoot : listRoots) {
                        if (additionalRoot.getTag().equals(tag.getName())) {
                            Element element = root.addElement(tag.getName());
                            List<Attribute> list = tag.getListOfAttributes();
                            for (Attribute attribute : list) {
                                element.addAttribute(attribute.getName(), attribute.getValue());
                            }
                            element.add(additionalRoot.getElement());
                        }
                    }
                } else {
                    Element add = root.addElement(tag.getName()).addText(tag.getValue());
                    List<Attribute> list = tag.getListOfAttributes();
                    for (Attribute attr : list) {
                        add.addAttribute(attr.getName(), attr.getValue());
                    }
                }
            }
        }
        return document;
    }

    public void findField(Field[] fields, List<Tag> list, String root) throws IllegalAccessException, XmlFormatException, InvocationTargetException, ClassNotFoundException, IOException {
        for (Field field : fields) {
            field.setAccessible(true);
            XmlTag tagAnnotation = field.getAnnotation(XmlTag.class);
            XmlAttribute attributeAnnotation = field.getAnnotation(XmlAttribute.class);
            String tagName = null;
            if ((tagAnnotation != null) && (attributeAnnotation != null)){
                throw new  XmlFormatException("Поле " + field.getName() + " имеет две аннотации!");
            }
            if (tagAnnotation != null) {
                if (tagAnnotation.name().equals("")) {
                    tagName = stringChanger(field.getName());
                } else {
                    tagName = tagAnnotation.name();
                }
                if (containTag(list, tagName)) {
                    throw new XmlFormatException("Повторяющийся тег " + tagName);
                }
                try {
                    Tag tag;
                    if (field.getType().getAnnotation(XmlObject.class) != null) {
                        tag = new Tag(true, tagName, String.valueOf(field.get(object)));
                    } else {
                        tag = new Tag(tagName, String.valueOf(field.get(object)));
                    }
                    if (!containTag(list, tag.getName())){
                        list.add(tag);
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalAccessException();
                }
            }
            if (attributeAnnotation != null) {
                String attributeName;
                if (attributeAnnotation.name().equals("")) {
                    attributeName = stringChanger(field.getName());
                } else {
                    attributeName = attributeAnnotation.name();
                }
                String attributeTag = getAttributeTag(attributeAnnotation, root);
                if (containAttribute(list, attributeName, attributeTag)) {
                    throw new XmlFormatException("Одинаковый атрибут " + attributeName + " для тэга " + attributeTag);
                }
                try {
                    Attribute attribute = new Attribute(attributeName, (String.valueOf(field.get(object))));
                    if (!containAttribute(list, attributeName, attributeTag)){
                        addAttributeForTag(list, attribute, attributeTag);
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalAccessException();
                }
            }
            if (field.getType().getAnnotation(XmlObject.class) != null && tagAnnotation != null) {
                Serialization tracker = new Serialization(field.get(object));
                Document additionalDocument = tracker.exploreObject();
                listRoots.add(new ElementTag(additionalDocument.getRootElement(), tagName));
            }
        }
    }

    public void findMethod(Method[] methods, List<Tag> list, String root) throws XmlFormatException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        for (Method method : methods) {
            method.setAccessible(true);
            XmlTag tagAnnotation = method.getAnnotation(XmlTag.class);
            XmlAttribute attributeAnnotation = method.getAnnotation(XmlAttribute.class);
            String tagName = null;
            if ((tagAnnotation != null) && (attributeAnnotation != null)) {
                throw new XmlFormatException("Метод " + method.getName() + " имеет две аннотации!");
            }
            if (tagAnnotation != null) {
                if (method.getReturnType().getSimpleName().contains(void.class.getSimpleName())) {
                    throw new XmlFormatException("Метод " + method.getName() + " не возвращает параметров");
                }
                if (method.getParameterTypes().length != 0) {
                    throw new XmlFormatException("Метод " + method.getName() + " имеет параметры");
                }
                if (tagAnnotation.name().equals("")) {
                    tagName = stringChanger(method.getName());
                } else {
                    tagName = tagAnnotation.name();
                }
                if (containTag(list, tagName)) {
                    throw new XmlFormatException("Данный тэг уже существует " + tagName);
                }
                try {
                    Tag tag = new Tag(tagName, String.valueOf(method.invoke(object)));
                    if (!containTag(list, tag.getName())) {
                        list.add(tag);
                    }
                } catch (IllegalAccessException e) {
                    throw e;
                }
            }
            if (attributeAnnotation != null) {
                if (method.getReturnType().getSimpleName().contains(void.class.getSimpleName())) {
                    throw new XmlFormatException("Метод " + method.getName() + " не возвращает параметров");
                }
                if (method.getParameterCount() != 0) {
                    throw new XmlFormatException("Метод " + method.getName() + " имеет параметры");
                }
                String attributeName;
                if (tagAnnotation.name().equals("")) {
                    attributeName = stringChanger(method.getName());
                } else {
                    attributeName = tagAnnotation.name();
                }
                String attributeTag = getAttributeTag(attributeAnnotation, root);
                if (containAttribute(list, attributeName, attributeTag)) {
                    throw new XmlFormatException("Данный атрибут" + attributeName + "уже существует");
                }
                try {
                    Attribute attribute = new Attribute(attributeName, (String.valueOf(method.invoke(object))));
                    if (!containAttribute(list, attributeName, attributeTag)) {
                        addAttributeForTag(list, attribute, attributeTag);
                    }
                } catch (IllegalAccessException e) {
                    throw e;
                }
            }
            if (method.getReturnType().getAnnotation(XmlObject.class) != null && tagAnnotation != null) {
                Serialization serialization = new Serialization(method.invoke(object));
                Document additionalDocument = serialization.exploreObject();
                listRoots.add(new ElementTag(additionalDocument.getRootElement(), tagName));
            }
        }
    }
}
