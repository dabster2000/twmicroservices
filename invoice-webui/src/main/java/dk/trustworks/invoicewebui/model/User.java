package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.WordUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hans on 23/06/2017.
 */

public class User {

    private String uuid;
    private boolean active;
    private LocalDate created;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private String username;
    private String teamuuid;
    private String slackusername;
    private LocalDate birthday;
    private List<Salary> salaries;
    private List<UserStatus> statuses;
    private List<Role> roleList;
    private UserContactinfo userContactinfo;
    private Team team;

    public User() {
        uuid = UUID.randomUUID().toString();
        created = LocalDate.now();
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

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
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
        return statuses; //UserService.get().findUserStatusList(this.getUuid());
    }

    public void setStatuses(List<UserStatus> statuses) {
        this.statuses = statuses;
    }

    public UserContactinfo getUserContactinfo() {
        return userContactinfo;
    }

    public void setUserContactinfo(UserContactinfo userContactinfo) {
        this.userContactinfo = userContactinfo;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("roleList")
    private void unpackNested(List<Map<String,Object>> roleList) {
        this.roleList = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : roleList) {
            this.roleList.add(new Role(stringObjectMap.get("uuid").toString(), RoleType.valueOf(((String)stringObjectMap.get("role")).toUpperCase())));
        }
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("userContactinfo")
    private void unpackNestedUserContactinfo(Map<String,Object> stringObjectMap) {
        return;/*
        this.userContactinfo = new UserContactinfo(
                (String)stringObjectMap.getOrDefault("streetName", ""),
                (String)stringObjectMap.getOrDefault("postalCode", ""),
                (String)stringObjectMap.getOrDefault("city", ""),
                (String)stringObjectMap.getOrDefault("phone", ""));
                */
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    @JsonIgnore
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
                //", statusses=" + ArrayUtils.toString(getStatuses().stream().map(UserStatus::toString).toArray()) +
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


    public String getTeamuuid() {
        return teamuuid;
    }

    public void setTeamuuid(String teamuuid) {
        this.teamuuid = teamuuid;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
