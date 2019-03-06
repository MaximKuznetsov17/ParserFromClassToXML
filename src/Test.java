import annotation.XmlAttribute;
import annotation.XmlObject;
import annotation.XmlTag;

@XmlObject
public class Test {
    public Test(int t, String s) {
        this.t = t;
        this.s = s;
    }

    @XmlTag(name = "t")
    private int t = 1;

    @XmlAttribute(tag = "t")
    private String s = "2";
}
