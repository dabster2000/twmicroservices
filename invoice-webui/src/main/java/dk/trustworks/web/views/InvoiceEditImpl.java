package dk.trustworks.web.views;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.network.dto.Invoice;
import dk.trustworks.network.dto.InvoiceItem;
import dk.trustworks.network.dto.InvoiceStatus;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hans on 10/07/2017.
 */
public class InvoiceEditImpl extends InvoiceEditDesign {

    Map<Binder<InvoiceItem>, InvoiceItem> binders = new HashMap<>();
    Binder<Invoice> invoiceBinder;
    private Invoice invoice;

    public InvoiceEditImpl(Invoice invoice, RestTemplate restTemplate) {
        this.invoice = invoice;
        this.lblAttention.setValue(invoice.attention);
        this.lblClientname.setValue(invoice.clientname);
        if(invoice.cvr != null && invoice.cvr.trim().length() > 0) this.lblCvrEan.setValue(invoice.cvr);
        if(invoice.ean != null && invoice.ean.trim().length() > 0) this.lblCvrEan.setValue(invoice.ean);
        this.lblStreetname.setValue(invoice.clientaddresse);
        this.lblZipCity.setValue(invoice.zipcity);
        this.lblDescription.setValue(invoice.description);

        invoiceBinder = new Binder<>();
        invoiceBinder.forField(dfInvoiceDate).bind(Invoice::getInvoicedate, Invoice::setInvoicedate);
        invoiceBinder.readBean(invoice);
        createInvoiceItems();
    }

    private void createInvoiceItems() {
        List<InvoiceItem> invoiceItems = invoice.invoiceitems;
        binders.clear();
        for (int i = 1; i < gridInvoiceItems.getRows(); i++) {
            gridInvoiceItems.removeRow(i);
        }
        gridInvoiceItems.setRows(1);
        gridInvoiceItems.setRows(invoiceItems.size()+2);

        gridInvoiceItems.setColumnExpandRatio(0, 0.3f);
        gridInvoiceItems.setColumnExpandRatio(1, 0.35f);
        gridInvoiceItems.setColumnExpandRatio(2, 0.1f);
        gridInvoiceItems.setColumnExpandRatio(3, 0.1f);
        gridInvoiceItems.setColumnExpandRatio(4, 0.15f);

        int atRow = 1;
        for (InvoiceItem invoiceItem : invoiceItems) {
            createInvoiceLine(invoiceItem, atRow++);
        }

        Button btnAddInvoiceItem = new Button("add row");
        btnAddInvoiceItem.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        btnAddInvoiceItem.setIcon(FontAwesome.PLUS_SQUARE);
        gridInvoiceItems.addComponent(btnAddInvoiceItem);

        btnAddInvoiceItem.addClickListener(event -> {
            int row = gridInvoiceItems.getRows() - 1;
            InvoiceItem invoiceItem = new InvoiceItem("", "", 0.0, 0);
            invoiceItem.uuid = UUID.randomUUID().toString();
            invoiceItems.add(invoiceItem);
            gridInvoiceItems.insertRow(row);
            createInvoiceLine(invoiceItem, row);
            calcSums(invoiceItems);
        });

        calcSums(invoiceItems);
    }

    private void calcSums(List<InvoiceItem> invoiceItems) {
        double sumWithoutTax = invoiceItems.stream().mapToDouble(o -> o.hours * o.rate).sum();
        lblSumNoTax.setValue(sumWithoutTax+"");
        lblTax.setValue((sumWithoutTax*0.25)+"");
        lblSumWithTax.setValue((sumWithoutTax*1.25)+"");
    }

    private void createInvoiceLine(InvoiceItem invoiceItem, int atRow) {
        Binder<InvoiceItem> binder = new Binder<>();
        binders.put(binder, invoiceItem);
        TextField lblItemname = new TextField();
        lblItemname.setId(UUID.randomUUID().toString());
        lblItemname.addStyleName("tiny");
        lblItemname.setWidth(100.0f, Unit.PERCENTAGE);
        lblItemname.setValue(invoiceItem.itemname);
        binder.forField(lblItemname).bind(InvoiceItem::getItemname, InvoiceItem::setItemname);

        TextField lblDescription = new TextField();
        lblDescription.addStyleName("tiny");
        lblDescription.setWidth(100.0f, Unit.PERCENTAGE);
        lblDescription.setValue(invoiceItem.description);
        binder.forField(lblDescription).bind(InvoiceItem::getDescription, InvoiceItem::setDescription);

        Label lblAmount = new Label();
        lblAmount.setWidthUndefined();
        lblAmount.setValue((invoiceItem.hours * invoiceItem.rate) + "");

        TextField lblRate = new TextField();
        lblRate.addStyleName("tiny");
        lblRate.setValue(invoiceItem.rate + "");
        lblRate.setWidth(100.0f, Unit.PERCENTAGE);
        lblRate.addValueChangeListener(event -> {
            try {
                binder.writeBean(invoiceItem);
                calcSums(invoice.invoiceitems);
                lblAmount.setValue((invoiceItem.hours * invoiceItem.rate) + "");
            } catch (ValidationException e) {
                e.printStackTrace();
                lblRate.setValue(event.getOldValue());
            }
        });
        binder.forField(lblRate)
                .withConverter(new StringToDoubleConverter("Must enter a number"))
                .bind(InvoiceItem::getRate, InvoiceItem::setRate);

        TextField lblHours = new TextField();
        lblHours.addStyleName("tiny");
        lblHours.setValue(invoiceItem.hours + "");
        lblHours.setWidth(100.0f, Unit.PERCENTAGE);
        lblHours.addValueChangeListener(event -> {
            try {
                binder.writeBean(invoiceItem);
                calcSums(invoice.invoiceitems);
                lblAmount.setValue((invoiceItem.hours * invoiceItem.rate) + "");
            } catch (ValidationException e) {
                e.printStackTrace();
                lblHours.setValue(event.getOldValue());
            }
        });
        binder.forField(lblHours)
                .withConverter(new StringToDoubleConverter("Must enter a number"))
                .bind(InvoiceItem::getHours, InvoiceItem::setHours);

        gridInvoiceItems.addComponent(lblItemname, 0, atRow);
        gridInvoiceItems.addComponent(lblDescription, 1, atRow);
        gridInvoiceItems.addComponent(lblRate, 2, atRow);
        gridInvoiceItems.addComponent(lblHours, 3, atRow);
        gridInvoiceItems.addComponent(lblAmount, 4, atRow);
        gridInvoiceItems.setComponentAlignment(lblAmount, Alignment.MIDDLE_RIGHT);
    }
}
