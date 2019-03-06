import annotation.XmlObject;
import annotation.XmlTag;

@XmlObject
public class Child extends Person {
    @XmlTag(name = "Class number")
    private String school;


    public Child(String name, String lang, int age, String sex, Address address, String school) {
        super(name, lang, age, sex, address);
        this.school = school;
    }

    @XmlTag
    public String getSchool() {
        return school;
    }
}
