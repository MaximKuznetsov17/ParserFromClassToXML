import annotation.XmlAttribute;
import annotation.XmlObject;
import annotation.XmlTag;

@XmlObject
public class Person {
    @XmlAttribute(tag = "Age")
    private String t = "asd";
    @XmlTag(name = "sss")
    private final Test test = new Test(1,"22");
    @XmlAttribute(tag = "fullname")
    private final String lang;
    @XmlTag(name = "fullname")
    private final String name;
    private final int age;
    @XmlTag(name = "Gender")
    private final String sex;
    @XmlTag(name = "My home")
    private final Address address;

    @XmlAttribute
    private final int index = 163000;

    public Person(String name, String lang, int age, String sex, Address address) {
        this.name = name;
        this.lang = lang;
        this.age = age;
        this.sex = sex;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    @XmlTag
    public int getAge() {
        return age;
    }

    @XmlTag
    public String getSex() {
        return sex;
    }

    public Address getAddress() {
        return address;
    }
}