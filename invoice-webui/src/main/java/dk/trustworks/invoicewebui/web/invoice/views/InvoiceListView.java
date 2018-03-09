package dk.trustworks.invoicewebui.web.invoice.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.invoice.components.InvoiceListImpl;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 11/07/2017.
 */
@AccessRules(roleTypes = {RoleType.ACCOUNTING})
@SpringView(name = InvoiceListView.VIEW_NAME)
public class InvoiceListView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "invoice_list";
    public static final String MENU_NAME = "Invoices";
    public static final String VIEW_BREADCRUMB = "Invoice / Invoices";
    public static final FontIcon VIEW_ICON = MaterialIcons.RECEIPT;

    protected static Logger logger = LoggerFactory.getLogger(InvoiceListView.class.getName());

    @Autowired
    private InvoiceListImpl invoiceListComponent;

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        //Broadcaster.register(this);
        mainTemplate.setMainContent(invoiceListComponent, VIEW_ICON, MENU_NAME, "Everything that we ever billed - atleast since Juli 1. 2017", VIEW_BREADCRUMB);
    }

    public void receiveBroadcast(String message) {
        logger.debug("MainWindowImpl.receiveBroadcast");
        logger.debug("message = " + message);
        /*
        int numberOfDrafts = invoiceClient.findByStatus(DRAFT).size();
        System.out.println("numberOfDrafts = " + numberOfDrafts);
        getUI().access(() -> {
            invoiceListComponent.reloadData();
            if(numberOfDrafts>0) getMenuButton4().setCaption("DRAFTS ("+numberOfDrafts+")");
            else getMenuButton4().setCaption("DRAFTS");
        });
        */
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //Authorizer.authorize(this);
        invoiceListComponent.loadInvoicesToGrid();
        //leftMenu.getMenuItems().get(VIEW_NAME).getParent().foldOut();
    }
}
