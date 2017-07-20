package dk.trustworks.web.views;

import com.vaadin.annotations.Push;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.network.clients.InvoiceClient;
import dk.trustworks.network.clients.ProjectSummaryClient;
import dk.trustworks.network.dto.*;
import dk.trustworks.web.Broadcaster;
import dk.trustworks.web.model.SubTotal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.vaadin.simplefiledownloader.SimpleFileDownloader;

import java.io.ByteArrayInputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static dk.trustworks.network.dto.InvoiceStatus.*;

/**
 * Created by hans on 13/07/2017.
 */

@SpringComponent
@SpringUI
@Push
public class InvoiceStatusListImpl extends InvoiceStatusListDesign
        implements Broadcaster.BroadcastListener {

    private final InvoiceClient invoiceClient;

    private final ProjectSummaryClient projectSummaryClient;

    private ListDataProvider<Invoice> dataProvider;

    private Resources<Invoice> invoices;

    private NumberFormat kronerFormat;

    @Autowired
    public InvoiceStatusListImpl(InvoiceClient invoiceClient, ProjectSummaryClient projectSummaryClient) {
        this.projectSummaryClient = projectSummaryClient;
        this.invoiceClient = invoiceClient;

        Broadcaster.register(this);

        kronerFormat = NumberFormat.getCurrencyInstance();

        createInvoiceTable();

        createSubTotalsGrid();
    }

    public void createSubTotalsGrid() {
        List<Invoice> invoiceList = dataProvider
                .fetch(new Query<>())
                .filter(invoice -> invoice.type != InvoiceType.CREDIT_NOTE && invoice.status != CREDIT_NOTE)
                .collect(Collectors.toList());
        Map<Integer, SubTotal> subTotalMap = new HashMap<>();
        for (Invoice invoice : invoiceList) {
            LocalDate invoicedate = invoice.getInvoicedate();
            if(!subTotalMap.containsKey(invoicedate.getYear())) subTotalMap.put(invoicedate.getYear(), new SubTotal(invoicedate.getYear()));
            SubTotal subTotal = subTotalMap.get(invoicedate.getYear());
            switch (invoicedate.getMonthValue()) {
                case 1:
                    subTotal.jan += invoice.getSumNoTax();
                    break;
                case 2:
                    subTotal.feb += invoice.getSumNoTax();
                    break;
                case 3:
                    subTotal.mar += invoice.getSumNoTax();
                    break;
                case 4:
                    subTotal.apr += invoice.getSumNoTax();
                    break;
                case 5:
                    subTotal.may += invoice.getSumNoTax();
                    break;
                case 6:
                    subTotal.jun += invoice.getSumNoTax();
                    break;
                case 7:
                    subTotal.jul += invoice.getSumNoTax();
                    break;
                case 8:
                    subTotal.aug += invoice.getSumNoTax();
                    break;
                case 9:
                    subTotal.sep += invoice.getSumNoTax();
                    break;
                case 10:
                    subTotal.oct += invoice.getSumNoTax();
                    break;
                case 11:
                    subTotal.nov += invoice.getSumNoTax();
                    break;
                case 12:
                    subTotal.dec += invoice.getSumNoTax();
                    break;
            }
        }
        ListDataProvider<SubTotal> dataProviderSubTotals = DataProvider.ofCollection(subTotalMap.values());
        gridSubTotals.setDataProvider(dataProviderSubTotals);
        for (Grid.Column subTotalColumn : gridSubTotals.getColumns()) {
            if(subTotalColumn.getId().equals("year")) continue;
            subTotalColumn.setStyleGenerator(item -> "v-align-right");
            subTotalColumn.setRenderer(new NumberRenderer(kronerFormat));
        }

        gridSubTotals.getDataProvider().refreshAll();
        gridSubTotals.sort("year", SortDirection.DESCENDING);
        gridSubTotals.setFrozenColumnCount(1);
    }

    public void createInvoiceTable() {
        loadInvoicesToGrid();
        gridInvoiceList.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridInvoiceList.getEditor().setBuffered(true).setEnabled(true);
        gridInvoiceList.addSelectionListener(selectionEvent -> {
            if(gridInvoiceList.getSelectionModel().getFirstSelectedItem().isPresent()) {
                btnDownloadPdf.setEnabled(true);
                btnCreateCreditNota.setEnabled(true);
                if(gridInvoiceList.getSelectionModel().getFirstSelectedItem().get().status.equals(CREDIT_NOTE)
                        || gridInvoiceList.getSelectionModel().getFirstSelectedItem().get().type.equals(InvoiceType.CREDIT_NOTE)) {
                    btnCreateCreditNota.setEnabled(false);
                }
            } else {
                btnDownloadPdf.setEnabled(false);
                btnCreateCreditNota.setEnabled(false);
            }
        });

        gridInvoiceList.sort("invoicenumber", SortDirection.DESCENDING);

        ArrayList<InvoiceStatus> statuses = new ArrayList<>(Arrays.asList(InvoiceStatus.values()));
        statuses.remove(InvoiceStatus.DRAFT);
        statuses.remove(InvoiceStatus.CREDIT_NOTE);
        statuses.remove(InvoiceStatus.CANCELLED);

        HeaderRow filteringHeader = gridInvoiceList.appendHeaderRow();
        TextField filteringField = getColumnFilterField();
        filteringField.addValueChangeListener(event -> {
            dataProvider.setFilter(Invoice::getStatus, invoiceStatus -> {
                if (invoiceStatus == null) {
                    return false;
                }
                String companyLower = invoiceStatus.name().toLowerCase(Locale.ENGLISH);
                String filterLower = event.getValue().toLowerCase(Locale.ENGLISH);
                return companyLower.contains(filterLower);
            });
        });
        filteringHeader.getCell("status").setComponent(filteringField);

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

        ComboBox<InvoiceStatus> comboBox = new ComboBox<>("Change status", statuses);
        comboBox.setEmptySelectionAllowed(false);
        gridInvoiceList.getColumn("status").setEditorComponent(comboBox);

        Grid.Column invoiceNumber = gridInvoiceList.getColumn("invoicenumber");
        invoiceNumber.setStyleGenerator(item -> "v-align-right");

        Grid.Column sumNoTax = gridInvoiceList.getColumn("sumNoTax");
        sumNoTax.setRenderer(new NumberRenderer(kronerFormat));
        sumNoTax.setStyleGenerator(item -> "v-align-right");

        Grid.Column sumWithTax = gridInvoiceList.getColumn("sumWithTax");
        sumWithTax.setRenderer(new NumberRenderer(kronerFormat));
        sumWithTax.setStyleGenerator(item -> "v-align-right");

        FooterRow footer = gridInvoiceList.appendFooterRow();
        footer.setStyleName("bold");
        FooterCell joinedFooterCell = footer.join("clientname", "projectname", "invoicenumber", "invoicedate", "type", "status");
        joinedFooterCell.setText("Total: ");
        joinedFooterCell.setStyleName("v-align-right");
        Registration registration = dataProvider.addDataProviderListener(event -> {
            double sum = event.getSource()
                    .fetch(new Query<>())
                    .filter(invoice -> invoice.type != InvoiceType.CREDIT_NOTE && invoice.status != CREDIT_NOTE)
                    .mapToDouble(Invoice::getSumNoTax).sum();
            FooterCell sumNoTaxFooter = footer.getCell("sumNoTax");
            sumNoTaxFooter.setText(kronerFormat.format(sum));
            sumNoTaxFooter.setStyleName("v-align-right");

            FooterCell sumWithTaxFooter = footer.getCell("sumWithTax");
            sumWithTaxFooter.setText(kronerFormat.format(sum*1.25));
            sumWithTaxFooter.setStyleName("v-align-right");
        });
        // Fire a data change event to initialize the summary footer

        btnDownloadPdf.addClickListener(clickEvent -> {
            if(!gridInvoiceList.getSelectionModel().getFirstSelectedItem().isPresent()) return;
            PdfContainer invoicePdf = invoiceClient.getInvoicePdf(gridInvoiceList.getSelectionModel().getFirstSelectedItem().get().getUuid());

            final StreamResource resource = new StreamResource(() ->
                    new ByteArrayInputStream(invoicePdf.pdf), "invoice.pdf");

            SimpleFileDownloader downloader = new SimpleFileDownloader();
            addExtension(downloader);
            downloader.setFileDownloadResource(resource);
            downloader.download();

        });

        btnCreateCreditNota.addClickListener(clickEvent -> {
            Invoice invoice = gridInvoiceList.getSelectionModel().getFirstSelectedItem().get();
            System.out.println("invoice = " + invoice);
            if(invoice.uuid == null && invoice.uuid.trim().length() == 0) return;
            projectSummaryClient.createCreditNota(invoice);
        });

        gridInvoiceList.getDataProvider().refreshAll();
    }

    private TextField getColumnFilterField() {
        TextField filter = new TextField();
        filter.setWidth("100%");
        filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filter.setPlaceholder("Filter");
        return filter;
    }

    @Override
    public void receiveBroadcast(final String message) {
        if(this.getUI() != null) {
            this.getUI().access(() -> {
                loadInvoicesToGrid();
                createSubTotalsGrid();
            });
        }
    }

    public void loadInvoicesToGrid() {
        invoices = invoiceClient.findByStatusIn(CREATED, SUBMITTED, PAID, CREDIT_NOTE);
        dataProvider = DataProvider.ofCollection(calcTotal(invoices).getContent());
        gridInvoiceList.setDataProvider(dataProvider);
        gridInvoiceList.getDataProvider().refreshAll();
    }

    private Resources<Invoice> calcTotal(Resources<Invoice> invoices) {
        for (Invoice invoice : invoices) {
            invoice.setSumNoTax(0.0);
            for (InvoiceItem invoiceitem : invoice.invoiceitems) {
                invoice.addToSumNoTax(invoiceitem.getHours() * invoiceitem.getRate());
            }
        }
        return invoices;
    }
}