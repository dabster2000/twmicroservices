package dk.trustworks.web.views;

import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.network.clients.ProjectSummaryClient;
import dk.trustworks.network.dto.Invoice;
import dk.trustworks.network.dto.InvoiceItem;
import dk.trustworks.network.dto.ProjectSummary;
import dk.trustworks.web.model.YearMonthSelect;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by hans on 10/07/2017.
 */

//@SpringComponent
//@UIScope
public class InvoiceListImplOld extends InvoiceListDesignOld {

    private final ProjectSummaryClient projectSummaryClient;

    @Autowired
    public InvoiceListImplOld(ProjectSummaryClient projectSummaryClient) {
        this.projectSummaryClient = projectSummaryClient;
        List<YearMonthSelect> yearMonthList = createYearMonthSelector();
        cbSelectYearMonth.setItems(yearMonthList);
        cbSelectYearMonth.setSelectedItem(yearMonthList.get(yearMonthList.size()-1));
        cbSelectYearMonth.addValueChangeListener(event -> {
            List<ProjectSummary> invoices = projectSummaryClient.loadProjectSummaryByYearAndMonth(event.getValue().getDate().getYear(), event.getValue().getDate().getMonthValue() - 1);
            //calculateAmountPerInvoice(invoices);
            //gridInvoiceList.setItems(invoices);
            gridInvoiceList.getDataProvider().refreshAll();
            Notification.show("Invoices loaded: ",
                    String.valueOf(invoices.size()),
                    Notification.Type.TRAY_NOTIFICATION);
            System.out.println("invoices.size() = " + invoices.size());
        });

        List<ProjectSummary> invoices = projectSummaryClient.loadProjectSummaryByYearAndMonth(yearMonthList.get(yearMonthList.size()-1).getDate().getYear(), yearMonthList.get(yearMonthList.size()-1).getDate().getMonthValue() - 1);
        //calculateAmountPerInvoice(invoices);

        //gridInvoiceList.setItems(invoices);
        gridInvoiceList.addColumn(Invoice::getClientname).setId("clientname").setCaption("Client name").setWidth(200);
        gridInvoiceList.addColumn(Invoice::getProjectname).setCaption("Project name").setWidth(300);
        gridInvoiceList.addColumn("errors").setCaption("Errors").setWidth(100);


        TextField nameFilter = new TextField();
        nameFilter.setStyleName(ValoTheme.TEXTFIELD_TINY);
        nameFilter.setPlaceholder("Filter");
        nameFilter.setWidth("100%");
        nameFilter.addValueChangeListener(this::onNameFilterTextChange);
        //addComponent(nameFilter);
        HeaderRow filteringHeader = gridInvoiceList.appendHeaderRow();
        filteringHeader.getCell("clientname")
                .setComponent(nameFilter);

        DecimalFormat dollarFormat = new DecimalFormat("kr #,##0.00");

        /*gridInvoiceList.addColumn(Invoice::getAmount, new NumberRenderer(dollarFormat))
                .setStyleGenerator(budgetHistory -> "align-right").setId("amount").setCaption("Total").setWidth(150);*/
        gridInvoiceList.setWidth(750, Unit.PIXELS);

        FooterRow footer = gridInvoiceList.appendFooterRow();
        // Update the summary row every time data has changed
        // by collecting the sum of each column's data
        gridInvoiceList.getDataProvider().addDataProviderListener(event -> {
            System.out.println("event.getSource() = " + event.getSource());
            List<Invoice> data = event.getSource().fetch(new Query<>()).collect(Collectors.toList());
            for (Invoice invoice : data) {
                System.out.println("invoice = " + invoice.projectname);
            }

            //Double firstHalfSum = data.stream().mapToDouble(i -> i.amount).sum();
            //footer.getCell("amount").setText(dollarFormat.format(firstHalfSum));
        });
        // Fire a data change event to initialize the summary footer
        gridInvoiceList.getDataProvider().refreshAll();

        gridInvoiceList.getSelectionModel().addSelectionListener(event -> {
            boolean somethingSelected = !gridInvoiceList.getSelectedItems().isEmpty();
            if (somethingSelected) {
                Invoice invoice = event.getFirstSelectedItem().get();
                InvoicePreview invoicePreview = new InvoicePreview();
                invoicePreview.lblClientname.setValue(invoice.clientname);
                invoicePreview.lblStreet.setValue(invoice.clientaddresse);
                invoicePreview.lblZipCity.setValue(invoice.zipcity);
                //invoicePreview.lblCvrEan.setValue(invoice.cvr_ean);
                invoicePreview.lblAttention.setValue(invoice.attention);
                invoicePreview.lblDescription.setValue(invoice.getDescription()+" ");
                invoicePreview.glInvoiceItems.removeAllComponents();
                invoicePreview.glInvoiceItems.setRows(invoice.invoiceitems.size());

                Locale locale = new Locale("dk", "DK");
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

                //DecimalFormat dollarFormat = new DecimalFormat("#.##0,00 kr");
                for (InvoiceItem invoiceitem : invoice.invoiceitems) {
                    Label lblConsultantName = new Label(getInitials(invoiceitem.description));
                    lblConsultantName.setSizeFull();
                    invoicePreview.glInvoiceItems.addComponent(lblConsultantName);
                    invoicePreview.glInvoiceItems.setComponentAlignment(lblConsultantName, Alignment.MIDDLE_LEFT);

                    Label lblConsultantAmount = new Label(currencyFormatter.format(invoiceitem.hours * invoiceitem.rate));
                    invoicePreview.glInvoiceItems.addComponent(lblConsultantAmount);
                    invoicePreview.glInvoiceItems.setComponentAlignment(lblConsultantAmount, Alignment.MIDDLE_RIGHT);
                }
                //invoicePreview.lblTotal.setValue(currencyFormatter.format(invoice.getAmount()) + "");

                invoicePreviewContainer.removeAllComponents();
                invoicePreviewContainer.setVisible(true);
                invoicePreviewContainer.addComponent(invoicePreview);

                invoicePreview.btnAction.addClickListener(event2 -> {
                    Notification.show("The button was clicked", Notification.Type.TRAY_NOTIFICATION);
                    final Window window = new Window("Invoice editor");
                    window.setWidth(700.0f, Unit.PIXELS);
                    window.setHeight(1000.0f, Unit.PIXELS);
                    window.setModal(true);
                    window.setContent(new InvoiceEditImpl(invoice, null));
                    this.getUI().getUI().addWindow(window);
                });
                //sample.getUI().getUI().addWindow(window);
            } else {
                invoicePreviewContainer.removeAllComponents();
                invoicePreviewContainer.setVisible(false);
            }
        });

    }

