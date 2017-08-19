package dk.trustworks.invoicewebui.web.security;

import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.ilay.api.Authorizer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hans on 17/08/2017.
 */
@SpringComponent
public class RolePermissionAuthorizer implements Authorizer<CurrentUserRole> {

    @Override
    public boolean isGranted(CurrentUserRole permission) {

        CurrentUserRole currentUserRole = VaadinSession.getCurrent().getAttribute(CurrentUserRole.class);

        if (currentUserRole == null) {
            currentUserRole = CurrentUserRole.GUEST;
            VaadinSession.getCurrent().setAttribute(CurrentUserRole.class, currentUserRole);
        }

        return currentUserRole.ordinal() >= permission.ordinal();
    }

    @Override
    public Class<CurrentUserRole> getPermissionClass() {
        return CurrentUserRole.class;
    }
}
