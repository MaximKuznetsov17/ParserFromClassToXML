import annotation.XmlObject;
import annotation.XmlTag;

@XmlObject(name = "address")
public class Address {
    @XmlTag(name = "Living now")
    private final String country;
    @XmlTag(name = "City")
    private final String city;
    @XmlTag(name = "My street")
    private final String street;
    @XmlTag(name = "Number")
    private final int houseNumber;


    public Address(String country, String city, String street, int houseNumber) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    @XmlTag
    public String getStreet() {
        return street;
    }

    @XmlTag
    public int getHouseNumber() {
        return houseNumber;
    }

    @Override
    public String toString() {
        return "Address{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber=" + houseNumber +
                '}';
    }
}
