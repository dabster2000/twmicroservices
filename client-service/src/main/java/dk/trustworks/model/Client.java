package dk.trustworks.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Client {
    @Id
    private String uuid;
    private boolean active;
    private String contactname;
    private java.sql.Timestamp created;
    private String name;
    private Double latitude;
    private Double longitude;
    @OneToMany(mappedBy = "client")
    private List<Clientdata> clientdata;
    @OneToMany(mappedBy = "client")
    private List<Project> projects;
    @Lob
    private byte[] logo;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContactname() {
        return contactname;
    }

    public void setContactname(String contactname) {
        this.contactname = contactname;
    }

    public java.sql.Timestamp getCreated() {
        return created;
    }

    public void setCreated(java.sql.Timestamp created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<Clientdata> getClientdata() {
        return clientdata;
    }

    public void setClientdata(List<Clientdata> clientdata) {
        this.clientdata = clientdata;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
