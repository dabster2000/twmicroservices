package dk.trustworks.invoicewebui.web.trips;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.trips.components.TripsCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 16/08/2017.
 */
@AccessRules(roleTypes = {RoleType.USER})
@SpringView(name = TripsView.VIEW_NAME)
public class TripsView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(TripsView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private TripsCanvas tripsCanvas;

    public static final String VIEW_NAME = "travel";
    public static final String MENU_NAME = "TRAVEL";
    public static final String VIEW_BREADCRUMB = "Travel";
    public static final FontIcon VIEW_ICON = MaterialIcons.AIRPLANEMODE_ACTIVE;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(tripsCanvas.init(), VIEW_ICON, MENU_NAME, "Trustworks Travel", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
