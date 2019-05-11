package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import org.apache.commons.text.WordUtils;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDate;
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
    private LocalDate birthday;
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private List<Salary> salaries;
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private List<UserStatus> statuses;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Week> weeks;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Work> workList;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Role> roleList;
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<Notification> notifications;
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Project> projects;

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

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
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

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public String getInitials() {
        return WordUtils.initials(firstname + " " + lastname);
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", active=" + active +
                ", created=" + created +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", slackusername='" + slackusername + '\'' +
                ", birthday=" + birthday +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getUuid().equals(user.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUuid());
    }


}
