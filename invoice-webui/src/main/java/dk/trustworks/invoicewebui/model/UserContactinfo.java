package dk.trustworks.invoicewebui.model;

import javax.persistence.*;

/**
 * Created by hans on 23/06/2017.
 */
@Entity
@Table(name = "user_contactinfo")
public class UserContactinfo {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="useruuid")
    private User user;

    @Column(name = "street")
    private String streetName;

    @Column(name = "postalcode")
    private String postalCode;

    private String city;


    public UserContactinfo() {
    }

    public UserContactinfo(User user, String streetName, String postalCode, String city) {
        this.user = user;
        this.streetName = streetName;
        this.postalCode = postalCode;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "UserContactinfo{" +
                "id=" + id +
                ", user=" + user +
                ", streetName='" + streetName + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
