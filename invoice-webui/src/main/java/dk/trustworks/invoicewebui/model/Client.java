package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.trustworks.invoicewebui.services.ClientdataService;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.ProjectService;
import dk.trustworks.invoicewebui.services.UserService;

import java.util.*;

public class Client {
    private String uuid;
    private boolean active;
    private String contactname;
    private Date created;
    private String name;
    private String crmid;

    private String accountmanager;

    public Client() {
    }

    public Client(String contactname, String name) {
        uuid = UUID.randomUUID().toString();
        this.active = true;
        this.contactname = contactname;
        this.created = new Date();
        this.name = name;
        this.crmid = "";
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

    @JsonIgnore
    public List<Clientdata> getClientdata() {
        return ClientdataService.get().findByClient(this);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @JsonIgnore
    public List<Project> getProjects() {
        return ProjectService.get().findByClientuuidOrderByNameAsc(this.getUuid());
    }

    @JsonIgnore
    public User getAccount_manager() {
        return UserService.get().findByUUID(accountmanager);
    }

    @JsonIgnore
    public void setAccount_manager(User accountmanager) {
        this.accountmanager = accountmanager.getUuid();
    }

    public String getCrmid() {
        return crmid;
    }

    public void setCrmid(String crmid) {
        this.crmid = crmid;
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

    @JsonIgnore
    public List<Contract> getContracts() {
        return ContractService.get().findByClientuuid(this.getUuid());
    }
}
