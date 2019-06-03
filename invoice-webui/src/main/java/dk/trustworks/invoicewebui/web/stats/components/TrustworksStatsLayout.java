package dk.trustworks.invoicewebui.web.stats.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.dashboard.cards.DashboardBoxCreator;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardImpl;
import dk.trustworks.invoicewebui.web.model.stats.BudgetItem;
import dk.trustworks.invoicewebui.web.model.stats.ExpenseItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.haijian.Exporter;
import org.vaadin.viritin.button.MButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by hans on 20/09/2017.
 */
@SpringComponent
@SpringUI
public class TrustworksStatsLayout extends VerticalLayout {

    @Autowired
    private RevenuePerMonthChart revenuePerMonthChart;

    @Autowired
    private AllRevenuePerMonthChart allRevenuePerMonthChart;

    @Autowired
    private CumulativeRevenuePerMonthChart cumulativeRevenuePerMonthChart;

    @Autowired
    private TopGrossingConsultantsChart topGrossingConsultantsChart;

    //@Autowired
    //private ConsultantHoursPerMonthChart consultantHoursPerMonthChart;

    @Autowired
    private AverageConsultantRevenueByYearChart averageConsultantRevenueByYearChart;

    @Autowired
    private AverageConsultantRevenueChart averageConsultantRevenueChart;

    @Autowired
    private AverageConsultantAllocationChart averageConsultantAllocationChart;

    @Autowired
    private RevenuePerMonthEmployeeAvgChart revenuePerMonthEmployeeAvgChart;

    //@Autowired
    //private CumulativePredictiveRevenuePerMonthChart cumulativePredictiveRevenuePerMonthChart;

    //@Autowired
    //private CumulativePredictiveRevenuePerYearChart cumulativePredictiveRevenuePerYearChart;

    @Autowired
    private YourTrustworksForecastChart yourTrustworksForecastChart;

    @Autowired
    private BudgetTable budgetTable;

    @Autowired
    private ExpenseTable expenseTable;

    @Autowired
    private ConsultantsBudgetRealizationChart consultantsBudgetRealizationChart;

    @Autowired
    private ExpensesPerMonthChart expensesPerMonthChart;

    @Autowired
    private RevenuePerConsultantChart revenuePerConsultantChart;

    @Autowired
    private UserService userService;

    @Autowired
    private DashboardBoxCreator dashboardBoxCreator;


    public TrustworksStatsLayout init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        ResponsiveRow searchRow = responsiveLayout.addRow();

        ResponsiveRow boxRow = responsiveLayout.addRow();
        boxRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getGoodPeopleBox()));
        boxRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getCumulativeGrossRevenue()));
        boxRow.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getPayout()));


        final ResponsiveRow chartRow = responsiveLayout.addRow();

        LocalDate startFiscalPeriod = LocalDate.of(2014, 7, 1);
        ComboBox<LocalDate> fiscalPeriodStartComboBox = new ComboBox<>();
        ComboBox<LocalDate> fiscalPeriodEndComboBox = new ComboBox<>();
        LocalDate currentFiscalYear = DateUtils.getCurrentFiscalStartDate();
        List<LocalDate> fiscalPeriodList = new ArrayList<>();
        while(startFiscalPeriod.isBefore(currentFiscalYear) || startFiscalPeriod.isEqual(currentFiscalYear)) {
            fiscalPeriodList.add(startFiscalPeriod);
            startFiscalPeriod = startFiscalPeriod.plusYears(1);
        }
        fiscalPeriodList.add(startFiscalPeriod);

        searchRow.addColumn().withDisplayRules(12, 6, 4, 3).withComponent(fiscalPeriodStartComboBox);
        searchRow.addColumn().withDisplayRules(12, 6, 4, 3).withComponent(fiscalPeriodEndComboBox);

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

        return this;
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
        Card revenuePerMonthCard = new Card();
        revenuePerMonthCard.getLblTitle().setValue("Revenue Per Month");
        revenuePerMonthCard.getContent().addComponent(revenuePerMonthChart.createRevenuePerMonthChart(localDateStart, localDateEnd));
        notification.setDescription("1 out of 10 charts created!");
        System.out.println("timeMillis 1 = " + (System.currentTimeMillis() - timeMillis));

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

        /*
        Card consultantHoursPerMonth = new Card();
        consultantHoursPerMonth.getLblTitle().setValue("Consultant Hours Per Month");
        consultantHoursPerMonth.getContent().addComponent(consultantHoursPerMonthChart.createTopGrossingConsultantsChart(localDateStart, localDateEnd));
        notification.setDescription("4 out of 10 charts created!");
        System.out.println("timeMillis 4 = " + (System.currentTimeMillis() - timeMillis));
        */
        Card averageConsultantRevenueByYearCard = new Card();
        averageConsultantRevenueByYearCard.getLblTitle().setValue("Average Revenue Per Consultant");
        averageConsultantRevenueByYearCard.getContent().addComponent(averageConsultantRevenueByYearChart.createRevenuePerConsultantChart());
        notification.setDescription("4 out of 10 charts created!");
        System.out.println("timeMillis 4 = " + (System.currentTimeMillis() - timeMillis));

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
*/
        Card revenuePerMonthEmployeeAvgCard = new Card();
        revenuePerMonthEmployeeAvgCard.getLblTitle().setValue("Average Revenue per Consultant");
        revenuePerMonthEmployeeAvgCard.getContent().addComponent(revenuePerMonthEmployeeAvgChart.createRevenuePerMonthChart(localDateStart, localDateEnd));
        notification.setDescription("8 out of 10 charts created!");
        System.out.println("timeMillis 8 = " + (System.currentTimeMillis() - timeMillis));

        Card consultantsBudgetRealizationCard = new Card();
        consultantsBudgetRealizationCard.getLblTitle().setValue("Consultant Budget Realization");
        consultantsBudgetRealizationCard.getContent().addComponent(consultantsBudgetRealizationChart.createConsultantsBudgetRealizationChart());
        notification.setDescription("9 out of 10 charts created!");
        System.out.println("timeMillis 9 = " + (System.currentTimeMillis() - timeMillis));


        Card expensesPerEmployee = new Card();
        expensesPerEmployee.getLblTitle().setValue("Average Historical Economic Overview per Employee");
        expensesPerEmployee.getContent().addComponent(expensesPerMonthChart.createExpensesPerMonthChart());
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

        /*
        Button downloadAsExcel = new Button("Download As Excel");
StreamResource excelStreamResource = new StreamResource((StreamResource.StreamSource) () -> Exporter.exportAsExcel(grid), "my-excel.xlsx");
FileDownloader excelFileDownloader = new FileDownloader(excelStreamResource);
excelFileDownloader.extend(downloadAsExcel);
         */

        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(revenuePerMonthCard);
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
        /*
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
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(averageConsultantRevenueByYearCard);
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
        /*
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(cumulativePredictiveRevenuePerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(consultantHoursPerMonth);
        */

        notification.setDelayMsec(1000);
    }

}
