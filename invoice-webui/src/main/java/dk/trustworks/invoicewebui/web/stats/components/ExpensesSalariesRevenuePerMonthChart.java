package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ExpensesSalariesRevenuePerMonthChart {

    private final StatisticsService statisticsService;

    @Autowired
    public ExpensesSalariesRevenuePerMonthChart(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public Chart createExpensesPerMonthChart() {
        System.out.println("ExpensesPerMonthChart.createExpensesPerMonthChart");
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        LocalDate periodStart = LocalDate.of(2014, 07, 01);
        LocalDate periodEnd = LocalDate.now().withDayOfMonth(1);

        chart.setCaption("Expenses, Salaries and Revenue per Employee");
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

        DataSeries revenueSeries = new DataSeries("Revenue");
        DataSeries expensesSeries = new DataSeries("Expenses");
        DataSeries salariesSeries = new DataSeries("Salaries");

        double expensesSum = 0.0;
        double salariesSum = 0.0;
        double revenueSum = 0.0;
        int count = 1;

        int average = 3;

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        Map<LocalDate, Double> revenuePerMonth = statisticsService.calcActualRevenuePerMonth(periodStart, periodEnd.plusMonths(2));

        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            int consultantCount = (int) statisticsService.countActiveConsultantCountByMonth(currentDate);
            double sharedExpensesAndStaffSalariesByMonth = NumberUtils.round(statisticsService.getSharedExpensesAndStaffSalariesByMonth(currentDate) / consultantCount, 0);
            double consultantSalariesByMonth = NumberUtils.round(((statisticsService.getAllUserExpensesByMonth(currentDate) / consultantCount) - sharedExpensesAndStaffSalariesByMonth) , 0);
            double revenue = NumberUtils.round(revenuePerMonth.get(currentDate) / consultantCount, 0);

            if(revenue == 0.0 || sharedExpensesAndStaffSalariesByMonth == 0.0 || consultantSalariesByMonth == 0.0) {
                count = 1;
                revenueSum = 0.0;
                expensesSum = 0.0;
                salariesSum = 0.0;
                continue;
            }

            expensesSum += (sharedExpensesAndStaffSalariesByMonth);
            salariesSum += consultantSalariesByMonth;
            revenueSum += revenue;

            if(count == average) {
                expensesSeries.add(new DataSeriesItem(currentDate.minusMonths(2).format(DateTimeFormatter.ofPattern("QQQ/yy")), expensesSum / average));
                salariesSeries.add(new DataSeriesItem(currentDate.minusMonths(2).format(DateTimeFormatter.ofPattern("QQQ/yy")), salariesSum / average));
                revenueSeries.add(new DataSeriesItem(currentDate.minusMonths(2).format(DateTimeFormatter.ofPattern("QQQ/yy")), revenueSum / average));
                chart.getConfiguration().getxAxis().addCategory(currentDate.minusMonths(2).format(DateTimeFormatter.ofPattern("QQQ/yy")));
                expensesSum = 0.0;
                salariesSum = 0.0;
                revenueSum = 0.0;
                count = 1;
                continue;
            }

            count++;
        }

        chart.getConfiguration().addSeries(revenueSeries);
        chart.getConfiguration().addSeries(salariesSeries);
        chart.getConfiguration().addSeries(expensesSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}