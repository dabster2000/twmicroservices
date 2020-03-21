package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.dto.UserExpenseDocument;
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

    public Chart createUserExpensePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
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

        PlotOptionsColumn poc4 = new PlotOptionsColumn();
        poc4.setColor(new SolidColor("#54D69E"));
        ListSeries salarySeries = new ListSeries("Average salaries");
        salarySeries.setPlotOptions(poc4);

        ListSeries staffSalarySeries = new ListSeries("Average staff salaries");
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#CFD6E3"));
        staffSalarySeries.setPlotOptions(poc3);

        ListSeries sharedExpensesSeries = new ListSeries("Average shared expenses");
        PlotOptionsColumn poc2 = new PlotOptionsColumn();
        poc2.setColor(new SolidColor("#7084AC"));
        sharedExpensesSeries.setPlotOptions(poc2);

        PlotOptionsColumn poc5 = new PlotOptionsColumn();
        poc5.setColor(new SolidColor("#123375"));
        ListSeries staffExensesSeries = new ListSeries("Everage consultant expenses");
        staffExensesSeries.setPlotOptions(poc5);

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        String[] monthNames = new String[months];
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            List<UserExpenseDocument> expensesByMonth = statisticsService.getConsultantsExpensesByMonth(currentDate);

            salarySeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getSalary).sum()));
            sharedExpensesSeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getSharedExpense).sum()));
            staffSalarySeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getStaffSalaries).sum()));
            staffExensesSeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getPersonaleExpense).sum()));

            monthNames[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }

        chart.getConfiguration().getxAxis().setCategories(monthNames);
        chart.getConfiguration().addSeries(salarySeries);
        chart.getConfiguration().addSeries(staffSalarySeries);
        chart.getConfiguration().addSeries(sharedExpensesSeries);
        chart.getConfiguration().addSeries(staffExensesSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    public Chart createExpensePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Categorized Expenses From Economics");
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

        PlotOptionsColumn poc4 = new PlotOptionsColumn();
        poc4.setColor(new SolidColor("#54D69E"));
        ListSeries salarySeries = new ListSeries("Average salaries");
        salarySeries.setPlotOptions(poc4);

        ListSeries staffSalarySeries = new ListSeries("Average staff salaries");
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#CFD6E3"));
        staffSalarySeries.setPlotOptions(poc3);

        ListSeries sharedExpensesSeries = new ListSeries("Average shared expenses");
        PlotOptionsColumn poc2 = new PlotOptionsColumn();
        poc2.setColor(new SolidColor("#7084AC"));
        sharedExpensesSeries.setPlotOptions(poc2);

        PlotOptionsColumn poc5 = new PlotOptionsColumn();
        poc5.setColor(new SolidColor("#123375"));
        ListSeries staffExensesSeries = new ListSeries("Everage consultant expenses");
        staffExensesSeries.setPlotOptions(poc5);

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        String[] monthNames = new String[months];
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            List<UserExpenseDocument> expensesByMonth = statisticsService.getConsultantsExpensesByMonth(currentDate);

            salarySeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getSalary).sum()));
            sharedExpensesSeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getSharedExpense).sum()));
            staffSalarySeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getStaffSalaries).sum()));
            staffExensesSeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getPersonaleExpense).sum()));

            monthNames[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }

        chart.getConfiguration().getxAxis().setCategories(monthNames);
        chart.getConfiguration().addSeries(salarySeries);
        chart.getConfiguration().addSeries(staffSalarySeries);
        chart.getConfiguration().addSeries(sharedExpensesSeries);
        chart.getConfiguration().addSeries(staffExensesSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}