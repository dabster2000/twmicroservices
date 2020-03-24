package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import dk.trustworks.invoicewebui.model.ExpenseDetails;
import dk.trustworks.invoicewebui.model.dto.ExpenseDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.network.clients.EconomicsAPI;
import dk.trustworks.invoicewebui.repositories.ExpenseDetailsRepository;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Range;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.vaadin.ui.Notification.*;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ExpensesPerMonthChart {

    private final StatisticsService statisticsService;

    private final UserService userService;

    private final ExpenseDetailsRepository expenseDetailsRepository;

    @Autowired
    public ExpensesPerMonthChart(StatisticsService statisticsService, UserService userService, ExpenseDetailsRepository expenseDetailsRepository) {
        this.statisticsService = statisticsService;
        this.userService = userService;
        this.expenseDetailsRepository = expenseDetailsRepository;
    }

    public Chart createExpensePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Categorized Expenses");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setStacking(Stacking.NORMAL);
        chart.getConfiguration().setPlotOptions(plotOptionsColumn);

        YAxis yAxis = new YAxis();
        yAxis.setMin(0);
        chart.getConfiguration().getyAxis().setTitle("");
        StackLabels sLabels = new StackLabels(true);
        yAxis.setStackLabels(sLabels);
        chart.getConfiguration().addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        Map<String, Range<Integer>> listSeriesRangeMap = new HashMap<>();

        PlotOptionsColumn poc4 = new PlotOptionsColumn();
        poc4.setColor(new SolidColor("#54D69E"));
        ListSeries consultantSalarySeries = new ListSeries("Consultant salaries");
        consultantSalarySeries.setId(UUID.randomUUID().toString());
        consultantSalarySeries.setPlotOptions(poc4);
        listSeriesRangeMap.put(consultantSalarySeries.getName(), EconomicsAPI.LOENNINGER_ACCOUNTS);

        ListSeries staffSalarySeries = new ListSeries("Staff salaries");
        staffSalarySeries.setId(UUID.randomUUID().toString());
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#98E6C4"));
        staffSalarySeries.setPlotOptions(poc3);
        listSeriesRangeMap.put(staffSalarySeries.getName(), EconomicsAPI.LOENNINGER_ACCOUNTS);

        ListSeries personaleExpensesSeries = new ListSeries("Consultant expenses");
        personaleExpensesSeries.setId(UUID.randomUUID().toString());
        PlotOptionsColumn poc2 = new PlotOptionsColumn();
        poc2.setColor(new SolidColor("#CFD6E3"));
        personaleExpensesSeries.setPlotOptions(poc2);
        listSeriesRangeMap.put(personaleExpensesSeries.getName(), EconomicsAPI.PERSONALE_ACCOUNTS);

        PlotOptionsColumn poc5 = new PlotOptionsColumn();
        poc5.setColor(new SolidColor("#A0ADC7"));
        ListSeries lokaleExensesSeries = new ListSeries("Office expenses");
        lokaleExensesSeries.setId(UUID.randomUUID().toString());
        lokaleExensesSeries.setPlotOptions(poc5);
        listSeriesRangeMap.put(lokaleExensesSeries.getName(), EconomicsAPI.LOKALE_ACCOUNTS);

        PlotOptionsColumn poc6 = new PlotOptionsColumn();
        poc6.setColor(new SolidColor("#7084AC"));
        ListSeries salgExensesSeries = new ListSeries("Sales expenses");
        salgExensesSeries.setId(UUID.randomUUID().toString());
        salgExensesSeries.setPlotOptions(poc6);
        listSeriesRangeMap.put(salgExensesSeries.getName(), EconomicsAPI.SALG_ACCOUNTS);

        PlotOptionsColumn poc7 = new PlotOptionsColumn();
        poc7.setColor(new SolidColor("#415B90"));
        ListSeries productionExensesSeries = new ListSeries("Production expenses");
        productionExensesSeries.setId(UUID.randomUUID().toString());
        productionExensesSeries.setPlotOptions(poc7);
        listSeriesRangeMap.put(productionExensesSeries.getName(), EconomicsAPI.PRODUKTION_ACCOUNTS);

        PlotOptionsColumn poc8 = new PlotOptionsColumn();
        poc8.setColor(new SolidColor("#123375"));
        ListSeries administrationExensesSeries = new ListSeries("Administration expenses");
        administrationExensesSeries.setId(UUID.randomUUID().toString());
        administrationExensesSeries.setPlotOptions(poc8);
        listSeriesRangeMap.put(administrationExensesSeries.getName(), EconomicsAPI.ADMINISTRATION_ACCOUNTS);

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        String[] monthNames = new String[months];
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            List<ExpenseDocument> allExpensesByMonth = statisticsService.getAllExpensesByMonth(currentDate);

            double consultantNetSalaries = userService.calcMonthSalaries(currentDate, ConsultantType.CONSULTANT.toString());
            double staffNetSalaries = userService.calcMonthSalaries(currentDate, ConsultantType.STAFF.toString());

            double totalSalaries = Math.round(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteSalaries).sum());
            
            double forholdstal = totalSalaries / (consultantNetSalaries + staffNetSalaries);

            final double staffSalaries = NumberUtils.round(staffNetSalaries * forholdstal, 0);//(expenseSalaries - consultantSalaries) / consultantCount;
            final double consultantSalaries = NumberUtils.round(consultantNetSalaries * forholdstal, 0);

            consultantSalarySeries.addData(consultantSalaries);
            staffSalarySeries.addData(staffSalaries);
            personaleExpensesSeries.addData(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteEmployee_expenses).sum());
            lokaleExensesSeries.addData(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteHousing).sum());
            salgExensesSeries.addData(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteSales).sum());
            productionExensesSeries.addData(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteProduktion).sum());
            administrationExensesSeries.addData(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteAdministration).sum());

            monthNames[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }

        chart.addPointClickListener(event -> {
            show("Click", "pointIndex = "+event.getPointIndex()+", series = "+event.getSeries().getName(), Type.HUMANIZED_MESSAGE);
            LocalDate currentDate = periodStart.plusMonths(event.getPointIndex());
            Range<Integer> range = listSeriesRangeMap.get(event.getSeries().getName());

            Grid<ExpenseDetails> expenseDetailGrid = createExpenseDetailGrid(currentDate, range);
            UI.getCurrent().addWindow(new Window("Expense Details for " + event.getCategory(), expenseDetailGrid));
        });

        chart.getConfiguration().getxAxis().setCategories(monthNames);
        chart.getConfiguration().addSeries(consultantSalarySeries);
        chart.getConfiguration().addSeries(staffSalarySeries);
        chart.getConfiguration().addSeries(personaleExpensesSeries);
        chart.getConfiguration().addSeries(lokaleExensesSeries);
        chart.getConfiguration().addSeries(salgExensesSeries);
        chart.getConfiguration().addSeries(productionExensesSeries);
        chart.getConfiguration().addSeries(administrationExensesSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    public Grid<ExpenseDetails> createExpenseDetailGrid(LocalDate month, Range<Integer> range) {
        System.out.println("ExpensesPerMonthChart.createExpenseDetailGrid");
        System.out.println("month = " + month + ", range = " + range);

        int[] accountNumber = new int[range.getMaximum()-range.getMinimum()+1];
        for (int i = range.getMinimum(); i <= range.getMaximum(); i++) {
            accountNumber[i-range.getMinimum()] = i;
        }
        //if(range.equals(EconomicsAPI.LOENNINGER_ACCOUNTS)) accountNumber = EconomicsAPI.LOENNINGER;
        System.out.println("accountNumber = " + ArrayUtils.toString(accountNumber)  );
        List<ExpenseDetails> expenseDetailsList = expenseDetailsRepository.findByExpensedateAndAccountnumberInOrderByAmountDesc(month, accountNumber);

        Grid<ExpenseDetails> treeGrid = new Grid<>();
        treeGrid.setWidth(100, Sizeable.Unit.PERCENTAGE);


        treeGrid.addColumn(ExpenseDetails::getEntrynumber).setCaption("Entry number").setId("entrynumber-column");
        treeGrid.addColumn(ExpenseDetails::getAccountnumber).setCaption("Account Number").setId("accountnumber-column");
        treeGrid.addColumn(ExpenseDetails::getExpensedate).setCaption("Date").setId("date-column");
        treeGrid.addColumn(ExpenseDetails::getText).setCaption("Text").setId("text-column");
        treeGrid.addColumn(ExpenseDetails::getAmount).setCaption("Amount").setId("amount-column");

        treeGrid.setItems(expenseDetailsList);

        return treeGrid;
    }

}