package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.services.ClientService;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.TaskService;
import dk.trustworks.invoicewebui.services.UserService;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Project {

    private String uuid;
    private boolean active;
    private Double budget;
    private Date created;
    private String customerreference;
    private String name;
    private boolean locked;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startdate;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate enddate;

    private String clientuuid;

    private String clientdatauuid;

    private String userowneruuid;

    public Project() {
    }

    public Project(String name, Client client, Clientdata clientdata) {
        this.clientdatauuid = clientdata.getUuid();
        uuid = UUID.randomUUID().toString();
        active = true;
        budget = 0.0;
        created = new Date();
        startdate = LocalDate.now();
        enddate = startdate.plusMonths(3);
        customerreference = "";
        this.name = name;
        this.clientuuid = client.getUuid();
    }

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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
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

    public boolean isActive() {
        return active;
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

    @JsonIgnore
    public User getOwner() {
        return UserService.get().findByUUID(getUserowneruuid());
    }

    @JsonIgnore
    public void setOwner(User owner) {
        this.userowneruuid = owner.getUuid();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return "Project{" + "uuid='" + uuid + '\'' +
                ", active=" + active +
                ", created=" + created +
                ", customerreference='" + customerreference + '\'' +
                ", name='" + name + '\'' +
                ", startdate=" + startdate +
                ", enddate=" + enddate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        return uuid.equals(project.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public String getUserowneruuid() {
        return userowneruuid;
    }

    public void setUserowneruuid(String userowneruuid) {
        this.userowneruuid = userowneruuid;
    }

    @JsonIgnore
    public Client getClient() {
        return ClientService.get().findOne(clientuuid);
    }

    @JsonIgnore
    public List<Task> getTasks() {
        return TaskService.get().findByProject(uuid);
    }

    @JsonIgnore
    public List<Contract> getContracts() {
        return ContractService.get().getContractsByProject(this);
    }
}
