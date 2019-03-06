import org.dom4j.*;

public class ElementTag {
    private Element element;
    private String tag;

    public ElementTag(Element element, String tag) {
        this.element = element;
        this.tag = tag;
    }

    public Element getElement() {
        return element;
    }

    public String getTag() {
        return tag;
    }
}
