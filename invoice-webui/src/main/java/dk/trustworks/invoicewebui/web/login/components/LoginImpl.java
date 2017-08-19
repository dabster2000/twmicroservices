package dk.trustworks.invoicewebui.web.login.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import dk.trustworks.invoicewebui.network.clients.LoginClient;
import dk.trustworks.invoicewebui.network.dto.Client;
import dk.trustworks.invoicewebui.network.dto.User;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

/**
 * Created by hans on 12/08/2017.
 */
public class LoginImpl extends LoginDesign {

    public LoginImpl(LoginClient loginClient) {
        System.out.println("LoginImpl.LoginImpl");
        getImgTop().setSource(new ThemeResource("images/password-card.jpg"));
        getBtnLogin().addClickListener(clickEvent -> {
            User user = loginClient.login(getTxtUsername().getValue(), getTxtPassword().getValue()).getContent();
            UserSession userSession = new UserSession(user.getUuid(), user.getEmail(), user.getFirstname(), user.getLastname(), user.getUsername(), null);
            VaadinSession.getCurrent().setAttribute(UserSession.class, userSession);
            getUI().getNavigator().navigateTo("mainmenu");
        });
    }
}
