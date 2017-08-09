package dk.trustworks.web.views;

import com.vaadin.annotations.Theme;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.NativeButton;
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
public class MainWindowImpl extends MainWindowDesign {

    public final InvoiceClient invoiceClient;
    private final ProjectListImpl projectList;
    private final InvoiceListImpl invoiceList;
    private final InvoiceStatusListImpl invoiceStatusList;

    public void init() {
        projectList.init();
    }

    @Autowired
    public MainWindowImpl(InvoiceClient invoiceClient, ProjectListImpl projectList, InvoiceListImpl invoiceList, InvoiceStatusListImpl invoiceStatusList) {
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
}
