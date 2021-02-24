package dk.trustworks.invoicewebui.web.vtv;

import com.vaadin.navigator.View;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.vtv.layouts.SalesLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 19/12/2016.
 */

@AccessRules(roleTypes = {RoleType.USER})
@SpringView(name = SalesView.VIEW_NAME)
public class SalesView extends VerticalLayout implements View {

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private SalesLayout layout;

    public static final String VIEW_NAME = "salesplanning";
    public static final String MENU_NAME = "Sales";
    public static final String VIEW_BREADCRUMB = "Sales Planning";
    public static final FontIcon VIEW_ICON = MaterialIcons.TIMELINE;


    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        mainTemplate.setMainContent(layout.init(), VIEW_ICON, MENU_NAME, "Sales Planning", VIEW_BREADCRUMB);
    }
}
