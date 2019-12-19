package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.dto.ExpenseDocument;
import dk.trustworks.invoicewebui.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static dk.trustworks.invoicewebui.utils.ChartUtils.createDataSeries;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class AvgExpensesPerMonthChart {

    private final StatisticsService statisticsService;

    @Autowired
    public AvgExpensesPerMonthChart(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public Chart createRevenuePerMonthChart() {
        System.out.println("RevenuePerMonthChart.createRevenuePerMonthChart");
        LocalDate periodStart = LocalDate.of(2016, 7, 1);
        LocalDate periodEnd = LocalDate.now().withDayOfMonth(1).minusMonths(1);
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Average Expenses");
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



        PlotOptionsArea plotOptionsArea = new PlotOptionsArea();
        plotOptionsArea.setColor(new SolidColor(18, 51, 117));

        DataSeries salarySeries = new DataSeries("Average salaries");
        salarySeries.setPlotOptions(plotOptionsArea);

        DataSeries sharedExpensesSeries = new DataSeries("Average shared expenses");
        sharedExpensesSeries.setPlotOptions(plotOptionsArea);

        DataSeries staffSalarySeries = new DataSeries("Average staff salaries");
        staffSalarySeries.setPlotOptions(plotOptionsArea);

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        String[] monthNames = new String[months];
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            List<ExpenseDocument> expensesByMonth = statisticsService.getExpensesByMonth(currentDate);

            salarySeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(expensesByMonth.stream().mapToDouble(ExpenseDocument::getSalary).average().orElse(0.0))));
            sharedExpensesSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(expensesByMonth.stream().mapToDouble(ExpenseDocument::getSharedExpense).average().orElse(0.0))));
            staffSalarySeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(expensesByMonth.stream().mapToDouble(ExpenseDocument::getStaffSalaries).average().orElse(0.0))));

            monthNames[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }

        chart.getConfiguration().getxAxis().setCategories(monthNames);
        chart.getConfiguration().addSeries(salarySeries);
        chart.getConfiguration().addSeries(sharedExpensesSeries);
        chart.getConfiguration().addSeries(staffSalarySeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}