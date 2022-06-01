package dk.trustworks.invoicewebui.web.login;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.login.components.ForgotPasswordImpl;
import dk.trustworks.invoicewebui.web.login.components.LoginImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 12/08/2017.
 */
@SpringView(name = LoginView.VIEW_NAME)
public class LoginView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(LoginView.class.getName());

    public static final String VIEW_NAME = "login";

    @Autowired
    private LoginImpl login;

    @Autowired
    private ForgotPasswordImpl forgotPassword;

    @Transactional
    @PostConstruct
    void init() {
        logger.debug("LoginView.init");
        this.setSizeFull();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        responsiveLayout.setSizeFull();
        ResponsiveRow loginRow = responsiveLayout.addRow();
        loginRow
                .addColumn()
                .withDisplayRules(12,12, 3, 4)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new Label());
        loginRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(login);
        loginRow
                .addColumn()
                .withDisplayRules(12,12, 3, 4)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new Label());
        addComponent(responsiveLayout);

        ResponsiveRow resetLoginRow = responsiveLayout.addRow();
        resetLoginRow
                .addColumn()
                .withDisplayRules(12,12, 3, 4)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new Label());
        resetLoginRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(forgotPassword);
        resetLoginRow
                .addColumn()
                .withDisplayRules(12,12, 3, 4)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new Label());
        resetLoginRow.setVisible(false);
        addComponent(responsiveLayout);

        login.getHlResetPassword().addLayoutClickListener(event -> {
            // slack://channel?id=U01GHJJG0E6&team=T036JELTL
            //getUI().getPage().setLocation("slack://channel?id=U01GHJJG0E6&team=T036JELTL");
            loginRow.setVisible(false);
            resetLoginRow.setVisible(true);
        });

        forgotPassword.getHlBackToLogin().addLayoutClickListener(event -> {
            loginRow.setVisible(true);
            resetLoginRow.setVisible(false);
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }

}
