package dk.trustworks.model;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class Project {
    @Id
    private String uuid;
    private String active;
    private Double budget;
    private Timestamp created;
    private String customerreference;
    private String name;
    private Date startdate;
    private Date enddate;
    @Column(insertable=false, updatable=false)
    private String clientuuid;

    @ManyToOne()
    @JoinColumn(name = "clientuuid")
    private Client client;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;

    @ManyToOne()
    @JoinColumn(name = "clientdatauuid")
    private Clientdata clientData;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getCustomerreference() {
        return customerreference;
    }

    public void setCustomerreference(String customerreference) {
        this.customerreference = customerreference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Clientdata getClientData() {
        return clientData;
    }

    public void setClientData(Clientdata clientData) {
        this.clientData = clientData;
    }

    public String getClientuuid() {
        return clientuuid;
    }

    public void setClientuuid(String clientuuid) {
        this.clientuuid = clientuuid;
    }
}
