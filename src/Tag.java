import java.util.ArrayList;
import java.util.List;

public class Tag {
    private boolean xmlObject;
    private String name;
    private String value;
    private List<Attribute> listOfAttributes = new ArrayList<>();

    public Tag(String name, String value){
        this.name = name;
        this.value = value;
        this.xmlObject = false;
    }

    public Tag(boolean xmlObject, String name, String value) {
        this.xmlObject = xmlObject;
        this.name = name;
        this.value = value;
    }

    public boolean isXmlObject() {
        return xmlObject;
    }

    public Tag(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getValue(){
        return value;
    }

    public List<Attribute> getListOfAttributes(){
        return listOfAttributes;
    }

    public void addAttribute(Attribute attr){
        listOfAttributes.add((attr));
    }

    public String toString(){
        String res;
        res = name + " - " + value;
        if (listOfAttributes.size() > 0){
            res += ", attributes:";
            int i = 1;
            for (Attribute attribute : listOfAttributes){
                res += String.valueOf(i) + ")" + attribute;
                i++;
            }
        }
        return res;
    }

}
