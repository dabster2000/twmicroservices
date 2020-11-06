package dk.trustworks.invoicewebui.web.stats.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.jobs.DashboardPreloader;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.dashboard.cards.DashboardBoxCreator;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardImpl;
import dk.trustworks.invoicewebui.web.stats.components.charts.utilization.UtilizationPerYearChart;
import dk.trustworks.invoicewebui.web.vtv.components.UtilizationPerMonthChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

import static dk.trustworks.invoicewebui.model.enums.ConsultantType.CONSULTANT;


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
    private CumulativeRevenuePerMonthChart cumulativeRevenuePerMonthChart;

    @Autowired
    private TopGrossingConsultantsChart topGrossingConsultantsChart;

    @Autowired
    private AverageConsultantRevenueByYearChart averageConsultantRevenueByYearChart;

    @Autowired
    private RevenuePerMonthEmployeeAvgChart revenuePerMonthEmployeeAvgChart;

    @Autowired
    private YourTrustworksForecastChart yourTrustworksForecastChart;

    @Autowired
    private UtilizationPerYearChart utilizationPerYearChart;

    @Autowired
    private ExpensesSalariesRevenuePerMonthChart expensesSalariesRevenuePerMonthChart;

    @Autowired
    private RevenuePerConsultantChart revenuePerConsultantChart;

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
    private PhotoService photoService;

    @Autowired
    private DashboardPreloader dashboardPreloader;

    private ResponsiveRow baseContentRow;

    private ResponsiveRow buttonContentRow;

    private ResponsiveRow companyContentRow;
    private ResponsiveRow consultantsContentRow;
    private ResponsiveRow historyContentRow;
    private ResponsiveRow customersContentRow;
    private ResponsiveRow administrationContentRow;
    private ResponsiveRow individualsContentRow;

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
        historyContentRow = responsiveLayout.addRow();
        historyContentRow.setVisible(false);
        customersContentRow = responsiveLayout.addRow();
        customersContentRow.setVisible(false);
        administrationContentRow = responsiveLayout.addRow();
        administrationContentRow.setVisible(false);
        individualsContentRow = responsiveLayout.addRow();
        individualsContentRow.setVisible(false);
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
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top").withEnabled(false);

        final Button btnTeam = new MButton(MaterialIcons.PEOPLE, "team", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");

        // Stats that show yearly progress
        final Button btnHistory = new MButton(MaterialIcons.HISTORY,"History", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");

        // Stats that show customer distributions
        final Button btnCustomers = new MButton(MaterialIcons.CONTACTS,"Customers", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");

        // Stats for administration, like salary, vacation, illness
        final Button btnAdministration = new MButton(MaterialIcons.INBOX, "Administration", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");

        // Stats for individuals
        final Button btnIndividuals = new MButton( MaterialIcons.FACE, "Individuals", event -> {
        }).withHeight(125, Unit.PIXELS).withFullWidth().withStyleName("tiny", "flat", "large-icon", "icon-align-top");

        btnCompany.addClickListener(event -> setNewButtonPressState(btnCompany, btnTeam, btnHistory, btnCustomers, btnAdministration, btnIndividuals, event, companyContentRow));
        btnTeam.addClickListener(event -> setNewButtonPressState(btnCompany, btnTeam, btnHistory, btnCustomers, btnAdministration, btnIndividuals, event, consultantsContentRow));
        btnHistory.addClickListener(event -> setNewButtonPressState(btnCompany, btnTeam, btnHistory, btnCustomers, btnAdministration, btnIndividuals, event, historyContentRow));
        btnCustomers.addClickListener(event -> setNewButtonPressState(btnCompany, btnTeam, btnHistory, btnCustomers, btnAdministration, btnIndividuals, event, customersContentRow));
        btnAdministration.addClickListener(event -> setNewButtonPressState(btnCompany, btnTeam, btnHistory, btnCustomers, btnAdministration, btnIndividuals, event, administrationContentRow));
        btnIndividuals.addClickListener(event -> setNewButtonPressState(btnCompany, btnTeam, btnHistory, btnCustomers, btnAdministration, btnIndividuals, event, individualsContentRow));

        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnCompany);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnTeam);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnHistory);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnCustomers);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnAdministration);
        buttonContentRow.addColumn().withDisplayRules(6, 2, 2, 2).withComponent(btnIndividuals);

        addCompanyCharts();
        //addConsultantCharts();
        //addHistoryCharts();
        //addIndividualCharts();
    }

    public void addCompanyCharts() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        companyContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);
        ResponsiveRow searchRow = responsiveLayout.addRow();
        final ResponsiveRow chartRow = responsiveLayout.addRow();

        AtomicReference<LocalDate> currentFiscalYear = createDateSelectorHeader(searchRow, chartRow);

        createCompanyCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
    }

    private AtomicReference<LocalDate> createDateSelectorHeader(ResponsiveRow searchRow, ResponsiveRow chartRow) {
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
            createCompanyCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        Button btnIncFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_RIGHT, null, event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().plusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createCompanyCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        searchRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new Label(""));
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnDescFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnIncFiscalYear);
        return currentFiscalYear;
    }

    private void createCompanyCharts(ResponsiveRow chartRow, LocalDate localDateStart, LocalDate localDateEnd) {
        Box revenuePerMonthCard = new Box();
        revenuePerMonthCard.getContent().addComponent(revenuePerMonthChart.createRevenuePerMonthChart(localDateStart, localDateEnd));

        Box expensesPerMonthCard = new Box();
        expensesPerMonthCard.getContent().addComponent(expensesPerMonthChart.createExpensePerMonthChart(localDateStart, localDateEnd));

        Box cumulativeRevenuePerMonthCard = new Box();
        //cumulativeRevenuePerMonthCard.getContent().addComponent(cumulativeRevenuePerMonthChart.createCumulativeRevenuePerMonthChart(localDateStart, localDateEnd));

        Box utilizationPerMonthCard = new Box();
        //utilizationPerMonthCard.getContent().addComponent(utilizationPerMonthChart.createUtilizationPerMonthChart(localDateStart, localDateEnd));

        Box forecastRevenuePerMonthCard = new Box();

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
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(forecastRevenuePerMonthCard);
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
            createConsultantCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        Button btnIncFiscalYear = new MButton(MaterialIcons.KEYBOARD_ARROW_RIGHT, " ", event -> {
            chartRow.removeAllComponents();
            currentFiscalYear.set(currentFiscalYear.get().plusYears(1));
            btnFiscalYear.setCaption(createFiscalYearText(currentFiscalYear));
            createConsultantCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
        }).withHeight(125, Unit.PIXELS).withStyleName("tiny", "icon-only", "flat", "large-icon").withFullWidth();

        searchRow.addColumn().withDisplayRules(12,12,12,12).withComponent(new Label(""));
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnDescFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnFiscalYear);
        searchRow.addColumn().withDisplayRules(4, 4, 4, 4).withComponent(btnIncFiscalYear);

        createConsultantCharts(chartRow, currentFiscalYear.get(), currentFiscalYear.get().plusYears(1));
    }

    private void createConsultantCharts(ResponsiveRow chartRow, LocalDate localDateStart, LocalDate localDateEnd) {
        Box topGrossingConsultantsBox = new Box();
        topGrossingConsultantsBox.getContent().addComponent(topGrossingConsultantsChart.createTopGrossingConsultantsChart(localDateStart, localDateEnd));

        Box avgExpensesPerMonthCard = new Box();
        avgExpensesPerMonthCard.getContent().addComponent(avgExpensesPerMonthChart.createExpensePerMonthChart(localDateStart, localDateEnd));

        Box yourTrustworksForecastCard = new Box();
        yourTrustworksForecastCard.getContent().addComponent(yourTrustworksForecastChart.createChart(localDateStart, localDateEnd));

        Box revenuePerMonthEmployeeAvgCard = new Box();
        revenuePerMonthEmployeeAvgCard.getContent().addComponent(revenuePerMonthEmployeeAvgChart.createRevenuePerMonthChart(localDateStart, localDateEnd.withDayOfMonth(1).minusMonths(1)));

        Box talentPassionCard = new Box();
        talentPassionCard.getContent().addComponent(talentPassionResultBox.create());

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
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(talentPassionCard);

    }

    public void addHistoryCharts() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        historyContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);

        final ResponsiveRow chartRow = responsiveLayout.addRow();

        createHistoryCharts(chartRow);
    }

    private void createHistoryCharts(ResponsiveRow chartRow) {
        Box averageConsultantRevenueByYearCard = new Box();
        averageConsultantRevenueByYearCard.getContent().addComponent(averageConsultantRevenueByYearChart.createRevenuePerConsultantChart());

        Box expensesPerEmployee = new Box();
        expensesPerEmployee.getContent().addComponent(expensesSalariesRevenuePerMonthChart.createExpensesPerMonthChart());

        Box utilizationPerYearCard = new Box();
        utilizationPerYearCard.getContent().addComponent(utilizationPerYearChart.createChart());

        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(expensesPerEmployee);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(averageConsultantRevenueByYearCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(utilizationPerYearCard);
    }


    public void addIndividualCharts() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        individualsContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);
        ResponsiveRow searchRow = responsiveLayout.addRow();
        final ResponsiveRow chartRow = responsiveLayout.addRow();

        AtomicReference<Image> selectedEmployeeImage = new AtomicReference<>(null);

        for (User employee : userService.findCurrentlyEmployedUsers(true, CONSULTANT)) {
            Image memberImage = photoService.getRoundMemberImage(employee, false, 60, Unit.PERCENTAGE);
            memberImage.addClickListener(event -> {
                chartRow.removeAllComponents();
                if(selectedEmployeeImage.get() != null) {
                    selectedEmployeeImage.get().removeStyleName("img-circle-gold");
                    selectedEmployeeImage.get().addStyleName("img-circle");
                }
                selectedEmployeeImage.set(memberImage);
                selectedEmployeeImage.get().removeStyleName("img-circle");
                selectedEmployeeImage.get().addStyleName("img-circle-gold");

                createIndividualCharts(chartRow, employee);
            });
            searchRow.addColumn().withDisplayRules(3, 2, 1, 1).withComponent(memberImage);
        }
    }

    public void addAdminPage() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        individualsContentRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(responsiveLayout);
        ResponsiveRow searchRow = responsiveLayout.addRow();
        final ResponsiveRow chartRow = responsiveLayout.addRow();

        chartRow.addColumn()
                .withDisplayRules(12, 12, 3, 3)
                .withComponent(new MButton(
                        "Reload Video Status",
                        clickEvent -> dashboardPreloader.loadTrustworksStatus()));
    }

    private void createIndividualCharts(ResponsiveRow chartRow, User employee) {
        System.out.println("TrustworksStatsLayout.createIndividualCharts");
        System.out.println("chartRow = " + chartRow + ", employee = " + employee);
        Box revenuePerEmployee = new Box();
        revenuePerEmployee.getContent().addComponent(revenuePerConsultantChart.createRevenuePerConsultantChart(employee));

        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(revenuePerEmployee);
    }

    private String createFiscalYearText(AtomicReference<LocalDate> currentFiscalYear) {
        return "Fiscal year " + currentFiscalYear.get().getYear() + "/" + currentFiscalYear.get().plusYears(1).format(DateTimeFormatter.ofPattern("yy"));
    }

    private void setNewButtonPressState(Button btnCompany, Button btnTeam, Button btnHistory, Button btnCustomers, Button btnAdministration, Button btnIndividuals, Button.ClickEvent event, ResponsiveRow contentRow) {
        hideAllDynamicRows();
        enableAllButtons(btnCompany, btnTeam, btnHistory, btnCustomers, btnAdministration, btnIndividuals);
        event.getButton().setEnabled(false);
        contentRow.setVisible(true);
    }

    private void enableAllButtons(Button btnCompany, Button btnTeam, Button btnHistory, Button btnCustomers, Button btnAdministration, Button btnIndividuals) {
        btnCompany.setEnabled(true);
        btnTeam.setEnabled(true);
        btnHistory.setEnabled(true);
        btnCustomers.setEnabled(true);
        btnAdministration.setEnabled(true);
        btnIndividuals.setEnabled(true);
    }

    private void hideAllDynamicRows() {
        companyContentRow.setVisible(false);
        consultantsContentRow.setVisible(false);
        historyContentRow.setVisible(false);
        customersContentRow.setVisible(false);
        administrationContentRow.setVisible(false);
        individualsContentRow.setVisible(false);
    }

}
