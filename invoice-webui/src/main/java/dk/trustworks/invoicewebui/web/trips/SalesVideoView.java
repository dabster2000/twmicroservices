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
import dk.trustworks.invoicewebui.web.trips.components.SalesVideoCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 16/08/2017.
 */
@AccessRules(roleTypes = {RoleType.VTV})
@SpringView(name = SalesVideoView.VIEW_NAME)
public class SalesVideoView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(SalesVideoView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private SalesVideoCanvas salesVideoCanvas;

    public static final String VIEW_NAME = "warstories";
    public static final String MENU_NAME = "WARSTORIES";
    public static final String VIEW_BREADCRUMB = "War Stories";
    public static final FontIcon VIEW_ICON = MaterialIcons.AIRPLANEMODE_ACTIVE;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(salesVideoCanvas.init(), VIEW_ICON, MENU_NAME, "I love the smell of napalm in the morning", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
