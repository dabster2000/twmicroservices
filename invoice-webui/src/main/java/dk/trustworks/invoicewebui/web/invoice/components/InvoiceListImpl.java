package dk.trustworks.invoicewebui.web.invoice.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.annotations.Push;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.invoicewebui.model.ExpenseDetails;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.model.InvoiceItem;
import dk.trustworks.invoicewebui.model.InvoiceReference;
import dk.trustworks.invoicewebui.model.enums.InvoiceType;
import dk.trustworks.invoicewebui.network.dto.enums.EconomicAccountGroup;
import dk.trustworks.invoicewebui.services.*;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.simplefiledownloader.SimpleFileDownloader;
import org.vaadin.viritin.label.MLabel;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.model.enums.InvoiceStatus.*;

/**
 * Created by hans on 13/07/2017.
 */

@SpringComponent
@SpringUI
@Push
public class InvoiceListImpl extends InvoiceListDesign {

    private final InvoiceService invoiceService;

    private final ClientService clientService;

    private final ProjectService projectService;

    private final FinanceService financeService;

    private final PhotoService photoService;

    private ListDataProvider<Invoice> dataProvider;

    @Autowired
    public InvoiceListImpl(InvoiceService invoiceService, ClientService clientService, ProjectService projectService, FinanceService financeService, PhotoService photoService) {
        this.invoiceService = invoiceService;
        this.clientService = clientService;
        this.projectService = projectService;
        this.financeService = financeService;
        this.photoService = photoService;
        createInvoiceTable();
        createDisconnectedInvoiceList();
    }

    public void createInvoiceTable() {
        loadInvoicesToGrid();
        btnCreateCreditNota.setVisible(false);
        btnRecreateInvoice.setVisible(false);
        btnViewPdf.setVisible(false);
        btnTour.setVisible(false);
        gridInvoiceList.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridInvoiceList.getEditor().setBuffered(true).setEnabled(true);
        gridInvoiceList.addSelectionListener(selectionEvent -> {
            if(gridInvoiceList.getSelectionModel().getFirstSelectedItem().isPresent()) {
                btnDownloadPdf.setEnabled(true);
            } else {
                btnViewPdf.setEnabled(false);
                btnRecreateInvoice.setEnabled(false);
                btnDownloadPdf.setEnabled(false);
                btnCreateCreditNota.setEnabled(false);
            }
        });

        gridInvoiceList.sort("invoicenumber", SortDirection.DESCENDING);

        HeaderRow filteringHeader = gridInvoiceList.appendHeaderRow();

        gridInvoiceList.addComponentColumn(invoice -> {
            if (invoice.getType().equals(InvoiceType.CREDIT_NOTE)) {
                return new MLabel("CREDIT NOTE").withStyleName("orange");
            } else {
                return new MLabel("INVOICE").withStyleName("");
            }
        }).setId("type").setCaption("Invoice Type");

        gridInvoiceList.setColumnOrder("clientname","projectname","invoicenumber","referencenumber","invoicedate","type","sumNoTax","sumWithTax");

        TextField filteringField2 = getColumnFilterField();
        filteringField2.addValueChangeListener(event -> {
            dataProvider.setFilter(Invoice::getType, invoiceType -> {
                if (invoiceType == null) {
                    return false;
                }
                String companyLower = invoiceType.name().toLowerCase(Locale.ENGLISH);
                String filterLower = event.getValue().toLowerCase(Locale.ENGLISH);
                return companyLower.contains(filterLower);
            });
        });
        filteringHeader.getCell("type").setComponent(filteringField2);

        Grid.Column invoiceNumber = gridInvoiceList.getColumn("invoicenumber");
        invoiceNumber.setStyleGenerator(item -> "v-align-right");

        Grid.Column sumNoTax = gridInvoiceList.getColumn("sumNoTax");
        sumNoTax.setRenderer(new NumberRenderer(NumberConverter.getCurrencyInstance()));
        sumNoTax.setStyleGenerator(item -> "v-align-right");

        Grid.Column sumWithTax = gridInvoiceList.getColumn("sumWithTax");
        sumWithTax.setRenderer(new NumberRenderer(NumberConverter.getCurrencyInstance()));
        sumWithTax.setStyleGenerator(item -> "v-align-right");

        FooterRow footer = gridInvoiceList.appendFooterRow();
        footer.setStyleName("bold");
        FooterCell joinedFooterCell = footer.join("clientname", "projectname", "invoicenumber", "referencenumber", "invoicedate", "type");
        joinedFooterCell.setText("Total: ");
        joinedFooterCell.setStyleName("v-align-right");
        dataProvider.addDataProviderListener(event -> {
            double sum = event.getSource()
                    .fetch(new Query<>())
                    .filter(invoice -> invoice.type != InvoiceType.CREDIT_NOTE || invoice.status != CREDIT_NOTE)
                    .mapToDouble(Invoice::getSumNoTax).sum();
            FooterCell sumNoTaxFooter = footer.getCell("sumNoTax");
            sumNoTaxFooter.setText(NumberConverter.formatCurrency(sum));
            sumNoTaxFooter.setStyleName("v-align-right");

            FooterCell sumWithTaxFooter = footer.getCell("sumWithTax");
            sumWithTaxFooter.setText(NumberConverter.formatCurrency(sum*1.25));
            sumWithTaxFooter.setStyleName("v-align-right");
        });
        // Fire a data change event to initialize the summary footer

        btnDownloadPdf.addClickListener(clickEvent -> {
            if(!gridInvoiceList.getSelectionModel().getFirstSelectedItem().isPresent()) return;
            Invoice invoice = gridInvoiceList.getSelectionModel().getFirstSelectedItem().get();

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            final StreamResource resource = new StreamResource(() ->
                    new ByteArrayInputStream(invoice.getPdf()),
                    invoice.invoicenumber +
                            "_" + invoice.type +
                            "-" + invoice.clientname +
                            "-" + dateTimeFormatter.format(invoice.invoicedate) +
                            ".pdf"
            );

            SimpleFileDownloader downloader = new SimpleFileDownloader();
            addExtension(downloader);
            downloader.setFileDownloadResource(resource);
            downloader.download();
        });

        btnViewPdf.setVisible(false);

        gridInvoiceList.getDataProvider().refreshAll();

    }

