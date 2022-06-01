package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Objects;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import lombok.Data;
import org.apache.commons.text.WordUtils;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by hans on 23/06/2017.
 */

@Data
public class User {

    private String uuid;
    private boolean active;
    private LocalDate created;
    private String email;
    private String firstname;
    private String lastname;
    private String gender;
    private String password;
    private String username;
    private String slackusername;
    private LocalDate birthday;
    private String cpr;
    private String phone;
    private boolean pension;
    private boolean healthcare;
    private String pensiondetails;
    private String defects;
    private boolean photoconsent;
    private String other;
    private List<TeamRole> teamroles;
    private List<Salary> salaries;
    private List<UserStatus> statuses;
    private List<Role> roleList;
    private UserContactinfo userContactinfo;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public UserStatus getUserStatus(LocalDate date) {
        return getStatuses().stream().filter(value -> value.getStatusdate().isBefore(date)).max(Comparator.comparing(UserStatus::getStatusdate)).orElse(new UserStatus(ConsultantType.STAFF, StatusType.TERMINATED, date, 0));
    }

    public Salary getSalary(LocalDate date) {
        return getSalaries().stream().filter(value -> value.getActivefrom().isBefore(date)).max(Comparator.comparing(Salary::getActivefrom)).orElse(new Salary(date, 0));
    }

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isPension() {
        return pension;
    }

    public void setPension(boolean pension) {
        this.pension = pension;
    }

    public boolean isHealthcare() {
        return healthcare;
    }

    public void setHealthcare(boolean healthcare) {
        this.healthcare = healthcare;
    }

    public String getPensiondetails() {
        return pensiondetails;
    }

    public void setPensiondetails(String pensiondetails) {
        this.pensiondetails = pensiondetails;
    }

    public String getDefects() {
        return defects;
    }

    public void setDefects(String defects) {
        this.defects = defects;
    }

    public boolean isPhotoconsent() {
        return photoconsent;
    }

    public void setPhotoconsent(boolean photoconsent) {
        this.photoconsent = photoconsent;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
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

    public List<TeamRole> getTeamroles() {
        return teamroles;
    }

    public void setTeamroles(List<TeamRole> teamroles) {
        this.teamroles = teamroles;
    }
}
