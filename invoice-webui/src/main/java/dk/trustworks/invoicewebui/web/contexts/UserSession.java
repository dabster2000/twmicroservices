package dk.trustworks.invoicewebui.web.contexts;


import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.security.CurrentUserRole;

/**
 * Created by hans on 17/08/2017.
 */

public class UserSession {

    private User user;
    private CurrentUserRole currentUserRole;

    public UserSession() {
    }

    public UserSession(User user, CurrentUserRole currentUserRole) {
        this.user = user;
        this.currentUserRole = currentUserRole;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentUserRole getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(CurrentUserRole currentUserRole) {
        this.currentUserRole = currentUserRole;
    }
}
