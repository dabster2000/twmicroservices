package dk.trustworks.invoicewebui.web.invoice.components;

import com.kbdunn.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.data.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.model.InvoiceItem;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by hans on 10/07/2017.
 */
public class DraftEditImpl extends DraftEditDesign {

    Map<Binder<InvoiceItem>, InvoiceItem> binders = new HashMap<>();
    Binder<Invoice> invoiceBinder;
    private Invoice invoice;

    public DraftEditImpl(Invoice invoice) {
        this.invoice = invoice;
        this.lblInvoiceHeadline.setValue(invoice.getType().name());
        this.lblProjectReference.setValue(invoice.projectref);
        this.lblContractReference.setValue(invoice.contractref);

        invoiceBinder = new Binder<>();
        invoiceBinder.forField(txtAttention).bind(Invoice::getAttention, Invoice::setAttention);
        invoiceBinder.forField(txtClientname).bind(Invoice::getClientname, Invoice::setClientname);
        invoiceBinder.forField(txtCvr).bind(Invoice::getCvr, Invoice::setCvr);
        invoiceBinder.forField(txtEan).bind(Invoice::getEan, Invoice::setEan);
        invoiceBinder.forField(txtStreetname).bind(Invoice::getClientaddresse, Invoice::setClientaddresse);
        invoiceBinder.forField(txtZipCity).bind(Invoice::getZipcity, Invoice::setZipcity);
        invoiceBinder.forField(dfInvoiceDate).bind(Invoice::getInvoicedate, Invoice::setInvoicedate);
        invoiceBinder.forField(txtSpecificDescription).bind(Invoice::getSpecificdescription, Invoice::setSpecificdescription);
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

        NumberFormat currencyFormatter = NumberFormat.getInstance(Locale.getDefault());
        currencyFormatter.setMaximumFractionDigits(2);
        currencyFormatter.setMinimumFractionDigits(2);

        lblSumNoTax.setValue(currencyFormatter.format(sumWithoutTax));
        lblTax.setValue(currencyFormatter.format(sumWithoutTax*0.25));
        lblSumWithTax.setValue(currencyFormatter.format(sumWithoutTax*1.25));
    }

    private void createInvoiceLine(InvoiceItem invoiceItem, int atRow) {
        Binder<InvoiceItem> binder = new Binder<>();
        binders.put(binder, invoiceItem);
        TextField lblItemname = new TextField();
        lblItemname.setId(UUID.randomUUID().toString());
        lblItemname.addStyleName("tiny");
        lblItemname.setWidth(100.0f, Unit.PERCENTAGE);
        binder.forField(lblItemname).bind(InvoiceItem::getItemname, InvoiceItem::setItemname);

        TextField lblDescription = new TextField();
        lblDescription.addStyleName("tiny");
        lblDescription.setWidth(100.0f, Unit.PERCENTAGE);
        binder.forField(lblDescription).bind(InvoiceItem::getDescription, InvoiceItem::setDescription);

        Label lblAmount = new Label();
        //lblAmount.setBoxWidth(100.0f, Unit.PERCENTAGE);
        NumberFormat danishNumberFormatter = NumberFormat.getInstance(Locale.getDefault());
        danishNumberFormatter.setMaximumFractionDigits(2);
        danishNumberFormatter.setMinimumFractionDigits(2);
        lblAmount.setValue(String.valueOf(danishNumberFormatter.format((invoiceItem.hours * invoiceItem.rate))));
        lblAmount.setWidthUndefined();

        TextField lblRate = new TextField();
        lblRate.addStyleName("tiny");
        lblRate.setWidth(100.0f, Unit.PERCENTAGE);

        binder.forField(lblRate)
                .withConverter(new MyConverter())
                .bind(InvoiceItem::getRate, InvoiceItem::setRate);

        TextField lblHours = new TextField();
        lblHours.addStyleName("tiny");
        lblHours.setWidth(100.0f, Unit.PERCENTAGE);

        binder.forField(lblHours)
                .withConverter(new MyConverter())
                //.withConverter(new StringToDoubleConverter("Must enter a number"))
                .bind(InvoiceItem::getHours, InvoiceItem::setHours);

        gridInvoiceItems.addComponent(lblItemname, 0, atRow);
        gridInvoiceItems.addComponent(lblDescription, 1, atRow);
        gridInvoiceItems.addComponent(lblRate, 2, atRow);
        gridInvoiceItems.addComponent(lblHours, 3, atRow);
        gridInvoiceItems.addComponent(lblAmount, 4, atRow);
        gridInvoiceItems.setComponentAlignment(lblAmount, Alignment.MIDDLE_RIGHT);
        binder.readBean(invoiceItem);

        lblRate.addValueChangeListener(event -> {
            try {
                binder.writeBean(invoiceItem);
                calcSums(invoice.invoiceitems);
                danishNumberFormatter.setMaximumFractionDigits(2);
                danishNumberFormatter.setMinimumFractionDigits(2);
                lblAmount.setValue(String.valueOf(danishNumberFormatter.format((invoiceItem.hours * invoiceItem.rate))));
            } catch (ValidationException e) {
                e.printStackTrace();
                lblRate.setValue(event.getOldValue());
            }
        });
        lblHours.addValueChangeListener(event -> {
            try {
                binder.writeBean(invoiceItem);
                calcSums(invoice.invoiceitems);
                danishNumberFormatter.setMaximumFractionDigits(2);
                danishNumberFormatter.setMinimumFractionDigits(2);
                lblAmount.setValue(String.valueOf(danishNumberFormatter.format((invoiceItem.hours * invoiceItem.rate))));
            } catch (ValidationException e) {
                e.printStackTrace();
                lblHours.setValue(event.getOldValue());
            }
        });
    }

    public class MyConverter implements Converter<String, Double> {
        @Override
        public Result<Double> convertToModel(String fieldValue, ValueContext context) {
            System.out.println("MyConverter.convertToModel");
            System.out.println("fieldValue = [" + fieldValue + "], context = [" + context + "]");
            // Produces a converted value or an error
            try {
                // ok is a static helper method that creates a Result
                NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
                return Result.ok(formatter.parse(fieldValue).doubleValue());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // error is a static helper method that creates a Result
                return Result.error("Please enter a number");
            } catch (ParseException e) {
                e.printStackTrace();
                return Result.error("Please enter a number");
            }
        }

        @Override
        public String convertToPresentation(Double aDouble, ValueContext context) {
            System.out.println("MyConverter.convertToPresentation");
            System.out.println("aDouble = [" + aDouble + "], context = [" + context + "]");
            // Converting to the field type should always succeed,
            // so there is no support for returning an error Result.
            NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            return String.valueOf(formatter.format(aDouble));
        }
    }

    public void setSpecificDescription(String description) {
        txtSpecificDescription.setValue(description);
    }

}
