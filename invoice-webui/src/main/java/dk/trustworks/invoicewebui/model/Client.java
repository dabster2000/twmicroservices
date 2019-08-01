package dk.trustworks.invoicewebui.model;


import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.*;
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

    private String accountmanager;

    private Double latitude;
    private Double longitude;
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Clientdata> clientdata;
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Project> projects;
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Contract> contracts;

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

    public void addClientdata(Clientdata clientdata) {
        this.getClientdata().add(clientdata);
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

    public List<Contract> getContracts() {
        return contracts;
    }

    private void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }

    public User getAccount_manager() {
        return UserService.get().findByUUID(accountmanager);
    }

    public void setAccount_manager(User accountmanager) {
        this.accountmanager = accountmanager.getUuid();
    }

    @Override
    public String toString() {
        return "Client{" +
                "uuid='" + uuid + '\'' +
                ", active=" + active +
                ", contactname='" + contactname + '\'' +
                ", created=" + created +
                ", name='" + name + '\'' +
                ", accountmanager=" + accountmanager +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ( !(o instanceof Client) ) return false;

        final Client client = (Client) o;
        return getUuid().equals(client.getUuid());
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public void setAccountmanager(String accountmanager) {
        this.accountmanager = accountmanager;
    }
}
