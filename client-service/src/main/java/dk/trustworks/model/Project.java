package dk.trustworks.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Project {
    @Id
    private String uuid;
    private boolean active;
    private Double budget;
    private Timestamp created;
    private String customerreference;
    private String name;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startdate;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate enddate;
    @Column(insertable=false, updatable=false)
    private String clientuuid;

    @Column(insertable=false, updatable=false)
    private String clientdatauuid;

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

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
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

    public LocalDate getStartdate() {
        return startdate;
    }

    public void setStartdate(LocalDate startdate) {
        this.startdate = startdate;
    }

    public LocalDate getEnddate() {
        return enddate;
    }

    public void setEnddate(LocalDate enddate) {
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

    public String getClientdatauuid() {
        return clientdatauuid;
    }

    public void setClientdatauuid(String clientdatauuid) {
        this.clientdatauuid = clientdatauuid;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Project{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", active=").append(active);
        sb.append(", budget=").append(budget);
        sb.append(", created=").append(created);
        sb.append(", customerreference='").append(customerreference).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", startdate=").append(startdate);
        sb.append(", enddate=").append(enddate);
        sb.append(", clientuuid='").append(clientuuid).append('\'');
        sb.append(", clientdatauuid='").append(clientdatauuid).append('\'');
        sb.append(", client=").append(client);
        sb.append('}');
        return sb.toString();
    }
}
