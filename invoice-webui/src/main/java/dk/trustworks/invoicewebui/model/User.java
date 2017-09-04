package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 23/06/2017.
 */

@Entity
public class User {

    @Id private String uuid;
    private boolean active;
    private Date created;
    private String email;
    private String firstname;
    private String lastname;
    @JsonIgnore private String password;
    private String username;
    private String slackusername;
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private List<Salary> salaries;
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private List<UserStatus> statuses;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Taskworkerconstraint> taskworkerconstraint;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Budget> budget;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Week> weeks;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Work> workList;

    public User() {
        uuid = UUID.randomUUID().toString();
        created = new Date();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public List<Salary> getSalaries() {
        return salaries;
    }

    public void setSalaries(List<Salary> salaries) {
        this.salaries = salaries;
    }

    public List<UserStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<UserStatus> statuses) {
        this.statuses = statuses;
    }

    public List<Taskworkerconstraint> getTaskworkerconstraint() {
        return taskworkerconstraint;
    }

    public void setTaskworkerconstraint(List<Taskworkerconstraint> taskworkerconstraint) {
        this.taskworkerconstraint = taskworkerconstraint;
    }

    public List<Budget> getBudget() {
        return budget;
    }

    public void setBudget(List<Budget> budget) {
        this.budget = budget;
    }

    public List<Week> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
    }

    public List<Work> getWorkList() {
        return workList;
    }

    public void setWorkList(List<Work> workList) {
        this.workList = workList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", active=").append(active);
        sb.append(", created=").append(created);
        sb.append(", email='").append(email).append('\'');
        sb.append(", firstname='").append(firstname).append('\'');
        sb.append(", lastname='").append(lastname).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", slackusername='").append(slackusername).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
