package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.jobs.CountEmployeesJob;
import dk.trustworks.invoicewebui.model.Expense;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ExpensesPerMonthChart {

    private final StatisticsService statisticsService;

    private final ExpenseRepository expenseRepository;

    private final CountEmployeesJob countEmployees;

    private final GraphKeyValueRepository graphKeyValueRepository;

    @Autowired
    public ExpensesPerMonthChart(StatisticsService statisticsService, ExpenseRepository expenseRepository, CountEmployeesJob countEmployees, GraphKeyValueRepository graphKeyValueRepository) {
        this.statisticsService = statisticsService;
        this.expenseRepository = expenseRepository;
        this.countEmployees = countEmployees;
        this.graphKeyValueRepository = graphKeyValueRepository;
    }

    public Chart createExpensesPerMonthChart() {
        System.out.println("ExpensesPerMonthChart.createExpensesPerMonthChart");
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        LocalDate periodStart = countEmployees.getStartDate();
        LocalDate periodEnd = LocalDate.now().withDayOfMonth(1);

        chart.setCaption("Expenses per Employee");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        chart.getConfiguration().getxAxis().setCategories(statisticsService.getCategories(periodStart, periodEnd));

        DataSeries expensesSeries = new DataSeries("Expenses (no salaries)");
        DataSeries salariesSeries = new DataSeries("Salaries (no expenses)");

        double expensesSum = 0.0;
        double salariesSum = 0.0;
        double revenueSum = 0.0;
        int count = 1;

        int average = 3;

        DataSeries revenueSeries = new DataSeries("Revenue");
        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.findRevenueByMonthByPeriod(periodStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")))).collect(Collectors.toList());


        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            if(amountPerItemList.size() <= i) continue;

            double expense = expenseRepository.findByPeriod(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant())).stream().filter(expense1 -> !expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum();
            double salaries = expenseRepository.findByPeriod(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant())).stream().filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum();
            double revenue = (amountPerItemList.get(i)!=null)?amountPerItemList.get(i).getValue():0.0;

            if(expense == 0) {
                count = 1;
                revenueSum = 0.0;
                expensesSum = 0.0;
                salariesSum = 0.0;
                continue;
            }

            expensesSum += (expense / countEmployees.getUsersByLocalDate(currentDate).size());
            salariesSum += (salaries / countEmployees.getUsersByLocalDate(currentDate).size());
            revenueSum += (revenue  / countEmployees.getUsersByLocalDate(currentDate).size());

            if(count == average) {
                expensesSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), expensesSum / average));
                salariesSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), salariesSum / average));
                revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), revenueSum / average));
                expensesSum = 0.0;
                salariesSum = 0.0;
                revenueSum = 0.0;
                count = 1;
                continue;
            }

            count++;
        }

        chart.getConfiguration().addSeries(expensesSeries);
        chart.getConfiguration().addSeries(salariesSeries);
        chart.getConfiguration().addSeries(revenueSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}