package dk.trustworks.invoicewebui.web.login.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.network.clients.LoginClient;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.web.Broadcaster;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hans on 12/08/2017.
 */
@SpringComponent
@SpringUI
public class LoginImpl extends LoginDesign {
    @Autowired
    public LoginImpl(LoginClient loginClient) {
        System.out.println("LoginImpl.LoginImpl");
        getImgTop().setSource(new ThemeResource("images/password-card.jpg"));
        getBtnLogin().addClickListener(clickEvent -> {
            User user = loginClient.login(getTxtUsername().getValue(), getTxtPassword().getValue());
            if(user.getRoleList().size()==0) {
                getBtnLogin().setComponentError(new UserError("No user role assigned!"));
                return;
            }
            UserSession userSession = new UserSession(user, user.getRoleList());
            VaadinSession.getCurrent().setAttribute(UserSession.class, userSession);
            Broadcaster.broadcast("login");
            getUI().getNavigator().navigateTo("mainmenu");
        });
        getBtnLogin().setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }
}
