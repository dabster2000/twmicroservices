package dk.trustworks.invoicewebui.web.client.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.client.components.ClientManagerImpl;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.views.TrustworksView;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 16/08/2017.
 */
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

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(clientManager.init());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //Authorizer.authorize(this);
    }
}
