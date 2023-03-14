package dk.trustworks.invoicewebui.web.invoice.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.network.dto.ProjectSummary;
import dk.trustworks.invoicewebui.services.FinanceService;
import dk.trustworks.invoicewebui.services.InvoiceService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.utils.StringUtils;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardContent;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardImpl;
import dk.trustworks.invoicewebui.web.model.YearMonthSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.simplefiledownloader.SimpleFileDownloader;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

/**
 * Created by hans on 12/07/2017.
 */
@SpringComponent
@UIScope
public class NewInvoiceImpl2 extends NewInvoiceDesign2 {

    protected static Logger logger = LoggerFactory.getLogger(NewInvoiceImpl2.class.getName());

    private final FinanceService financeService;

    private final InvoiceService invoiceService;

    private final PhotoService photoService;

    //private final DropboxAPI dropboxAPI;

    private ResponsiveRow statsRow;
    private ResponsiveRow invoicesRow;

    private ComboBox<YearMonthSelect> yearMonthSelectComboBox;

    private InvoiceListItem selectedItem;

    @Autowired
    public NewInvoiceImpl2(FinanceService financeService, InvoiceService invoiceService, PhotoService photoService1) {
        this.financeService = financeService;
        this.invoiceService = invoiceService;
        this.photoService = photoService1;
    }

    public NewInvoiceImpl2 init() {
        logger.info("NewInvoiceImpl.init");
        this.removeAllComponents();

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        this.addComponent(responsiveLayout);

        statsRow = responsiveLayout.addRow();
        ResponsiveRow yearSelectionRow = responsiveLayout.addRow();
        invoicesRow = responsiveLayout.addRow();

        createYearMonthList(yearSelectionRow);

        return this;
    }

