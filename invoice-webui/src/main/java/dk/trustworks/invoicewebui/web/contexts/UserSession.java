package dk.trustworks.invoicewebui.web.contexts;


import dk.trustworks.invoicewebui.model.Role;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.LoginToken;
import dk.trustworks.invoicewebui.model.enums.RoleType;

import java.util.List;

/**
 * Created by hans on 17/08/2017.
 */

public class UserSession {

    private User user;
    private LoginToken loginToken;
    private List<Role> roles;

    public UserSession() {
    }

    public UserSession(LoginToken loginToken, List<Role> roles) {
        this.loginToken = loginToken;
        this.roles = roles;
    }

    public UserSession(User user, LoginToken loginToken, List<Role> roles) {
        this.user = user;
        this.loginToken = loginToken;
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

    public LoginToken getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(LoginToken loginToken) {
        this.loginToken = loginToken;
    }

    public boolean hasRole(RoleType roleType) {
        return roles.stream().map(Role::getRole).anyMatch(roleType::equals);
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "user=" + user +
                ", roles=" + roles +
                '}';
    }
}
