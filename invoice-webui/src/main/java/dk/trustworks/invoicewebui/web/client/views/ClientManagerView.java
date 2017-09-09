package dk.trustworks.invoicewebui.web.client.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.client.components.ClientManagerImpl;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 16/08/2017.
 */
@AccessRules(roleTypes = {RoleType.SALES})
@SpringView(name = ClientManagerView.VIEW_NAME)
public class ClientManagerView extends VerticalLayout implements View {

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private ClientManagerImpl clientManager;

    public static final String VIEW_NAME = "client";
    public static final String VIEW_BREADCRUMB = "Clients";
    public static final FontIcon VIEW_ICON = MaterialIcons.CONTACT_MAIL;
    public static final String MENU_NAME = "Clients";

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(clientManager.init(), VIEW_ICON, ClientManagerView.MENU_NAME, "Our best friends", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //Authorizer.authorize(this);
    }
}
