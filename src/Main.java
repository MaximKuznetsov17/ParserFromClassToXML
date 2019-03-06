import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws XmlFormatException, ClassNotFoundException, IOException, InvocationTargetException, IllegalAccessException {
        Person person = new Person("Maxim", "RUS", 19, "man", new Address("Russia", "Saint-P", "Lenina", 228));
        Child child = new Child("Maxim", "RUS", 19, "man", new Address("Russia", "Saint-P", "Lenina", 228), "dfsdf");
        Serialization serialization = new Serialization(person);
        Document document = serialization.exploreObject();
        try (FileWriter fileWriter = new FileWriter("output.xml")) {
            XMLWriter writer = new XMLWriter(fileWriter);
            writer.write(document);
            writer.close();
            OutputFormat format = OutputFormat.createPrettyPrint();
            writer = new XMLWriter(System.out, format);
            writer.write(document);
        }
    }
}