    private TextField getColumnFilterField() {
        TextField filter = new TextField();
        filter.setWidth("100%");
        filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filter.setPlaceholder("Filter");
        return filter;
    }

    public void loadInvoicesToGrid() {
        List<Invoice> invoices = invoiceService.findAll().stream().filter(
                invoice -> invoice.status.equals(CREATED) ||
                        invoice.status.equals(SUBMITTED) ||
                        invoice.status.equals(PAID) ||
                        invoice.status.equals(CREDIT_NOTE)
        ).collect(Collectors.toList()); //.findByStatusIn(CREATED, SUBMITTED, PAID, CREDIT_NOTE);
        dataProvider = DataProvider.ofCollection(calcTotal(invoices));
        gridInvoiceList.setDataProvider(dataProvider);
        gridInvoiceList.getDataProvider().refreshAll();
    }

    public void createDisconnectedInvoiceList() {
        ResponsiveLayout layout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        vlNotConnected.addComponent(new BoxImpl().instance(layout));

        LocalDate startDate = LocalDate.now().isAfter(LocalDate.of(LocalDate.now().getYear(), 1,1))?DateUtils.getCurrentFiscalStartDate():DateUtils.getCurrentFiscalStartDate().minusMonths(6);

        ResponsiveRow rowHeaders = layout.addRow();
        rowHeaders.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("").withStyleName("large bold"));
        rowHeaders.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("#").withStyleName("large bold"));
        rowHeaders.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("Date").withStyleName("large bold"));
        rowHeaders.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("Type").withStyleName("large bold"));
        rowHeaders.addColumn().withDisplayRules(2,2,2,2).withComponent(new MLabel("Client").withStyleName("large bold"));
        rowHeaders.addColumn().withDisplayRules(3,3,3,3).withComponent(new MLabel("Project").withStyleName("large bold"));
        rowHeaders.addColumn().withDisplayRules(1,1,1,1).withComponent(new MLabel("Amount").withStyleName("large bold"));
        rowHeaders.addColumn().withDisplayRules(2,2,2,2).withComponent(new MLabel("").withStyleName("large bold"));

        List<Invoice> invoices = invoiceService.findByPeriod(startDate, LocalDate.now());

        do {
            LocalDate finalStartDate = startDate;
            for (Invoice invoice : invoices.stream().filter(i -> i.getInvoicedate().withDayOfMonth(1).isEqual(finalStartDate.withDayOfMonth(1))).collect(Collectors.toList())) {
                if(invoice.getReferencenumber()>0 || invoice.getStatus().equals(DRAFT) || invoice.getStatus().equals(CANCELLED)) continue;
                ResponsiveRow row = layout.addRow().withAlignment(Alignment.MIDDLE_LEFT);
                row.addLayoutClickListener(event -> {
                    //if(event.getButton().) {
                        Notification.show("",
                                invoice.getInvoiceitems().stream().map(i -> i.itemname+" "+i.description+" "+i.hours+" hours at "+i.rate+" kr").collect(Collectors.joining("\n")),
                                Notification.Type.HUMANIZED_MESSAGE);
                    //}
                });
                row.addColumn().withDisplayRules(1, 1, 1, 1).withComponent(photoService.getRoundMemberImage(projectService.findOne(invoice.getProjectuuid()).getClient().getAccountmanager(), 0, 50, Unit.PIXELS));
                row.addColumn().withDisplayRules(1, 1, 1, 1).withComponent(new Label(invoice.getInvoicenumber()+""));
                row.addColumn().withDisplayRules(1, 1, 1, 1).withComponent(new Label(DateUtils.stringIt(invoice.getInvoicedate())));
                row.addColumn().withDisplayRules(1, 1, 1, 1).withComponent(new Label(invoice.getStatus()+""));
                row.addColumn().withDisplayRules(2, 2, 2, 2).withComponent(new Label(invoice.getClientname()));
                row.addColumn().withDisplayRules(3, 3, 3, 3).withComponent(new Label(invoice.getProjectname()));
                row.addColumn().withDisplayRules(1, 1, 1, 1).withComponent(new Label(NumberConverter.formatCurrency(getSumWithoutTax(invoice.getInvoiceitems()))));

                ComboBox<ExpenseDetails> referenceComboBox = new ComboBox<>();
                referenceComboBox.setEnabled(invoice.getInvoicenumber()==0);

                List<ExpenseDetails> expenseDetails = financeService.findExpenseDetailsByGroup(EconomicAccountGroup.OMSAETNING_ACCOUNTS).stream().filter(e -> e.getAmount() == -getSumWithoutTax(invoice.getInvoiceitems())).collect(Collectors.toList());
                referenceComboBox.setItems(expenseDetails);
                referenceComboBox.setItemCaptionGenerator(item -> item.getInvoicenumber()+" | "+item.getExpensedate()+" | "+item.getAmount()+" | "+item.getText());
                referenceComboBox.addValueChangeListener(event -> {
                    invoiceService.updateInvoiceReference(invoice.getUuid(), new InvoiceReference(event.getValue().getExpensedate(), event.getValue().getEntrynumber()));
                    referenceComboBox.setEnabled(false);
                });
                if(expenseDetails.size()==0) row.addColumn().withDisplayRules(2, 2, 2, 2).withComponent(new MLabel("Invoice missing").withStyleName("small bold"));
                else row.addColumn().withDisplayRules(2, 2, 2, 2).withComponent(referenceComboBox);
            }
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(LocalDate.now()));
    }

    private List<Invoice> calcTotal(List<Invoice> invoices) {
        for (Invoice invoice : invoices) {
            invoice.setSumNoTax(0.0);
            for (InvoiceItem invoiceitem : invoice.invoiceitems) {
                invoice.addToSumNoTax(invoiceitem.getHours() * invoiceitem.getRate());
            }
        }
        return invoices;
    }

    private static double getSumWithoutTax(Set<InvoiceItem> invoiceItems) {
        return invoiceItems.stream().mapToDouble(o -> o.hours * o.rate).sum();
    }

}
