package dk.trustworks.invoicewebui.web.security;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.web.contexts.UserSession;

/**
 * Created by hans on 18/08/2017.
 */
public class Authorizer {
    public static void authorize(Component component) {
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        if(userSession == null) component.getUI().getNavigator().navigateTo("login");
    }
}
