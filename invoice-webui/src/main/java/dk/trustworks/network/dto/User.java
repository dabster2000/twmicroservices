package dk.trustworks.network.dto;

import java.util.Date;
import java.util.UUID;

/**
 * Created by hans on 23/06/2017.
 */

public class User {

    private String uuid;
    private boolean active;
    private Date created;
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    private String slackusername;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", active=").append(active);
        sb.append(", created=").append(created);
        sb.append(", email='").append(email).append('\'');
        sb.append(", firstname='").append(firstname).append('\'');
        sb.append(", lastname='").append(lastname).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", slackusername='").append(slackusername).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
