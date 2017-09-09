package dk.trustworks.invoicewebui.web.login;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.board.Board;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.network.clients.LoginClient;
import dk.trustworks.invoicewebui.services.InvoiceService;
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

    @Transactional
    @PostConstruct
    void init() {
        logger.debug("LoginView.init");
        this.setSizeFull();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        responsiveLayout.setSizeFull();
        ResponsiveRow row = responsiveLayout.addRow();
        row
                .addColumn()
                .withDisplayRules(12,2, 3, 4)
                .withComponent(new Label());
        row
                .addColumn()
                .withDisplayRules(12, 8, 6, 4)
                .withComponent(login);
        row
                .addColumn()
                .withDisplayRules(12,2, 3, 4)
                .withComponent(new Label());
        addComponent(responsiveLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }

}
