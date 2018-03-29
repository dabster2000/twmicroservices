package dk.trustworks.invoicewebui.web.login;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.login.components.ResetPasswordImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 12/08/2017.
 */
@SpringView(name = ResetView.VIEW_NAME)
public class ResetView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(ResetView.class.getName());

    public static final String VIEW_NAME = "reset";

    @Autowired
    private ResetPasswordImpl resetPassword;

    @Transactional
    @PostConstruct
    void init() {
        logger.debug("ResetView.init");
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
                .withComponent(resetPassword);
        loginRow
                .addColumn()
                .withDisplayRules(12,12, 3, 4)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new Label());
        addComponent(responsiveLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        System.out.println("event = [" + event + "]");
        System.out.println("event.getParameters() = " + event.getParameters());
        if (event.getParameters() != null
                && !event.getParameters().isEmpty()) {
            resetPassword.setUser(event.getParameters());
        }
    }

}
