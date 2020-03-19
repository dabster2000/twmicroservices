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

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ExpensesPerMonthChart {

    private final StatisticsService statisticsService;

    @Autowired
    public ExpensesPerMonthChart(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public Chart createExpensePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        //LocalDate periodStart = LocalDate.of(2016, 7, 1);
        //LocalDate periodEnd = LocalDate.now().withDayOfMonth(1).minusMonths(1);
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Expenses per Month");
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
        yAxis.setTitle(new AxisTitle("kr"));
        StackLabels sLabels = new StackLabels(true);
        yAxis.setStackLabels(sLabels);
        chart.getConfiguration().addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        PlotOptionsColumn poc4 = new PlotOptionsColumn();
        poc4.setColor(new SolidColor("#CFD6E3"));
        ListSeries salarySeries = new ListSeries("Average salaries");
        salarySeries.setPlotOptions(poc4);

        ListSeries sharedExpensesSeries = new ListSeries("Average shared expenses");
        PlotOptionsColumn poc2 = new PlotOptionsColumn();
        poc2.setColor(new SolidColor("#7084AC"));
        sharedExpensesSeries.setPlotOptions(poc2);

        ListSeries staffSalarySeries = new ListSeries("Average staff salaries");
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#123375"));
        staffSalarySeries.setPlotOptions(poc3);

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        String[] monthNames = new String[months];
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            List<ExpenseDocument> expensesByMonth = statisticsService.getExpensesByMonth(currentDate);

            salarySeries.addData(Math.round(expensesByMonth.stream().mapToDouble(ExpenseDocument::getSalary).sum()));
            sharedExpensesSeries.addData(Math.round(expensesByMonth.stream().mapToDouble(ExpenseDocument::getSharedExpense).sum()));
            staffSalarySeries.addData(Math.round(expensesByMonth.stream().mapToDouble(ExpenseDocument::getStaffSalaries).sum()));

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