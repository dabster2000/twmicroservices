package dk.trustworks.invoicewebui.web.stats.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.model.dto.ExpenseDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.InvoiceService;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.dashboard.cards.DashboardBoxCreator;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardImpl;
import dk.trustworks.invoicewebui.web.model.stats.BudgetItem;
import dk.trustworks.invoicewebui.web.model.stats.ExpenseItem;
import dk.trustworks.invoicewebui.web.stats.components.charts.expenses.AvgExpensesPerYearChart;
import dk.trustworks.invoicewebui.web.stats.components.charts.utilization.UtilizationPerYearChart;
import dk.trustworks.invoicewebui.web.vtv.components.UtilizationPerMonthChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.haijian.Exporter;
import org.vaadin.viritin.button.MButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Created by hans on 20/09/2017.
 */
@SpringComponent
@SpringUI
public class TrustworksStatsLayout extends VerticalLayout {

    @Autowired
    private RevenuePerMonthChart revenuePerMonthChart;

    @Autowired
    private AvgExpensesPerMonthChart avgExpensesPerMonthChart;

    @Autowired
    private AllRevenuePerMonthChart allRevenuePerMonthChart;

    @Autowired
    private CumulativeRevenuePerMonthChart cumulativeRevenuePerMonthChart;

    @Autowired
    private TopGrossingConsultantsChart topGrossingConsultantsChart;

    @Autowired
    private AverageConsultantRevenueByYearChart averageConsultantRevenueByYearChart;

    @Autowired
    private AverageConsultantRevenueChart averageConsultantRevenueChart;

    @Autowired
    private AverageConsultantAllocationChart averageConsultantAllocationChart;

    @Autowired
    private RevenuePerMonthEmployeeAvgChart revenuePerMonthEmployeeAvgChart;

    @Autowired
    private YourTrustworksForecastChart yourTrustworksForecastChart;

    @Autowired
    private UtilizationPerYearChart utilizationPerYearChart;

    @Autowired
    private BudgetTable budgetTable;

    @Autowired
    private ExpenseTable expenseTable;

    @Autowired
    private ConsultantsBudgetRealizationChart consultantsBudgetRealizationChart;

    @Autowired
    private ExpensesSalariesRevenuePerMonthChart expensesSalariesRevenuePerMonthChart;

    @Autowired
    private RevenuePerConsultantChart revenuePerConsultantChart;

    @Autowired
    private AvgExpensesPerYearChart avgExpensesPerYearChart;

    @Autowired
    private ExpensesPerMonthChart expensesPerMonthChart;

    @Autowired
    private UtilizationPerMonthChart utilizationPerMonthChart;

    @Autowired
    private TalentPassionResultBox talentPassionResultBox;

    @Autowired
    private UserService userService;

    @Autowired
    private DashboardBoxCreator dashboardBoxCreator;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private InvoiceService invoiceService;

    private ResponsiveRow baseContentRow;

    private ResponsiveRow buttonContentRow;

    private ResponsiveRow companyContentRow;
    private ResponsiveRow consultantsContentRow;
    private ResponsiveRow empty1ContentRow;
    private ResponsiveRow empty2ContentRow;
    private ResponsiveRow empty3ContentRow;
    private ResponsiveRow empty4ContentRow;

    public TrustworksStatsLayout init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        responsiveLayout.setSizeFull();
        responsiveLayout.setScrollable(true);

