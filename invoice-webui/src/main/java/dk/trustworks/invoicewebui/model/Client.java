package dk.trustworks.invoicewebui.model;


import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Client {
    @Id
    private String uuid;
    private boolean active;
    private String contactname;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private String name;
    private Double latitude;
    private Double longitude;
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Clientdata> clientdata;
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Project> projects;
    @OneToOne(mappedBy = "client", fetch = FetchType.LAZY)
    private Logo logo;

    public Client() {
    }

    public Client(String contactname, String name) {
        uuid = UUID.randomUUID().toString();
        this.active = true;
        this.contactname = contactname;
        this.created = new Date();
        this.name = name;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.clientdata = null;
        this.projects = new ArrayList<>();
        this.clientdata = new ArrayList<>();
    }

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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public Logo getLogo() {
        return logo;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Client{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", active=").append(active);
        sb.append(", contactname='").append(contactname).append('\'');
        sb.append(", created=").append(created);
        sb.append(", name='").append(name).append('\'');
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        //sb.append(", clientdata=").append(clientdata);
        //sb.append(", projects=").append(projects);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        return uuid.equals(client.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