    public void reloadData() {
        logger.info("NewInvoiceImpl.reloadData");

        List<ProjectSummary> projectSummaries = invoiceService.loadProjectSummaryByYearAndMonth(yearMonthSelectComboBox.getValue().getDate());

        ResponsiveLayout invoiceListItemsResponsiveLayout = new ResponsiveLayout();
        VerticalLayout invoiceListLayout = new MVerticalLayout(new MVerticalLayout().withStyleName("card-1").with(invoiceListItemsResponsiveLayout));
        ResponsiveRow invoiceListItemsResponsiveRow = invoiceListItemsResponsiveLayout.addRow();
        VerticalLayout invoiceListVerticalLayout = new MVerticalLayout().withSpacing(false);
        VerticalLayout accordion = new MVerticalLayout().withSpacing(false).withMargin(false);
        invoiceListItemsResponsiveRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(accordion);

        TabSheet tabSheet = new TabSheet();

        Client client = new Client();
        InvoiceListItem lastInvoiceListItem = null;
        List<InvoiceTab> tabList = new ArrayList<>();

        double invoicedThisMonth = 0.0;
        double registeredThisMonth = 0.0;
        double registeredByClient = 0.0;
        double invoicedByClient = 0.0;
        int invoiceCount = 0;
        int invoicesSynced = 0;

        for (ProjectSummary projectSummary : projectSummaries.stream().sorted(Comparator.comparing(ProjectSummary::getClientname)).collect(Collectors.toList())) {
            InvoiceListItem invoiceListItem = new InvoiceListItem();
            invoiceListItem.getLblProjectName().setValue(projectSummary.getProjectname());
            invoiceListItem.getLblDescription().setValue(projectSummary.getDescription());
            invoiceListItem.getLblAmount().setValue(NumberConverter.formatCurrency(projectSummary.getRegisteredamount()));
            invoiceListItem.getLblInvoicedAmount().setValue(NumberConverter.formatCurrency(projectSummary.getInvoicedamount()));
            invoiceListItem.addStyleName("grey-box-border");

            // New client detected. Update previous tab with sums.
            if(!projectSummary.getClient().getUuid().equals(client.getUuid())) {
                if(projectSummary.getClient().getUuid().equals("2a44473b-d20d-4c0b-81ce-62a4d1368497")) logger.info(invoicesSynced + " of " + invoiceCount);
                if(projectSummary.getClient().getUuid().equals("2a44473b-d20d-4c0b-81ce-62a4d1368497")) logger.info("Ørsted");
                invoiceListVerticalLayout = new MVerticalLayout().withSpacing(false);
                if(!tabList.isEmpty()) {
                    InvoiceTab invoiceTab = tabList.get(tabList.size() - 1);
                    invoiceTab.getLblClientName().setValue(client.getName());
                    if(client.getAccountManager()!=null) invoiceTab.getImgAccountManager().addComponent(photoService.getRoundMemberImage(client.getAccountmanager(), 0, 40, PIXELS));
                    invoiceTab.getLblSynced().setValue(invoicesSynced + " of " + invoiceCount);
                    invoiceTab.getLblPercent().setValue(NumberConverter.convertDoubleToInt((invoicedByClient/registeredByClient)*100.0)+"%");
                }
                final InvoiceTab tab = new InvoiceTab();
                tab.getVlContent().addComponent(invoiceListVerticalLayout);
                tab.getHlTab().addLayoutClickListener(event -> {
                    tab.getVlContent().setVisible(!tab.getVlContent().isVisible());
                });
                accordion.addComponent(tab);
                tabList.add(tab);
                if(lastInvoiceListItem!=null) lastInvoiceListItem.removeStyleName("grey-box-border");
                client = projectSummary.getClient();
                registeredByClient = 0.0;
                invoicedByClient = 0.0;
                invoiceCount = 0;
                invoicesSynced = 0;
            }

            registeredByClient += projectSummary.getRegisteredamount();
            invoicedByClient += projectSummary.getInvoicedamount();

            registeredThisMonth += projectSummary.getRegisteredamount();
            invoicedThisMonth += projectSummary.getInvoicedamount();

            invoiceListItem.addLayoutClickListener(event -> {
                if(selectedItem!=null) {
                    if(selectedItem.equals(invoiceListItem)) return;
                    selectedItem.removeStyleName("light-blue");
                }
                invoiceListItem.addStyleName("light-blue");
                selectedItem = invoiceListItem;

                refreshTabs(tabSheet, projectSummary);
            });

            if(projectSummary.getClient().getUuid().equals("2a44473b-d20d-4c0b-81ce-62a4d1368497")) logger.info("Summary start: "+projectSummary);
            invoiceCount += projectSummary.getInvoiceList().size();
            if(projectSummary.getClient().getUuid().equals("2a44473b-d20d-4c0b-81ce-62a4d1368497")) logger.info("invoiceCount: " + invoiceCount);
            if(projectSummary.getInvoiceList().size() > 0) {
                if(projectSummary.getClient().getUuid().equals("2a44473b-d20d-4c0b-81ce-62a4d1368497")) logger.info("Set green");
                invoiceListItem.getTrafficLightRef().setStyleName("dark-green"); // dark-green
            }
            for (Invoice invoice : projectSummary.getInvoiceList()) {
                if(projectSummary.getClient().getUuid().equals("2a44473b-d20d-4c0b-81ce-62a4d1368497")) logger.info("Found invoice: "+invoice);
                if(invoice.getReferencenumber()==0) {
                    if(projectSummary.getClient().getUuid().equals("2a44473b-d20d-4c0b-81ce-62a4d1368497")) logger.info("Set yellow");
                    invoiceListItem.getTrafficLightRef().setStyleName("yellow"); // yellow
                    if(invoice.invoicedate.isBefore(LocalDate.now().minusMonths(1))) {
                        if(projectSummary.getClient().getUuid().equals("2a44473b-d20d-4c0b-81ce-62a4d1368497")) logger.info("Set red");
                        invoiceListItem.getTrafficLightRef().setStyleName("bg-secondary-1-2"); // red
                    }
                    if(projectSummary.getClient().getUuid().equals("2a44473b-d20d-4c0b-81ce-62a4d1368497")) logger.info("Invoice Not Synced");
                } else {
                    invoicesSynced++;
                    if(projectSummary.getClient().getUuid().equals("2a44473b-d20d-4c0b-81ce-62a4d1368497")) logger.info("Invoice Synced: "+invoicesSynced);
                }
            }
            if(projectSummary.getClient().getUuid().equals("2a44473b-d20d-4c0b-81ce-62a4d1368497")) logger.info("Summary done");

            if(projectSummary.getRegisteredamount()*0.2>projectSummary.getInvoicedamount()) {
                invoiceListItem.getTrafficLight().addStyleName("bg-secondary-1-2"); // red
            } else if(projectSummary.getRegisteredamount()*0.95<=projectSummary.getInvoicedamount()) {
                invoiceListItem.getTrafficLight().addStyleName("dark-green"); // dark-green
            } else {
                invoiceListItem.getTrafficLight().addStyleName("yellow"); // yellow
            }

            lastInvoiceListItem = invoiceListItem;
            invoiceListVerticalLayout.addComponent(invoiceListItem);
        }

        if(!tabList.isEmpty()) {
            InvoiceTab invoiceTab = tabList.get(tabList.size() - 1);
            invoiceTab.getLblClientName().setValue(client.getName());
            if(client.getAccountManager()!=null) invoiceTab.getImgAccountManager().addComponent(photoService.getRoundMemberImage(client.getAccountmanager(), 0, 40, PIXELS));
            invoiceTab.getLblSynced().setValue(invoicesSynced + " of " + invoiceCount);
            invoiceTab.getLblPercent().setValue(NumberConverter.convertDoubleToInt((invoicedByClient/registeredByClient)*100.0)+"%");
        }
        if(lastInvoiceListItem!=null) lastInvoiceListItem.removeStyleName("grey-box-border");

        createStatCards(NumberConverter.convertDoubleToInt(invoicedThisMonth), NumberConverter.convertDoubleToInt(registeredThisMonth-invoicedThisMonth), 0, 0);

        invoicesRow.removeAllComponents();

        ResponsiveLayout invoiceResponsiveLayout = new ResponsiveLayout();

        ResponsiveRow invoiceResponsiveLayoutRow = invoiceResponsiveLayout.addRow();

        invoiceResponsiveLayoutRow.addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(invoiceListLayout);

        VerticalLayout invoicePreviewVerticalLayout = new MVerticalLayout(tabSheet)
                .withStyleName("card-1");

        invoiceResponsiveLayoutRow.addColumn()
                .withDisplayRules(12, 12, 8, 8)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new VerticalLayout(invoicePreviewVerticalLayout));

