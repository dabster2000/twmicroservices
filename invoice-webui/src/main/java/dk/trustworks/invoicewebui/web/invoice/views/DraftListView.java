package dk.trustworks.invoicewebui.web.invoice.views;

import com.vaadin.annotations.Push;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.repositories.InvoiceRepository;
import dk.trustworks.invoicewebui.web.Broadcaster;
import dk.trustworks.invoicewebui.web.invoice.components.DraftListImpl;
import dk.trustworks.invoicewebui.web.mainmenu.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static dk.trustworks.invoicewebui.model.InvoiceStatus.DRAFT;

/**
 * Created by hans on 11/07/2017.
 */
@Push
@SpringView(name = DraftListView.VIEW_NAME)
public class DraftListView extends VerticalLayout implements View, Broadcaster.BroadcastListener {

    public static final String VIEW_NAME = "invoice_drafts";
    public static final String MENU_NAME = "Drafts";

    protected static Logger logger = LoggerFactory.getLogger(DraftListView.class.getName());

    @Autowired
    private DraftListImpl draftListComponent;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private LeftMenu leftMenu;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        Broadcaster.register(this);
        mainTemplate.setMainContent(draftListComponent);
    }

    @Override
    public void receiveBroadcast(String message) {
        logger.info("MainWindowImpl.receiveBroadcast");
        logger.info("message = " + message);
/*
        int numberOfDrafts = invoiceRepository.findByStatus(DRAFT).size();
        if(this.getUI() != null) this.getUI().access(() -> {
            draftListComponent.loadInvoicesToGrid();
            if(numberOfDrafts>0) leftMenu.getMenuItems().get(VIEW_NAME).getMenuItem().getComponent().setCaption(MENU_NAME+" ("+numberOfDrafts+")");
            else leftMenu.getMenuItems().get(VIEW_NAME).getMenuItem().getComponent().setCaption(MENU_NAME);
        });
*/
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //Authorizer.authorize(this);
        draftListComponent.loadInvoicesToGrid();
        leftMenu.getMenuItems().get(VIEW_NAME).getParent().foldOut();
    }
}
