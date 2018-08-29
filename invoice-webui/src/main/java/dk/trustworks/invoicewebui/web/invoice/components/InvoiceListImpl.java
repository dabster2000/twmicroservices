package dk.trustworks.invoicewebui.web.invoice.components;

import com.vaadin.annotations.Push;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.InvoiceStatus;
import dk.trustworks.invoicewebui.model.enums.InvoiceType;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.repositories.InvoiceRepository;
import dk.trustworks.invoicewebui.security.Authorizer;
import dk.trustworks.invoicewebui.services.InvoiceService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.Broadcaster;
import dk.trustworks.invoicewebui.web.model.SubTotal;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.producttour.actions.TourActions;
import org.vaadin.addons.producttour.button.StepButton;
import org.vaadin.addons.producttour.shared.step.StepAnchor;
import org.vaadin.addons.producttour.step.Step;
import org.vaadin.addons.producttour.step.StepBuilder;
import org.vaadin.addons.producttour.tour.Tour;
import org.vaadin.simplefiledownloader.SimpleFileDownloader;
import org.vaadin.viritin.label.MLabel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.model.enums.InvoiceStatus.*;

/**
 * Created by hans on 13/07/2017.
 */

@SpringComponent
@SpringUI
@Push
public class InvoiceListImpl extends InvoiceListDesign
        implements Broadcaster.BroadcastListener {

    private final InvoiceRepository invoiceRepository;

    private final InvoiceService invoiceService;

    private ListDataProvider<Invoice> dataProvider;

    private List<Invoice> invoices;

    @Autowired
    public InvoiceListImpl(InvoiceRepository invoiceRepository, InvoiceService invoiceService, Authorizer authorizer) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;

        if(!authorizer.hasAccess(RoleType.ADMIN)) btnRecreateInvoice.setVisible(false);

        btnRecreateInvoice.addClickListener(event -> {
            Invoice invoice = gridInvoiceList.getSelectionModel().getFirstSelectedItem().get();
            try {
                invoice.pdf = invoiceService.createInvoicePdf(invoice);
            } catch (IOException e) {
                e.printStackTrace();
            }
            invoiceRepository.save(invoice);
        });

        Broadcaster.register(this);

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
            subTotalColumn.setRenderer(new NumberRenderer(NumberConverter.getCurrencyInstance()));
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
                btnRecreateInvoice.setEnabled(true);
                btnDownloadPdf.setEnabled(true);
                btnCreateCreditNota.setEnabled(true);
                if(gridInvoiceList.getSelectionModel().getFirstSelectedItem().get().status.equals(CREDIT_NOTE)
                        || gridInvoiceList.getSelectionModel().getFirstSelectedItem().get().type.equals(InvoiceType.CREDIT_NOTE)) {
                    btnCreateCreditNota.setEnabled(false);
                }
            } else {
                btnRecreateInvoice.setEnabled(false);
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
        filteringField.addValueChangeListener(event -> dataProvider.setFilter(Invoice::getStatus, invoiceStatus -> {
            if (invoiceStatus == null) {
                return false;
            }
            String companyLower = invoiceStatus.name().toLowerCase(Locale.ENGLISH);
            String filterLower = event.getValue().toLowerCase(Locale.ENGLISH);
            return companyLower.contains(filterLower);
        }));
        filteringHeader.getCell("status").setComponent(filteringField);

        gridInvoiceList.addComponentColumn(invoice -> {
            if (invoice.getType().equals(InvoiceType.CREDIT_NOTE)) {
                return new MLabel("CREDIT NOTE").withStyleName("orange");
            } else {
                return new MLabel("INVOICE").withStyleName("");
            }
        }).setId("type").setCaption("Invoice Type");

        gridInvoiceList.setColumnOrder("clientname","projectname","invoicenumber","invoicedate","type","status","sumNoTax","sumWithTax");
        /*
      <col column-id="clientname" sortable>
      <col column-id="projectname" sortable>
      <col column-id="invoicenumber" sortable>
      <col column-id="invoicedate" sortable>
      <col column-id="type" sortable>
      <col column-id="status" sortable>
      <col column-id="sumNoTax" sortable>
      <col column-id="sumWithTax" sortable>
                */

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
        sumNoTax.setRenderer(new NumberRenderer(NumberConverter.getCurrencyInstance()));
        sumNoTax.setStyleGenerator(item -> "v-align-right");

        Grid.Column sumWithTax = gridInvoiceList.getColumn("sumWithTax");
        sumWithTax.setRenderer(new NumberRenderer(NumberConverter.getCurrencyInstance()));
        sumWithTax.setStyleGenerator(item -> "v-align-right");

        FooterRow footer = gridInvoiceList.appendFooterRow();
        footer.setStyleName("bold");
        FooterCell joinedFooterCell = footer.join("clientname", "projectname", "invoicenumber", "invoicedate", "type", "status");
        joinedFooterCell.setText("Total: ");
        joinedFooterCell.setStyleName("v-align-right");
        Registration registration = dataProvider.addDataProviderListener(event -> {
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

        btnCreateCreditNota.addClickListener(clickEvent -> {
            Invoice invoice = gridInvoiceList.getSelectionModel().getFirstSelectedItem().get();
            System.out.println("invoice = " + invoice);
            if(invoice.uuid == null && invoice.uuid.trim().length() == 0) return;
            invoiceService.createCreditNota(invoice);
        });

        gridInvoiceList.getDataProvider().refreshAll();

        addTour();
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
        invoices = invoiceRepository.findByStatusIn(CREATED, SUBMITTED, PAID, CREDIT_NOTE);
        dataProvider = DataProvider.ofCollection(calcTotal(invoices));
        gridInvoiceList.setDataProvider(dataProvider);
        gridInvoiceList.getDataProvider().refreshAll();
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

    private void addTour() {
        btnTour.addClickListener(e -> {
            Tour tour = new Tour();
            tour.addStep(getStep1(gridInvoiceList));
            tour.addStep(getStep2(gridInvoiceList));
            tour.addStep(getStep3(btnDownloadPdf));
            tour.addStep(getStep4(btnCreateCreditNota));
            tour.addStep(getStep5(gridSubTotals));
            tour.start();
        });
    }

    private Step getStep1(AbstractComponent attachTo) {
        return new StepBuilder()
                //.withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(220, Unit.PIXELS)
                .withTitle("Invoice Status Page!")
                .withText(
                        "This page gives an overview of all the created invoices including a monthly view of the invoice totals. From this page you may " +
                                "change invoice status, download invoice pdf's, and create new credit notes.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .build();
    }

    private Step getStep2(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(350, Unit.PIXELS)
                .withTitle("Inovice Status")
                .withText("This grid gives an overview of all invoices which have on of the following statuses: CREATED, SENT, PAID, or CREDIT NOTE. " +
                        "You can also see the invoice number, invoice date, and the amount with or without tax. " +
                        "If you double-click an invoice you can change the invoice status - remember to press update when you are finished." +
                        "<br />" +
                        "<br />" +
                        "Finally the invoice type is shown, this can be either INVOICE or CREDIT NOTE. I'll get back to credit notes...")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.BOTTOM)
                .build();
    }

    private Step getStep3(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(200, Unit.PIXELS)
                .withTitle("Download invoice pdf")
                .withText("When an invoice is created a pdf is generated as well. This an be downloaded by selecting the invoice from the list and clicking this button.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.LEFT)
                .build();
    }

    private Step getStep4(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(350, Unit.PIXELS)
                .withTitle("Credit Note")
                .withText("If you need to create a credit note, select the invoice that need to be credit'et and click this button (Create Credit Note). The selected " +
                        "invoice status changes to CREDIT NOTE. " +
                        "<br />" +
                        "<br />" +
                        "A new credit note draft is also created and is found along with the other draft invoices under DRAFTS. This follows the same flow as a normal invoice" +
                        " but has another PDF template.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Next", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.LEFT)
                .build();
    }

    private Step getStep5(AbstractComponent attachTo) {
        return new StepBuilder()
                .withAttachTo(attachTo)
                .withWidth(400, Unit.PIXELS)
                .withHeight(200, Unit.PIXELS)
                .withTitle("Credit Note")
                .withText("This table shows invoice sums by month without tax. Invoices with credit notes aren't counted.")
                .addButton(new StepButton("Cancel", TourActions::back))
                .addButton(new StepButton("Finished", ValoTheme.BUTTON_PRIMARY, TourActions::next))
                .withAnchor(StepAnchor.TOP)
                .build();
    }
}
