package dk.trustworks.invoicewebui.web.contexts;

import dk.trustworks.invoicewebui.web.security.CurrentUserRole;

/**
 * Created by hans on 17/08/2017.
 */

public class UserSession {

    private String uuid;
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    private CurrentUserRole currentUserRole;

    public UserSession() {
    }

    public UserSession(String uuid, String email, String firstname, String lastname, String username, CurrentUserRole currentUserRole) {
        this.uuid = uuid;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.currentUserRole = currentUserRole;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public CurrentUserRole getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(CurrentUserRole currentUserRole) {
        this.currentUserRole = currentUserRole;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserSession{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", firstname='").append(firstname).append('\'');
        sb.append(", lastname='").append(lastname).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", currentUserRole=").append(currentUserRole);
        sb.append('}');
        return sb.toString();
    }
}
