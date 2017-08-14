package dk.trustworks.invoicewebui.web.invoice.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.NativeButton;
import dk.trustworks.invoicewebui.web.invoice.components.InvoiceListImpl;
import dk.trustworks.invoicewebui.network.clients.InvoiceClient;
import dk.trustworks.invoicewebui.web.Broadcaster;
import dk.trustworks.invoicewebui.web.invoice.components.InvoiceStatusListImpl;
import dk.trustworks.invoicewebui.web.invoice.components.ProjectListImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static dk.trustworks.invoicewebui.network.dto.InvoiceStatus.DRAFT;

/**
 * Created by hans on 11/07/2017.
 */
@SpringView(name = MainWindowImpl.VIEW_NAME)
public class MainWindowImpl extends MainWindowDesign implements View, Broadcaster.BroadcastListener {

    public static final String VIEW_NAME = "invoice";

    public final InvoiceClient invoiceClient;
    private final ProjectListImpl projectList;
    private final InvoiceListImpl invoiceList;
    private final InvoiceStatusListImpl invoiceStatusList;

    @PostConstruct
    public void init() {
        Broadcaster.register(this);
        projectList.init();
    }

    @Autowired
    public MainWindowImpl(InvoiceClient invoiceClient, ProjectListImpl projectList, InvoiceListImpl invoiceList, InvoiceStatusListImpl invoiceStatusList) {
        System.out.println("MainWindowImpl.MainWindowImpl");
        System.out.println("invoiceClient = [" + invoiceClient + "], projectList = [" + projectList + "], invoiceList = [" + invoiceList + "], invoiceStatusList = [" + invoiceStatusList + "]");
        this.invoiceClient = invoiceClient;
        this.projectList = projectList;
        this.invoiceList = invoiceList;
        this.invoiceStatusList = invoiceStatusList;

        System.out.println("MainWindowImpl.MainWindowImpl");
        page_content.addComponent(this.projectList);

        menuButton1.addClickListener(event -> {
            clearButtonSelection();
            menuButton1.addStyleName("selected");
            page_content.removeAllComponents();
            page_content.addComponent(this.projectList);
            projectList.reloadData();
        });

        menuButton4.addClickListener(event -> {
            clearButtonSelection();
            menuButton4.addStyleName("selected");
            page_content.removeAllComponents();
            page_content.addComponent(this.invoiceList);
            invoiceList.loadInvoicesToGrid();
        });

        menuButton2.addClickListener(event -> {
            clearButtonSelection();
            menuButton2.addStyleName("selected");
            page_content.removeAllComponents();
            page_content.addComponent(this.invoiceStatusList);
            invoiceStatusList.loadInvoicesToGrid();
        });


    }

    private void clearButtonSelection() {
        menuButton1.removeStyleName("selected");
        menuButton2.removeStyleName("selected");
        menuButton3.removeStyleName("selected");
        menuButton4.removeStyleName("selected");
    }

    public NativeButton getMenuButton4() {
        return menuButton4;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }

    @Override
    public void receiveBroadcast(String message) {
        System.out.println("MainWindowImpl.receiveBroadcast");
        System.out.println("message = " + message);
        int numberOfDrafts = invoiceClient.findByStatus(DRAFT).getContent().size();
        System.out.println("numberOfDrafts = " + numberOfDrafts);
        getUI().access(() -> {
            projectList.reloadData();
            if(numberOfDrafts>0) getMenuButton4().setCaption("DRAFTS ("+numberOfDrafts+")");
            else getMenuButton4().setCaption("DRAFTS");
        });

    }
}
