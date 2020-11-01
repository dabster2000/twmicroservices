package dk.trustworks.invoicewebui.web.employee.components.charts;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.BudgetService;
import dk.trustworks.invoicewebui.services.RevenueService;
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

    @Autowired
    public BillableConsultantHoursPerMonthChart(RevenueService revenueService, BudgetService budgetService) {
        this.revenueService = revenueService;
        this.budgetService = budgetService;
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

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0) +' hours'");
        chart.getConfiguration().setTooltip(tooltip);

        String[] categories = new String[months];
        DataSeries revenueSeries = new DataSeries("Billable hours");
        DataSeries budgetSeries = new DataSeries("Budgeted hours");
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            revenueSeries.add(new DataSeriesItem(stringIt(currentDate, "MMM-yyyy"), revenueService.getRegisteredHoursForSingleMonthAndSingleConsultant(user.getUuid(), currentDate)));
            budgetSeries.add(new DataSeriesItem(stringIt(currentDate,"MMM-yyyy"), budgetService.getConsultantBudgetHoursByMonth(user.getUuid(), currentDate)));

            categories[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueSeries);
        chart.getConfiguration().addSeries(budgetSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}