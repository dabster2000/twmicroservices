package dk.trustworks.invoicewebui.web.employee.components.charts;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.BudgetService;
import dk.trustworks.invoicewebui.services.RevenueService;
import dk.trustworks.invoicewebui.services.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class BillableConsultantHoursPerMonthChart {

    private final RevenueService revenueService;

    private final BudgetService budgetService;

    private final WorkService workService;

    @Autowired
    public BillableConsultantHoursPerMonthChart(RevenueService revenueService, BudgetService budgetService, WorkService workService) {
        this.revenueService = revenueService;
        this.budgetService = budgetService;
        this.workService = workService;
    }

    @Cacheable("revenueChart")
    public Chart createBillableConsultantHoursPerMonthChart(User user, LocalDate periodStart, LocalDate periodEnd) {
        System.out.println("ConsultantHoursPerMonthChart.createBillableConsultantHoursPerMonthChart");
        System.out.println("user = [" + user + "], periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);
        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);

        chart.setCaption("Billable and budgeted hours during fiscal year 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setStacking(Stacking.NORMAL);
        chart.getConfiguration().setPlotOptions(plotOptions);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0) +' hours'");
        chart.getConfiguration().setTooltip(tooltip);

        String[] categories = new String[months];
        DataSeries revenueSeries = new DataSeries("Billable hours");
        revenueSeries.setStack("hours");
        DataSeries internalSeries = new DataSeries("Internal hours");
        internalSeries.setStack("hours");
        DataSeries budgetSeries = new DataSeries("Budgeted hours");
        budgetSeries.setStack("budget");
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            revenueSeries.add(new DataSeriesItem(stringIt(currentDate, "MMM-yyyy"), revenueService.getRegisteredHoursForSingleMonthAndSingleConsultant(user.getUuid(), currentDate)));
            if(user.getUsername().equals("marie.daugaard") || user.getUsername().equals("hans.lassen")) internalSeries.add(new DataSeriesItem(stringIt(currentDate, "MMM-yyyy"), workService.findByPeriodAndUserUUID(currentDate, currentDate.plusMonths(1), user.getUuid()).stream().filter(work -> work.getTaskuuid().equals("1d5902bf-f767-4984-a0bf-0c2d2a946ef4")).mapToDouble(value -> value.getWorkduration()).sum()));
            budgetSeries.add(new DataSeriesItem(stringIt(currentDate,"MMM-yyyy"), budgetService.getConsultantBudgetHoursByMonth(user.getUuid(), currentDate)));

            categories[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(budgetSeries);
        if(user.getUsername().equals("marie.daugaard") || user.getUsername().equals("hans.lassen")) chart.getConfiguration().addSeries(internalSeries);
        chart.getConfiguration().addSeries(revenueSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}