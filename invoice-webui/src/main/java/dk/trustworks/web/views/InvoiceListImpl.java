package dk.trustworks.web.views;

import com.vaadin.annotations.Push;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.LocalDateRenderer;
import dk.trustworks.network.clients.InvoiceClient;
import dk.trustworks.network.dto.Invoice;
import dk.trustworks.network.dto.InvoiceItem;
import dk.trustworks.network.dto.InvoiceStatus;
import dk.trustworks.web.Broadcaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Set;

import static dk.trustworks.network.dto.InvoiceStatus.DRAFT;

/**
 * Created by hans on 13/07/2017.
 */

@SpringComponent
@SpringUI
@Push
public class InvoiceListImpl extends InvoiceListDesign
        implements Broadcaster.BroadcastListener {

    private final InvoiceClient invoiceClient;

    private final RestTemplate restTemplate;

    @Autowired
    public InvoiceListImpl(InvoiceClient invoiceClient, RestTemplate restTemplate) {
        Broadcaster.register(this);
        this.restTemplate = restTemplate;
        System.out.println("InvoiceListImpl.InvoiceListImpl");
        this.invoiceClient = invoiceClient;

        loadInvoicesToGrid();

        gridInvoiceList.setSelectionMode(Grid.SelectionMode.MULTI);

        Grid.Column invoicedateColumn = gridInvoiceList.getColumn("invoicedate");
        DateTimeFormatter localDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        invoicedateColumn.setRenderer(new LocalDateRenderer(localDateFormatter));

        gridInvoiceList.addItemClickListener(event -> {
            if(event.getMouseEventDetails().isDoubleClick()) {
                final Window window = new Window("Invoice editor");
                window.setWidth(700.0f, Unit.PIXELS);
                window.setHeight(1000.0f, Unit.PIXELS);
                window.setModal(true);

                Invoice invoice = event.getItem();

                InvoiceEditImpl invoiceEdit = new InvoiceEditImpl(invoice, restTemplate);

                invoiceEdit.btnCreateInvoice.addClickListener(clickEvent -> {
                    try {
                        saveFormToInvoiceBean(invoice, invoiceEdit);
                        invoice.setStatus(InvoiceStatus.CREATED);
                        restTemplate.patchForObject(invoice.getLink("self").getHref(), invoice, String.class);
                        window.close();
                    } catch (ValidationException e) {
                        e.printStackTrace();
                    }
                });

                invoiceEdit.btnSave.addClickListener(clickEvent -> {
                    try {
                        saveFormToInvoiceBean(invoice, invoiceEdit);
                        restTemplate.patchForObject(invoice.getLink("self").getHref(), invoice, String.class);
                    } catch (ValidationException e) {
                        Notification.show("Invoice could not be saved, " +
                                "please check error messages for each field.");
                    }
                    window.close();
                });

                window.setContent(invoiceEdit);
                this.getUI().addWindow(window);
            }
        });

        btnDelete.addClickListener(event -> {
            for (Invoice selectedInvoice : gridInvoiceList.getSelectedItems()) {
                invoiceClient.deleteInvoice(selectedInvoice.getUuid());
            }
        });
    }

    public void saveFormToInvoiceBean(Invoice invoice, InvoiceEditImpl invoiceEdit) throws ValidationException {
        for (Binder<InvoiceItem> binder : invoiceEdit.binders.keySet()) {
            binder.writeBean(invoiceEdit.binders.get(binder));
        }
        invoiceEdit.invoiceBinder.writeBean(invoice);
        invoice.getInvoiceitems().removeIf(invoiceItem -> invoiceItem.itemname.trim().length() == 0);
    }

    @Override
    public void receiveBroadcast(final String message) {
        if(this.getUI() != null) {
            this.getUI().access(this::loadInvoicesToGrid);
        }
    }

    public void loadInvoicesToGrid() {
        Resources<Invoice> invoices = invoiceClient.findByStatus(DRAFT);
        gridInvoiceList.setItems(invoices.getContent());
        gridInvoiceList.getDataProvider().refreshAll();
    }
}