        baseContentRow = responsiveLayout.addRow();
        buttonContentRow = responsiveLayout.addRow();
        companyContentRow = responsiveLayout.addRow();
        consultantsContentRow = responsiveLayout.addRow();
        consultantsContentRow.setVisible(false);
        empty1ContentRow = responsiveLayout.addRow();
        empty1ContentRow.setVisible(false);
        empty2ContentRow = responsiveLayout.addRow();
        empty2ContentRow.setVisible(false);
        empty3ContentRow = responsiveLayout.addRow();
        empty3ContentRow.setVisible(false);
        empty4ContentRow = responsiveLayout.addRow();
        empty4ContentRow.setVisible(false);
        addComponent(responsiveLayout);
        loadData();
        return this;
    }

    private void loadData() {
        createMonthReportCard();
    }

    private void createMonthReportCard() {
        baseContentRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getGoodPeopleBox()));
        baseContentRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getCumulativeGrossRevenue()));
        baseContentRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getPayout()));
        baseContentRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getUserAllocationBox()));

        final Button btnCompany = new MButton(MaterialIcons.BUSINESS, "trustworks", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");


        final Button btnConsultants = new MButton(MaterialIcons.FACE, "consultants", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");

        // Stats that show yearly progress
        final Button btnEmpty1 = new MButton(MaterialIcons.HISTORY,"History", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top").withEnabled(false);

        // Stats that show customer distributions
        final Button btnEmpty2 = new MButton(MaterialIcons.PEOPLE,"Customers", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top").withEnabled(false);

        // Stats for administration, like salary, vacation, illness
        final Button btnEmpty3 = new MButton(MaterialIcons.INBOX, "Administration", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top").withEnabled(false);

        // UNKNOWN
        final Button btnEmpty4 = new MButton( "", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top").withEnabled(false);

        btnCompany.addClickListener(event -> setNewButtonPressState(btnCompany, btnEmpty1, btnEmpty2, btnEmpty3, btnEmpty4, btnConsultants, event, companyContentRow));
        btnConsultants.addClickListener(event -> setNewButtonPressState(btnCompany, btnEmpty1, btnEmpty2, btnEmpty3, btnEmpty4, btnConsultants, event, consultantsContentRow));
        btnEmpty1.addClickListener(event -> setNewButtonPressState(btnCompany, btnEmpty1, btnEmpty2, btnEmpty3, btnEmpty4, btnConsultants, event, empty1ContentRow));
        btnEmpty2.addClickListener(event -> setNewButtonPressState(btnCompany, btnEmpty1, btnEmpty2, btnEmpty3, btnEmpty4, btnConsultants, event, empty2ContentRow));
        btnEmpty3.addClickListener(event -> setNewButtonPressState(btnCompany, btnEmpty1, btnEmpty2, btnEmpty3, btnEmpty4, btnConsultants, event, empty3ContentRow));
        btnEmpty4.addClickListener(event -> setNewButtonPressState(btnCompany, btnEmpty1, btnEmpty2, btnEmpty3, btnEmpty4, btnConsultants, event, empty4ContentRow));

        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnCompany);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnConsultants);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnEmpty1);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnEmpty2);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnEmpty3);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnEmpty4);

        addCompanyCharts();
        addConsultantCharts();
    }

    public void addCompanyCharts() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        companyContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);
        ResponsiveRow searchRow = responsiveLayout.addRow();
        final ResponsiveRow chartRow = responsiveLayout.addRow();

        AtomicReference<LocalDate> currentFiscalYear = new AtomicReference<>(DateUtils.getCurrentFiscalStartDate());

        Button btnFiscalYear = new MButton(createFiscalYearText(currentFiscalYear))
                .withStyleName("tiny", "flat", "large-icon","icon-align-top")
                .withHeight(125, Unit.PIXELS)
                .withFullWidth()
                .withIcon(MaterialIcons.INSERT_INVITATION);

        Button btnDescFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_LEFT, null, event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().minusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createCompanyCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1), getCreateChartsNotification());
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        Button btnIncFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_RIGHT, null, event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().plusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createCompanyCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1), getCreateChartsNotification());
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        searchRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new Label(""));
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnDescFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnIncFiscalYear);

        createCompanyCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1), getCreateChartsNotification());
    }

    private void createCompanyCharts(ResponsiveRow chartRow, LocalDate localDateStart, LocalDate localDateEnd, Notification notification) {
        Box revenuePerMonthCard = new Box();
        revenuePerMonthCard.getContent().addComponent(revenuePerMonthChart.createRevenuePerMonthChart(localDateStart, localDateEnd));

        Box expensesPerMonthCard = new Box();
        expensesPerMonthCard.getContent().addComponent(expensesPerMonthChart.createExpensePerMonthChart(localDateStart, localDateEnd));

        Box cumulativeRevenuePerMonthCard = new Box();
        cumulativeRevenuePerMonthCard.getContent().addComponent(cumulativeRevenuePerMonthChart.createCumulativeRevenuePerMonthChart(localDateStart, localDateEnd));

        Box utilizationPerMonthCard = new Box();
        utilizationPerMonthCard.getContent().addComponent(utilizationPerMonthChart.createUtilizationPerMonthChart(localDateStart, localDateEnd));

        // Weighted Average Margin Per Month

        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(revenuePerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(expensesPerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(cumulativeRevenuePerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(utilizationPerMonthCard);
    }

    public void addConsultantCharts() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        consultantsContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);
        ResponsiveRow searchRow = responsiveLayout.addRow();
        final ResponsiveRow chartRow = responsiveLayout.addRow();

        AtomicReference<LocalDate> currentFiscalYear = new AtomicReference<>(DateUtils.getCurrentFiscalStartDate());

        Button btnFiscalYear = new MButton(createFiscalYearText(currentFiscalYear))
                .withStyleName("tiny", "flat", "large-icon","icon-align-top")
                .withHeight(125, Unit.PIXELS)
                .withFullWidth()
                .withIcon(MaterialIcons.INSERT_INVITATION);

        Button btnDescFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_LEFT, " ", event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().minusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createConsultantCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1), getCreateChartsNotification());
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        Button btnIncFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_RIGHT, " ", event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().plusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createConsultantCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1), getCreateChartsNotification());
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        searchRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new Label(""));
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnDescFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnIncFiscalYear);

        createConsultantCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1), getCreateChartsNotification());
    }

    private void createConsultantCharts(ResponsiveRow chartRow, LocalDate localDateStart, LocalDate localDateEnd, Notification notification) {
        Box topGrossingConsultantsBox = new Box();
        topGrossingConsultantsBox.getContent().addComponent(topGrossingConsultantsChart.createTopGrossingConsultantsChart(localDateStart, localDateEnd));

        Box avgExpensesPerMonthCard = new Box();
        avgExpensesPerMonthCard.getContent().addComponent(avgExpensesPerMonthChart.createExpensePerMonthChart(localDateStart, localDateEnd));

        /*
        Box averageConsultantRevenueByYearCard = new Box();
        averageConsultantRevenueByYearCard.getContent().addComponent(averageConsultantRevenueByYearChart.createRevenuePerConsultantChart());
         */

        Box yourTrustworksForecastCard = new Box();
        yourTrustworksForecastCard.getContent().addComponent(yourTrustworksForecastChart.createChart(localDateStart, localDateEnd));

        Box revenuePerMonthEmployeeAvgCard = new Box();
        revenuePerMonthEmployeeAvgCard.getContent().addComponent(revenuePerMonthEmployeeAvgChart.createRevenuePerMonthChart(localDateStart, localDateEnd.withDayOfMonth(1).minusMonths(1)));


        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(topGrossingConsultantsBox);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(avgExpensesPerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(yourTrustworksForecastCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(revenuePerMonthEmployeeAvgCard);
    }

    public void old() {
        // *** OLD ***

        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

        ResponsiveRow boxRow = responsiveLayout.addRow();
        buttonContentRow = responsiveLayout.addRow();

        ResponsiveRow searchRow = responsiveLayout.addRow();


        boxRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getGoodPeopleBox()));
        boxRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getCumulativeGrossRevenue()));
        boxRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getPayout()));
        boxRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getUserAllocationBox()));


        final ResponsiveRow chartRow = responsiveLayout.addRow();

        LocalDate startFiscalPeriod = LocalDate.of(2014, 7, 1);
        ComboBox<LocalDate> fiscalPeriodStartComboBox = new ComboBox<>();
        ComboBox<LocalDate> fiscalPeriodEndComboBox = new ComboBox<>();
        AtomicReference<LocalDate> currentFiscalYear = new AtomicReference<>(DateUtils.getCurrentFiscalStartDate());
        List<LocalDate> fiscalPeriodList = new ArrayList<>();
        while(startFiscalPeriod.isBefore(currentFiscalYear.get()) || startFiscalPeriod.isEqual(currentFiscalYear.get())) {
            fiscalPeriodList.add(startFiscalPeriod);
            startFiscalPeriod = startFiscalPeriod.plusYears(1);
        }
        fiscalPeriodList.add(startFiscalPeriod);

        Button btnFiscalYear = new MButton(createFiscalYearText(currentFiscalYear))
                .withStyleName("tiny", "flat", "large-icon","icon-align-top").withFullWidth();

        Button btnDescFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_LEFT, "", event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().minusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1), getCreateChartsNotification());
        }).withStyleName("tiny", "icon-only", "flat", "large-icon","icon-align-top").withFullWidth();

        Button btnIncFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_RIGHT, "", event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().plusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1), getCreateChartsNotification());
        }).withStyleName("tiny", "icon-only", "flat", "large-icon","icon-align-top").withFullWidth();



        //searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withVisibilityRules(false, false,true,true).withComponent(new Label(""));
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnDescFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnIncFiscalYear);
        //searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withVisibilityRules(false, false,true,true).withComponent(new Label(""));

        //searchRow.addColumn().withDisplayRules(12, 6, 4, 3).withComponent(fiscalPeriodStartComboBox);
        //searchRow.addColumn().withDisplayRules(12, 6, 4, 3).withComponent(fiscalPeriodEndComboBox);

        int adjustStartYear = 0;
        if(LocalDate.now().getMonthValue() >= 1 && LocalDate.now().getMonthValue() <=6)  adjustStartYear = 1;
        LocalDate localDateStart = LocalDate.now().withMonth(7).withDayOfMonth(1).minusYears(adjustStartYear);
        LocalDate localDateEnd = localDateStart.plusYears(1);

        //fiscalPeriodStartComboBox.setWidth(100, Unit.PERCENTAGE);
        fiscalPeriodStartComboBox.setItems(fiscalPeriodList);
        fiscalPeriodStartComboBox.setItemCaptionGenerator(localDate -> localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        //fiscalPeriodStartComboBox.setWidth(100, Unit.PERCENTAGE);
        fiscalPeriodEndComboBox.setItems(fiscalPeriodList);
        fiscalPeriodEndComboBox.setItemCaptionGenerator(localDate -> localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        fiscalPeriodStartComboBox.setSelectedItem(fiscalPeriodList.get(fiscalPeriodList.size()-2));
        fiscalPeriodEndComboBox.setSelectedItem(fiscalPeriodList.get(fiscalPeriodList.size()-1));

        fiscalPeriodStartComboBox.addValueChangeListener(event -> {
            if(event.getValue().isEqual(fiscalPeriodEndComboBox.getValue()) || event.getValue().isAfter(fiscalPeriodEndComboBox.getValue())) return;
            chartRow.removeAllComponents();
            createCharts(chartRow, event.getValue(), fiscalPeriodEndComboBox.getValue(), getCreateChartsNotification());
        });

        fiscalPeriodEndComboBox.addValueChangeListener(event -> {
            if(event.getValue().isEqual(fiscalPeriodStartComboBox.getValue()) || event.getValue().isBefore(fiscalPeriodStartComboBox.getValue())) return;
            chartRow.removeAllComponents();
            createCharts(chartRow, fiscalPeriodStartComboBox.getValue(), event.getValue(), getCreateChartsNotification());
        });

        createCharts(chartRow, localDateStart, localDateEnd, getCreateChartsNotification());

        this.addComponent(responsiveLayout);

        //return this;
    }

    private String createFiscalYearText(AtomicReference<LocalDate> currentFiscalYear) {
        return "Fiscal year " + currentFiscalYear.get().getYear() + "/" + currentFiscalYear.get().plusYears(1).format(DateTimeFormatter.ofPattern("yy"));
    }

    private Notification getCreateChartsNotification() {
        Notification notification = new Notification("Creating charts...",
                "0 out of 7 charts created!",
                Notification.Type.TRAY_NOTIFICATION, true);
        notification.setDelayMsec(120000);
        notification.show(Page.getCurrent());
        return notification;
    }

    private void createCharts(ResponsiveRow chartRow, LocalDate localDateStart, LocalDate localDateEnd, Notification notification) {

        long timeMillis = System.currentTimeMillis();
        /*
        Box revenuePerMonthCard = new Box();
        revenuePerMonthCard.getContent().addComponent(revenuePerMonthChart.createRevenuePerMonthChart(localDateStart, localDateEnd));
        notification.setDescription("Revenue per month created!");
        System.out.println("timeMillis 1 = " + (System.currentTimeMillis() - timeMillis));

        Card avgExpensesPerMonthCard = new Card();
        avgExpensesPerMonthCard.getLblTitle().setValue("Average Expenses Per Month");
        avgExpensesPerMonthCard.getContent().addComponent(avgExpensesPerMonthChart.createExpensePerMonthChart(localDateStart, localDateEnd));
        notification.setDescription("1b out of 10 charts created!");
        System.out.println("timeMillis 1b = " + (System.currentTimeMillis() - timeMillis));

        Card allRevenuePerMonthCard = new Card();
        allRevenuePerMonthCard.getLblTitle().setValue("Revenue Per Month");
        allRevenuePerMonthCard.getContent().addComponent(allRevenuePerMonthChart.createRevenuePerMonthChart());
        notification.setDescription("1a out of 13 charts created!");
        System.out.println("timeMillis 1a = " + (System.currentTimeMillis() - timeMillis));

        Card cumulativeRevenuePerMonthCard = new Card();
        cumulativeRevenuePerMonthCard.getLblTitle().setValue("Cumulative Revenue Per Month");
        cumulativeRevenuePerMonthCard.getContent().addComponent(cumulativeRevenuePerMonthChart.createCumulativeRevenuePerMonthChart(localDateStart, localDateEnd));
        notification.setDescription("2 out of 10 charts created!");
        System.out.println("timeMillis 2 = " + (System.currentTimeMillis() - timeMillis));

        Card consultantGrossingCard = new Card();
        consultantGrossingCard.getLblTitle().setValue("Top Grossing Consultants");
        consultantGrossingCard.getContent().addComponent(topGrossingConsultantsChart.createTopGrossingConsultantsChart(localDateStart, localDateEnd));
        notification.setDescription("3 out of 10 charts created!");
        System.out.println("timeMillis 3 = " + (System.currentTimeMillis() - timeMillis));


        Card consultantHoursPerMonth = new Card();
        consultantHoursPerMonth.getLblTitle().setValue("Consultant Hours Per Month");
        consultantHoursPerMonth.getContent().addComponent(consultantHoursPerMonthChart.createTopGrossingConsultantsChart(localDateStart, localDateEnd));
        notification.setDescription("4 out of 10 charts created!");
        System.out.println("timeMillis 4 = " + (System.currentTimeMillis() - timeMillis));

        Card averageConsultantRevenueByYearCard = new Card();
        averageConsultantRevenueByYearCard.getLblTitle().setValue("Average Revenue Per Consultant");
        averageConsultantRevenueByYearCard.getContent().addComponent(averageConsultantRevenueByYearChart.createRevenuePerConsultantChart());
        notification.setDescription("4 out of 10 charts created!");
        System.out.println("timeMillis 4 = " + (System.currentTimeMillis() - timeMillis));
*/
        Card averageConsultantRevenueCard = new Card();
        averageConsultantRevenueCard.getLblTitle().setValue("Average Gross Margin Per Consultant");
        averageConsultantRevenueCard.getContent().addComponent(averageConsultantRevenueChart.createRevenuePerConsultantChart());
        notification.setDescription("5 out of 10 charts created!");
        System.out.println("timeMillis 5 = " + (System.currentTimeMillis() - timeMillis));

        Card averageConsultantAllocationCard = new Card();
        averageConsultantAllocationCard.getLblTitle().setValue("Average Allocation Per Consultant");
        averageConsultantAllocationCard.getContent().addComponent(averageConsultantAllocationChart.createChart());
        notification.setDescription("6 out of 10 charts created!");
        System.out.println("timeMillis 6 = " + (System.currentTimeMillis() - timeMillis));

        Card yourTrustworksForecastCard = new Card();
        yourTrustworksForecastCard.getLblTitle().setValue("Your Trustworks Forecast");
        yourTrustworksForecastCard.getContent().addComponent(yourTrustworksForecastChart.createChart(localDateStart, localDateEnd));
        notification.setDescription("6 out of 10 charts created!");
        System.out.println("timeMillis 6 = " + (System.currentTimeMillis() - timeMillis));

        /*
        Card cumulativePredictiveRevenuePerMonthCard = new Card();
        cumulativePredictiveRevenuePerMonthCard.getLblTitle().setValue("Cumulative Predicted Revenue");
        cumulativePredictiveRevenuePerMonthCard.getContent().addComponent(cumulativePredictiveRevenuePerMonthChart.createCumulativePredictiveRevenuePerMonthChart());
        notification.setDescription("5 out of 10 charts created!");
        System.out.println("timeMillis 5 = " + (System.currentTimeMillis() - timeMillis));
         */
/*
        Card cumulativePredictiveRevenuePerYearCard = new Card();
        cumulativePredictiveRevenuePerYearCard.getLblTitle().setValue("Cumulative Predicted Revenue");
        cumulativePredictiveRevenuePerYearCard.getContent().addComponent(cumulativePredictiveRevenuePerYearChart.createCumulativePredictiveRevenuePerYearChart());
        notification.setDescription("7 out of 10 charts created!");
        System.out.println("timeMillis 7 = " + (System.currentTimeMillis() - timeMillis));

        Card revenuePerMonthEmployeeAvgCard = new Card();
        revenuePerMonthEmployeeAvgCard.getLblTitle().setValue("Average Revenue per Consultant");
        revenuePerMonthEmployeeAvgCard.getContent().addComponent(revenuePerMonthEmployeeAvgChart.createRevenuePerMonthChart(localDateStart, localDateEnd.withDayOfMonth(1).minusMonths(1)));
        notification.setDescription("8 out of 10 charts created!");
        System.out.println("timeMillis 8 = " + (System.currentTimeMillis() - timeMillis));

 */

        Card consultantsBudgetRealizationCard = new Card();
        consultantsBudgetRealizationCard.getLblTitle().setValue("Consultant Budget Realization");
        consultantsBudgetRealizationCard.getContent().addComponent(consultantsBudgetRealizationChart.createConsultantsBudgetRealizationChart());
        notification.setDescription("9 out of 10 charts created!");
        System.out.println("timeMillis 9 = " + (System.currentTimeMillis() - timeMillis));

        Card talentPassionCard = new Card();
        talentPassionCard.getLblTitle().setValue("Talent & Passion");
        talentPassionCard.getContent().addComponent(talentPassionResultBox.create());
        notification.setDescription("9 out of 10 charts created!");
        System.out.println("timeMillis 9 = " + (System.currentTimeMillis() - timeMillis));

        Card expensesPerEmployee = new Card();
        expensesPerEmployee.getLblTitle().setValue("Average Historical Economic Overview per Employee");
        expensesPerEmployee.getContent().addComponent(expensesSalariesRevenuePerMonthChart.createExpensesPerMonthChart());
        notification.setDescription("10 out of 10 charts created!");
        System.out.println("timeMillis 10 = " + (System.currentTimeMillis() - timeMillis));

        Card revenuePerEmployee = new Card();
        ComboBox<User> userComboBox = new ComboBox<>();
        userComboBox.setItems(userService.findCurrentlyEmployedUsers(ConsultantType.CONSULTANT));
        userComboBox.setItemCaptionGenerator(User::getUsername);
        userComboBox.addValueChangeListener(event -> {
            revenuePerEmployee.getContent().removeAllComponents();
            revenuePerEmployee.getContent().addComponent(revenuePerConsultantChart.createRevenuePerConsultantChart(event.getValue()));
        });
        revenuePerEmployee.getHlTopBar().addComponent(userComboBox);
        revenuePerEmployee.getLblTitle().setValue("Historical Gross Profit per selected consultant");
        notification.setDescription("11 out of 11 charts created!");
        System.out.println("timeMillis 11 = " + (System.currentTimeMillis() - timeMillis));

        Card budgetTableCard = new Card();
        budgetTableCard.getLblTitle().setValue("Average Historical Economic Overview per Employee");
        TreeGrid<BudgetItem> table = budgetTable.createRevenuePerConsultantChart();
        budgetTableCard.getContent().addComponents(new MButton("content", event -> {
            table.getTreeData().getRootItems().stream().map(BudgetItem::changeValue);
            table.getDataProvider().refreshAll();
        }), table);
        notification.setDescription("12 out of 12 charts created!");
        System.out.println("timeMillis 12 = " + (System.currentTimeMillis() - timeMillis));

        Card expenseTableCard = new Card();
        expenseTableCard.getLblTitle().setValue("Expenses");
        Grid<ExpenseItem> table2 = expenseTable.createRevenuePerConsultantChart();
        Button b = new MButton("content");
        expenseTableCard.getContent().addComponents(b, table2);
        StreamResource excelStreamResource = new StreamResource((StreamResource.StreamSource) () -> Exporter.exportAsExcel(table2), "my-excel.xlsx");
        FileDownloader excelFileDownloader = new FileDownloader(excelStreamResource);
        excelFileDownloader.extend(b);
        notification.setDescription("13 out of 13 charts created!");
        System.out.println("timeMillis 13 = " + (System.currentTimeMillis() - timeMillis));

        Card avgExpensesPerYearCard = new Card();
        avgExpensesPerYearCard.getLblTitle().setValue("Average Expenses per Year per Employee");
        avgExpensesPerYearCard.getContent().addComponent(avgExpensesPerYearChart.createChart());
        notification.setDescription("14 out of 14 charts created!");
        System.out.println("timeMillis 14 = " + (System.currentTimeMillis() - timeMillis));

        Card utilizationPerYearCard = new Card();
        utilizationPerYearCard.getLblTitle().setValue("Average Utilization per Year per Employee");
        utilizationPerYearCard.getContent().addComponent(utilizationPerYearChart.createChart());
        notification.setDescription("15 out of 15 charts created!");
        System.out.println("timeMillis 15 = " + (System.currentTimeMillis() - timeMillis));

        /*
        Button downloadAsExcel = new Button("Download As Excel");
StreamResource excelStreamResource = new StreamResource((StreamResource.StreamSource) () -> Exporter.exportAsExcel(grid), "my-excel.xlsx");
FileDownloader excelFileDownloader = new FileDownloader(excelStreamResource);
excelFileDownloader.extend(downloadAsExcel);
         */
/*
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(revenuePerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(avgExpensesPerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(allRevenuePerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(cumulativeRevenuePerMonthCard);


        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(consultantGrossingCard);


        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(revenuePerMonthEmployeeAvgCard);

        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(cumulativePredictiveRevenuePerYearCard);
                */
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(consultantsBudgetRealizationCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(expensesPerEmployee);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(revenuePerEmployee);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(yourTrustworksForecastCard);
        /*
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(averageConsultantRevenueByYearCard);

         */
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(talentPassionCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(averageConsultantRevenueCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(averageConsultantAllocationCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(budgetTableCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(expenseTableCard);

        chartRow.addColumn()
                .withDisplayRules(1,1,1,1)
                .withComponent(new MButton("TEST").withListener(clickEvent -> {
                    String workResult = "username;date;workas;task;project;client;hours;rate\n";
                    for (Work work : workRepository.findByPeriod("2018-07-01", "2019-06-30")) {
                        User userEntity = userService.findByUUID(work.getUseruuid());
                        Double rate = contractService.findConsultantRateByWork(work, ContractStatus.SIGNED, ContractStatus.TIME, ContractStatus.BUDGET, ContractStatus.CLOSED);
                        workResult += ""+userEntity.getUsername()+";"+
                                work.getRegistered().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+";"+
                                userService.findByUUID(work.getWorkas())+";"+
                                work.getTask().getName()+";"+
                                work.getTask().getProject().getName()+";"+
                                work.getTask().getProject().getClient().getName()+";"+
                                work.getWorkduration()+";"+
                                rate+"\n";
                    }
                    TextArea workText = new TextArea("value", workResult);
                    chartRow.addColumn()
                            .withDisplayRules(12,12,12,12)
                            .withComponent(workText);

                    String expenseResult = "date;user;expensesum;salary;shared;staff\n";
                    for (ExpenseDocument document : statisticsService.getExpenseData()) {
                        expenseResult += document.getMonth()+";"+
                                document.getUser().getUsername()+";"+
                                document.getExpenseSum()+";"+
                                document.getSalary()+";"+
                                document.getSharedExpense()+";"+
                                document.getStaffSalaries()+"\n";
                    }
                    TextArea expensesText = new TextArea("expenses", expenseResult);
                    chartRow.addColumn()
                            .withDisplayRules(12,12,12,12)
                            .withComponent(expensesText);

                    String invoiceResult = "date;status;type;sum\n";
                    for (Invoice invoice : invoiceService.findAll()) {
                        invoiceResult += invoice.invoicenumber+";"+
                                invoice.getInvoicedate()+";"+
                                invoice.status+";"+
                                invoice.getType()+";"+
                                invoice.getInvoiceitems().stream().mapToDouble(value -> value.hours*value.rate).sum()+"\n";
                    }
                    TextArea invoiceText = new TextArea("invoice", invoiceResult);
                    chartRow.addColumn()
                            .withDisplayRules(12,12,12,12)
                            .withComponent(invoiceText);
                }));
        /*
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(cumulativePredictiveRevenuePerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(consultantHoursPerMonth);
        */

        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(avgExpensesPerYearCard);

        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(utilizationPerYearCard);

        notification.setDelayMsec(1000);
    }

    private void setNewButtonPressState(Button btnWork, Button btnKnowledge, Button btnSkills, Button btnBudget, Button btnDocuments, Button btnPurpose, Button.ClickEvent event, ResponsiveRow contentRow) {
        hideAllDynamicRows();
        enableAllButtons(btnWork, btnKnowledge, btnSkills, btnBudget, btnDocuments, btnPurpose);
        event.getButton().setEnabled(false);
        contentRow.setVisible(true);
    }

    private void enableAllButtons(Button btnWork, Button btnKnowledge, Button btnSkills, Button btnBudget, Button btnDocuments, Button btnPurpose) {
        btnWork.setEnabled(true);
        btnKnowledge.setEnabled(true);
        btnSkills.setEnabled(true);
        btnPurpose.setEnabled(true);
        btnBudget.setEnabled(true);
        btnDocuments.setEnabled(true);
    }

    private void hideAllDynamicRows() {
        companyContentRow.setVisible(false);
        consultantsContentRow.setVisible(false);
        empty1ContentRow.setVisible(false);
        empty3ContentRow.setVisible(false);
        empty2ContentRow.setVisible(false);
        empty4ContentRow.setVisible(false);
    }

}
