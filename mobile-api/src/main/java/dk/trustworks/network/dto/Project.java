package dk.trustworks.network.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.Resource;

import java.sql.Date;
import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {

    private String uuid;
    private String active;
    private Double budget;
    private Timestamp created;
    private String customerreference;
    private String name;
    private Date startdate;
    private Date enddate;
    private Client client;


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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Project{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", active='").append(active).append('\'');
        sb.append(", budget=").append(budget);
        sb.append(", created=").append(created);
        sb.append(", customerreference='").append(customerreference).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", startdate=").append(startdate);
        sb.append(", enddate=").append(enddate);
        sb.append(", client=").append(client);
        sb.append('}');
        return sb.toString();
    }
}
