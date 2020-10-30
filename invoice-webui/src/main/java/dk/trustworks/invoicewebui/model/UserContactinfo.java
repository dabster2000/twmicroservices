package dk.trustworks.invoicewebui.model;

/**
 * Created by hans on 23/06/2017.
 */
public class UserContactinfo {

    private String uuid;
    private String streetname;
    private String postalcode;
    private String city;
    private String phone;

    public UserContactinfo() {
    }

    public UserContactinfo(String streetname, String postalcode, String city, String phone) {
        this.streetname = streetname;
        this.postalcode = postalcode;
        this.city = city;
        this.phone = phone;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStreetname() {
        return streetname;
    }

    public void setStreetname(String streetname) {
        this.streetname = streetname;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UserContactinfo{" +
                "uuid=" + uuid +
                ", streetName='" + streetname + '\'' +
                ", postalCode='" + postalcode + '\'' +
                ", city='" + city + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
