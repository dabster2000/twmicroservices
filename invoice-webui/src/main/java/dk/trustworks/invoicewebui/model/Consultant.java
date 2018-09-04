package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by hans on 23/06/2017.
 */
@Entity
public class Consultant {

    @Id private String uuid;
    private boolean active;
    private Date created;
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    private String slackusername;
    private LocalDate birthday;
    private int salary;
    @Enumerated(EnumType.STRING)
    private ConsultantType type;
    @Enumerated(EnumType.STRING)
    private StatusType status;
    private int allocation;

    public Consultant() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSlackusername() {
        return slackusername;
    }

    public void setSlackusername(String slackusername) {
        this.slackusername = slackusername;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public ConsultantType getType() {
        return type;
    }

    public void setType(ConsultantType type) {
        this.type = type;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public int getAllocation() {
        return allocation;
    }

    public void setAllocation(int allocation) {
        this.allocation = allocation;
    }

    @Override
    public String toString() {
        return "Consultant{" +
                "uuid='" + uuid + '\'' +
                ", active=" + active +
                ", created=" + created +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", username='" + username + '\'' +
                ", slackusername='" + slackusername + '\'' +
                ", birthday=" + birthday +
                ", salary=" + salary +
                ", type=" + type +
                ", status=" + status +
                ", allocation=" + allocation +
                '}';
    }

}
