package dk.trustworks.invoicewebui.web.login;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.network.rest.UserRestService;
import dk.trustworks.invoicewebui.web.login.components.AcceptPasswordChangeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 12/08/2017.
 */
@SpringView(name = ConfirmPasswordChangeView.VIEW_NAME)
public class ConfirmPasswordChangeView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(ConfirmPasswordChangeView.class.getName());

    public static final String VIEW_NAME = "confirmchange";

    private AcceptPasswordChangeImpl acceptPasswordChangeImpl;

    @Autowired
    private UserRestService userRestService;

    @PostConstruct
    void init() {
        logger.debug("ResetView.init");
        this.setSizeFull();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        responsiveLayout.setSizeFull();
        ResponsiveRow loginRow = responsiveLayout.addRow();

        acceptPasswordChangeImpl = new AcceptPasswordChangeImpl();

        loginRow
                .addColumn()
                .withDisplayRules(12,12, 3, 4)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new Label());
        loginRow
                .addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(acceptPasswordChangeImpl);
        loginRow
                .addColumn()
                .withDisplayRules(12,12, 3, 4)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new Label());
        addComponent(responsiveLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if(event.getParameters() != null){
            String[] keys = event.getParameters().split("/");
            acceptPasswordChangeImpl.getBtnConfirmChange().addClickListener(event1 -> {
                userRestService.confirmPasswordChange(keys[0]);
                getUI().getNavigator().navigateTo("login");
            });
        }



        /*
        if (event.getParameters() != null
                && !event.getParameters().isEmpty()) {
            resetPassword.setUser(event.getParameters());
        }
        */
    }

}
