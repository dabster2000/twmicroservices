package dk.trustworks.invoicewebui.web.resourceplanning;

import com.vaadin.navigator.View;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.resourceplanning.components.SalesView;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 19/12/2016.
 */

@AccessRules(roleTypes = {RoleType.USER})
@SpringView(name = ResourcePlanningView.VIEW_NAME)
public class ResourcePlanningView extends VerticalLayout implements View {

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private SalesView salesView;

    public static final String VIEW_NAME = "availabilityplanning";
    public static final String MENU_NAME = "Availability";
    public static final String VIEW_BREADCRUMB = "Availability Planning";
    public static final FontIcon VIEW_ICON = MaterialIcons.TIMELINE;


    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        mainTemplate.setMainContent(salesView.init(), VIEW_ICON, MENU_NAME, "Availability Planning", VIEW_BREADCRUMB);
    }
}
