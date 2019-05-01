package dk.trustworks.invoicewebui.web.invoice.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.annotations.Push;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.network.dto.ProjectSummary;
import dk.trustworks.invoicewebui.services.InvoiceService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.ProjectService;
import dk.trustworks.invoicewebui.services.ProjectSummaryService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.utils.StringUtils;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardContent;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardImpl;
import dk.trustworks.invoicewebui.web.model.YearMonthSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

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
@Push
@SpringComponent
@UIScope
public class NewInvoiceImpl2 extends NewInvoiceDesign2 {

    protected static Logger logger = LoggerFactory.getLogger(NewInvoiceImpl2.class.getName());

    private final ProjectSummaryService projectSummaryClient;

    private final InvoiceService invoiceService;

    private final PhotoService photoService;

    private final ProjectService projectService;

    private ResponsiveRow statsRow;
    private ResponsiveRow invoicesRow;

    private ComboBox<YearMonthSelect> yearMonthSelectComboBox;

    private InvoiceListItem selectedItem;

    @Autowired
    public NewInvoiceImpl2(ProjectSummaryService projectSummaryClient, InvoiceService invoiceService, PhotoService photoService, ProjectService projectService) {
        this.projectSummaryClient = projectSummaryClient;
        this.invoiceService = invoiceService;
        this.photoService = photoService;
        this.projectService = projectService;
    }



    public NewInvoiceImpl2 init() {
        logger.info("NewInvoiceImpl.init");

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

        createStatCards();

        List<ProjectSummary> projectSummaries = projectSummaryClient.loadProjectSummaryByYearAndMonth(yearMonthSelectComboBox.getValue().getDate().getYear(), yearMonthSelectComboBox.getValue().getDate().getMonthValue());

        ResponsiveLayout invoiceListItemsResponsiveLayout = new ResponsiveLayout();
        VerticalLayout invoiceListLayout = new MVerticalLayout(new MVerticalLayout().withStyleName("card-1").with(invoiceListItemsResponsiveLayout));
        ResponsiveRow invoiceListItemsResponsiveRow = invoiceListItemsResponsiveLayout.addRow();
        VerticalLayout invoiceListVerticalLayout = new MVerticalLayout();
        invoiceListItemsResponsiveRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(new MVerticalLayout().withHeight(500, Unit.PIXELS).withStyleName("v-scrollable").withComponent(invoiceListVerticalLayout));

        //

        TabSheet tabSheet = new TabSheet();

        String clientName = "";

        for (ProjectSummary projectSummary : projectSummaries.stream().sorted(Comparator.comparing(ProjectSummary::getClientname)).collect(Collectors.toList())) {
            InvoiceListItem invoiceListItem = new InvoiceListItem();
            invoiceListItem.getLblProjectName().setValue(projectSummary.getProjectname());
            invoiceListItem.getLblDescription().setValue(projectSummary.getDescription());
            invoiceListItem.getLblAmount().setValue(NumberConverter.formatCurrency(projectSummary.getRegisteredamount()));
            invoiceListItem.getLblInvoicedAmount().setValue(NumberConverter.formatCurrency(projectSummary.getInvoicedamount()));
            //invoiceListItem.getImgLogo().setSource(photoService.getRelatedPhoto(projectService.findOne(projectSummary.getProjectuuid()).getClient().getUuid()));
            //invoiceListItem.getImgLogo().setVisible(true);

            if(!projectSummary.getClientname().equals(clientName)) {
                Image clientLogo = new Image(null, photoService.getRelatedPhoto(projectService.findOne(projectSummary.getProjectuuid()).getClient().getUuid()));
                clientLogo.setWidth(80, PIXELS);
                clientLogo.setHeight(40, PIXELS);
                invoiceListVerticalLayout.addComponent(new MHorizontalLayout(clientLogo, new MLabel(projectSummary.getClientname())).withDefaultComponentAlignment(Alignment.MIDDLE_LEFT).withStyleName("light-grey").withFullWidth());
                //invoiceListVerticalLayout.setComponentAlignment(clientLogo, Alignment.MIDDLE_LEFT);
                clientName = projectSummary.getClientname();
            }

            invoiceListItem.addLayoutClickListener(event -> {
                System.out.println("event = " + event);
                if(selectedItem!=null) {
                    if(selectedItem.equals(invoiceListItem)) return;
                    selectedItem.removeStyleName("light-blue");
                }
                invoiceListItem.addStyleName("light-blue");
                selectedItem = invoiceListItem;

                tabSheet.removeAllComponents();
                for (Invoice invoice : projectSummary.getInvoiceList().stream().sorted(Comparator.comparingInt(o -> o.invoicenumber)).collect(Collectors.toList())) {
                    DraftEditDesign invoiceEditDesign = new DraftEditImpl(invoice);
                    tabSheet.addTab(invoiceEditDesign, StringUtils.convertInvoiceNumberToString(invoice.invoicenumber));
                }
            });
            //invoiceListItem.getImgLogo().setSource(photoService.getRelatedPhoto(projectSummary.get));
            invoiceListVerticalLayout.addComponent(invoiceListItem);
        }

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

    private void createStatCards() {
        TopCardImpl invoicedTopCard = new TopCardImpl(new TopCardContent("images/icons/trustworks_icon_kollega.svg", "Invoiced", "This month", "kr 2.301.200", "dark-blue"));
        TopCardImpl notInvoicedTopCard = new TopCardImpl(new TopCardContent("images/icons/trustworks_icon_kollega.svg", "Not invoiced", "This month", "kr 2.301.200", "dark-blue"));
        TopCardImpl soTopCard = new TopCardImpl(new TopCardContent("images/icons/trustworks_icon_kollega.svg", "SO Hours", "This month", "kr 2.301.200", "dark-blue"));
        TopCardImpl otherTopCard = new TopCardImpl(new TopCardContent("images/icons/trustworks_icon_kollega.svg", "Other count", "This month", "kr 2.301.200", "dark-blue"));

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