    private String getInitials(String name) {
        String[] splitStringArray = name.split(" ");
        StringBuilder builder = new StringBuilder(3);
        for(int i = 0; i < splitStringArray.length; i++)
        {
            builder.append(splitStringArray[i].substring(0,1));
            builder.append(".");
        }

        return builder.toString();
    }

    private void onNameFilterTextChange(HasValue.ValueChangeEvent<String> event) {
        ListDataProvider<Invoice> dataProvider = (ListDataProvider<Invoice>) gridInvoiceList.getDataProvider();
        dataProvider.setFilter(Invoice::getClientname, s -> caseInsensitiveContains(s, event.getValue()));
        gridInvoiceList.getDataProvider().refreshAll();
    }

    private Boolean caseInsensitiveContains(String where, String what) {
        return where.toLowerCase().contains(what.toLowerCase());
    }

    private void calculateAmountPerInvoice(List<Invoice> invoices) {
        for (Invoice invoice : invoices) {
            //invoice.amount = invoice.invoiceitems.stream().mapToDouble(i -> i.hours * i.rate).sum();
        }
    }

    private TextField getColumnFilterField() {
        TextField filter = new TextField();
        filter.setWidth("100%");
        filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filter.setPlaceholder("Filter");
        return filter;
    }


    private List<YearMonthSelect> createYearMonthSelector() {
        List<YearMonthSelect> yearMonthSelectList = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2014, 2, 1);
        while (startDate.isBefore(LocalDate.now())) {
            yearMonthSelectList.add(new YearMonthSelect(startDate));
            startDate = startDate.plusMonths(1);
        }

        return yearMonthSelectList;
    }
}



        /*
        ComboBox<YearMonthSelect> selYearMonth = new ComboBox<>("Select your country", yearMonthSelectList);
        selYearMonth.setEmptySelectionAllowed(false);

        selYearMonth.addValueChangeListener(event -> {
            invoices = invoiceClient.loadProjectSummaryByYearAndMonth(event.getValue().getDate().getYear(), event.getValue().getDate().getMonthValue()-1);
            Notification.show("Invoices loaded: ",
                    String.valueOf(invoices.size()),
                    Notification.Type.TRAY_NOTIFICATION);
            System.out.println("invoices.size() = " + invoices.size());
            if(invoices != null) {
                cssLayout.removeAllComponents();
                for (Invoice invoice : invoices) {
                    cssLayout.addComponent(new InvoicePreviewView(invoice));
                }
            }
        });
        */