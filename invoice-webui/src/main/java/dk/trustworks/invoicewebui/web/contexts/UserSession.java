package dk.trustworks.invoicewebui.web.contexts;


import dk.trustworks.invoicewebui.model.Role;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.model.User;

import java.util.List;

/**
 * Created by hans on 17/08/2017.
 */

public class UserSession {

    private User user;
    private List<Role> roles;

    public UserSession(User user, List<Role> roles) {
        this.user = user;
        this.roles = roles;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public boolean hasRole(RoleType roleType) {
        boolean present = roles.stream().map(Role::getRole).filter(roleType::equals).findFirst().isPresent();
        return present;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "user=" + user +
                ", roles=" + roles +
                '}';
    }
}
