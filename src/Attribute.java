public class Attribute {
    private String tag;
    private String name;
    private String value;

    public Attribute(String name, String value){
        this.name = name;
        this.value = value;
    }

    public Attribute(String tag, String name, String value) {
        this.tag = tag;
        this.name = name;
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public String getName(){
        return name;
    }

    public String getValue(){
        return value;
    }

    public String toString(){
        return name + " = " + value;
    }
}
