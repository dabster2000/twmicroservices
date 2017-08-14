package dk.trustworks.invoicewebui.web.login.components;

import com.vaadin.server.ThemeResource;

/**
 * Created by hans on 12/08/2017.
 */
public class LoginImpl extends LoginDesign {

    public LoginImpl() {
        System.out.println("LoginImpl.LoginImpl");
        getImgTop().setSource(new ThemeResource("images/password-card.jpg"));
        getBtnLogin().addClickListener(clickEvent -> {
            getUI().getNavigator().navigateTo("mainmenu");
        });
    }
}
