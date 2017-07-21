package dk.trustworks.web.views;

import com.vaadin.annotations.Theme;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import dk.trustworks.network.clients.InvoiceClient;
import dk.trustworks.network.dto.InvoiceStatus;
import dk.trustworks.web.Broadcaster;
import org.springframework.beans.factory.annotation.Autowired;

import static dk.trustworks.network.dto.InvoiceStatus.DRAFT;

/**
 * Created by hans on 11/07/2017.
 */
@SpringComponent
@UIScope()
public class MainWindowImpl extends MainWindowDesign implements Broadcaster.BroadcastListener {

    private final InvoiceClient invoiceClient;
    private final ProjectListImpl projectList;
    private final InvoiceListImpl invoiceList;
    private final InvoiceStatusListImpl invoiceStatusList;

    @Autowired
    public MainWindowImpl(InvoiceClient invoiceClient, ProjectListImpl projectList, InvoiceListImpl invoiceList, InvoiceStatusListImpl invoiceStatusList) {
        this.invoiceClient = invoiceClient;
        this.projectList = projectList;
        this.invoiceList = invoiceList;
        this.invoiceStatusList = invoiceStatusList;

        Broadcaster.register(this);
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

        receiveBroadcast("");
    }

    private void clearButtonSelection() {
        menuButton1.removeStyleName("selected");
        menuButton2.removeStyleName("selected");
        menuButton3.removeStyleName("selected");
        menuButton4.removeStyleName("selected");
    }

    @Override
    public void receiveBroadcast(String message) {
        int numberOfDrafts = invoiceClient.findByStatus(DRAFT).getContent().size();
        if(numberOfDrafts>0) menuButton4.setCaption("DRAFTS ("+numberOfDrafts+")");
        else menuButton4.setCaption("DRAFTS");
    }
}