        invoicesRow.addColumn().withDisplayRules(12, 12,12, 12).withComponent(invoiceResponsiveLayout);
    }

    public void refreshTabs(TabSheet tabSheet, ProjectSummary projectSummary) {
        tabSheet.removeAllComponents();
        for (Invoice invoice : projectSummary.getInvoiceList().stream().sorted(Comparator.comparingInt(Invoice::getInvoicenumber).reversed()).collect(Collectors.toList())) {
            DraftEditDesign invoiceEditDesign = new DraftEditImpl(invoice, invoiceService, financeService);
            tabSheet.addTab(invoiceEditDesign, (invoice.getInvoicenumber()==0)?"Phantom":invoice.getType().name().substring(0,2)+"-"+StringUtils.convertInvoiceNumberToString(invoice.invoicenumber), invoice.referencenumber==0?MaterialIcons.SYNC_PROBLEM:MaterialIcons.LINK);

            invoiceEditDesign.btnDownload.addClickListener(event -> {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

                final StreamResource resource = new StreamResource(() ->
                        new ByteArrayInputStream(invoice.getPdf()),
                        StringUtils.convertInvoiceNumberToString(invoice.invoicenumber) +
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
            invoiceEditDesign.btnDropbox.addClickListener(event -> {
                uploadToDropbox(invoice);
            });
            invoiceEditDesign.btnDropbox.setVisible(false);
            invoiceEditDesign.btnCreateCreditNote.addClickListener(event -> {
                if(invoice.uuid == null || invoice.uuid.trim().length() == 0) return;
                Invoice creditNota = invoiceService.createCreditNote(invoice);
                projectSummary.getDraftInvoiceList().add(creditNota);
                refreshTabs(tabSheet, projectSummary);
            });
        }
        for (Invoice invoice : projectSummary.getDraftInvoiceList().stream().sorted(Comparator.comparingInt(Invoice::getInvoicenumber).reversed()).collect(Collectors.toList())) {
            DraftEditImpl invoiceEditDesign = new DraftEditImpl(invoice, invoiceService, financeService);
            final TabSheet.Tab tab = tabSheet.addTab(invoiceEditDesign, "DRAFT");
            invoiceEditDesign.btnDelete.addClickListener(event -> {
                invoiceService.delete(invoice.getUuid());
                projectSummary.getDraftInvoiceList().remove(invoice);
                tabSheet.removeTab(tab);
            });
            invoiceEditDesign.btnCreateInvoice.addClickListener(event -> {
                Invoice savedInvoice = invoiceEditDesign.saveInvoice();
                savedInvoice = invoiceService.createInvoice(savedInvoice);
                projectSummary.getInvoiceList().add(savedInvoice);
                projectSummary.getDraftInvoiceList().remove(invoice);
                //uploadToDropbox(savedInvoice);
                refreshTabs(tabSheet, projectSummary);
            });
            invoiceEditDesign.btnCreatePhantom.addClickListener(event -> {
                Invoice savedInvoice = invoiceEditDesign.saveInvoice();
                savedInvoice = invoiceService.createPhantomInvoice(savedInvoice);
                projectSummary.getInvoiceList().add(savedInvoice);
                projectSummary.getDraftInvoiceList().remove(invoice);
                refreshTabs(tabSheet, projectSummary);
            });
            invoiceEditDesign.btnCopyDescription.addClickListener(event -> {
                Invoice latestInvoiceByProjectuuid = invoiceService.findByLatestInvoiceByProjectuuid(invoice.projectuuid);
                invoiceEditDesign.setSpecificDescription(latestInvoiceByProjectuuid.specificdescription);
                Invoice savedInvoice = invoiceEditDesign.saveInvoice();
                projectSummary.getDraftInvoiceList().remove(invoice);
                projectSummary.getDraftInvoiceList().add(savedInvoice);
            });
        }
        tabSheet.addTab(new MVerticalLayout()
                .withFullWidth()
                .withHeight(300, PIXELS)
                .withComponent(
                        new MButton("Add Draft", event1 -> {
                            Invoice invoiceFromProject = invoiceService.createInvoiceFromProject(projectSummary, yearMonthSelectComboBox.getValue().getDate());
                            projectSummary.getDraftInvoiceList().add(invoiceFromProject);
                            refreshTabs(tabSheet, projectSummary);
                        })
                ), "NEW"
        );
    }

    private void uploadToDropbox(Invoice invoice) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        final StreamResource resource = new StreamResource(() ->
                new ByteArrayInputStream(invoice.getPdf()),
                StringUtils.convertInvoiceNumberToString(invoice.invoicenumber) +
                        "_" + invoice.type +
                        "-" + invoice.clientname.replaceAll("\\W+", "") +
                        "-" + dateTimeFormatter.format(invoice.invoicedate) +
                        ".pdf"
        );


        //dropboxAPI.uploadInvoice(resource, invoice.invoicedate);
    }

    private void createStatCards(int value1, int value2, int value3, int value4) {
        TopCardImpl invoicedTopCard = new TopCardImpl(new TopCardContent("images/icons/trustworks_icon_kollega.svg", "Invoiced", "This month", NumberConverter.formatCurrency(value1), "dark-blue"));
        TopCardImpl notInvoicedTopCard = new TopCardImpl(new TopCardContent("images/icons/trustworks_icon_kollega.svg", "Not invoiced", "This month", NumberConverter.formatCurrency(value2), "dark-blue"));
        TopCardImpl soTopCard = new TopCardImpl(new TopCardContent("images/icons/trustworks_icon_kollega.svg", "SO Hours", "This month", "kr ???", "dark-blue"));
        TopCardImpl otherTopCard = new TopCardImpl(new TopCardContent("images/icons/trustworks_icon_kollega.svg", "Other count", "This month", "kr ???", "dark-blue"));

        statsRow.removeAllComponents();

        statsRow.addColumn()
                .withDisplayRules(6, 6, 3, 3)
                .withComponent(invoicedTopCard);
        statsRow.addColumn()
                .withDisplayRules(6, 6, 3, 3)
                .withComponent(notInvoicedTopCard);
        statsRow.addColumn()
                .withDisplayRules(6, 6, 3, 3)
                .withComponent(soTopCard);
        statsRow.addColumn()
                .withDisplayRules(6, 6, 3, 3)
                .withComponent(otherTopCard);
    }

    private void createYearMonthList(ResponsiveRow yearSelectionRow) {
        List<YearMonthSelect> yearMonthSelectList = createYearMonthList();
        yearMonthSelectComboBox = new ComboBox<>("Select month", yearMonthSelectList);
        yearMonthSelectComboBox.setWidth(100, PERCENTAGE);
        yearMonthSelectComboBox.setItemCaptionGenerator(c -> c.getDate().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        yearMonthSelectComboBox.setEmptySelectionAllowed(false);
        yearMonthSelectComboBox.setSelectedItem(yearMonthSelectList.get(1));
        yearMonthSelectComboBox.addValueChangeListener(event -> reloadData());

        yearSelectionRow.addColumn()
                .withDisplayRules(6, 6, 2, 2)
                .withOffset(ResponsiveLayout.DisplaySize.MD, 5)
                .withOffset(ResponsiveLayout.DisplaySize.LG, 5)
                .withOffset(ResponsiveLayout.DisplaySize.SM, 3)
                .withOffset(ResponsiveLayout.DisplaySize.XS, 3)
                .withComponent(yearMonthSelectComboBox);
    }

    private List<YearMonthSelect> createYearMonthList() {
        List<YearMonthSelect> yearMonthSelectList = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2014, 2, 1);
        LocalDate countDate = LocalDate.now();
        while (countDate.isAfter(startDate)) {
            yearMonthSelectList.add(new YearMonthSelect(countDate));
            countDate = countDate.minusMonths(1);
        }

        return yearMonthSelectList;
    }

}
