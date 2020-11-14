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
import dk.trustworks.invoicewebui.web.vtv.layouts.TenderManagementLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 19/12/2016.
 */

@AccessRules(roleTypes = {RoleType.VTV})
@SpringView(name = TenderManagementView.VIEW_NAME)
public class TenderManagementView extends VerticalLayout implements View {

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private TenderManagementLayout layout;

    public static final String VIEW_NAME = "tendermanagement";
    public static final String MENU_NAME = "Tender Management";
    public static final String VIEW_BREADCRUMB = "Tender Management";
    public static final FontIcon VIEW_ICON = MaterialIcons.MOOD_BAD;


    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        mainTemplate.setMainContent(layout.init(), VIEW_ICON, MENU_NAME, "What can you do!?", VIEW_BREADCRUMB);
    }
}
