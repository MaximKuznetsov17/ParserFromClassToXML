public class XmlFormatException extends Exception {
    public XmlFormatException() {
        super("Ошибка при генерации XML-файла!");
    }

    public XmlFormatException(String message) {
        super(message);
    }
}
