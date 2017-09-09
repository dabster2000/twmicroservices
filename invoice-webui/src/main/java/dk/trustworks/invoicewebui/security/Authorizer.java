package dk.trustworks.invoicewebui.security;

import com.vaadin.navigator.View;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.stereotype.Service;

/**
 * Created by hans on 18/08/2017.
 */

@Service
public class Authorizer {
    public void authorize(Component component, RoleType roleType) {
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        if(userSession == null) component.getUI().getNavigator().navigateTo("login");
        if(userSession != null && !userSession.hasRole(roleType)) component.getUI().getNavigator().navigateTo("login");
    }

    public boolean hasAccess(RoleType... roleTypes) {
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        if(roleTypes == null) return true;
        if(roleTypes.length == 0) return true;
        if(userSession != null) {
            for (RoleType roleType : roleTypes) {
                if (userSession.hasRole(roleType)) return true;
            }
        }
        return false;
    }

    public boolean hasAccess(View view) {
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        AccessRules accessRules = view.getClass().getAnnotation(AccessRules.class);
        if(accessRules == null) return true;
        RoleType[] roleTypes = accessRules.roleTypes();
        if(roleTypes == null) return true;
        if(roleTypes.length == 0) return true;
        if(userSession != null) {
            for (RoleType roleType : roleTypes) {
                System.out.println("roleType = " + roleType);
                if (userSession.hasRole(roleType)) return true;
            }
        }
        return false;
    }
}
