package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static dk.trustworks.invoicewebui.utils.ChartUtils.createDataSeries;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class RevenuePerMonthChart {

    private final StatisticsService statisticsService;

    @Autowired
    public RevenuePerMonthChart(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public Chart createRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        return createRevenuePerMonthChart(periodStart, periodEnd, true);
    }

    public Chart createRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd, boolean showEarnings) {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Revenue, Budget, and Earnings");
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

        chart.getConfiguration().addSeries(statisticsService.calcBudgetPerMonth(periodStart, periodEnd));
        chart.getConfiguration().getxAxis().setCategories(statisticsService.getMonthCategories(periodStart, periodEnd));
        chart.getConfiguration().addSeries(statisticsService.calcRegisteredHoursRevenuePerMonth(periodStart, periodEnd));
        if(showEarnings) chart.getConfiguration().addSeries(createDataSeries(statisticsService.calcActualRevenuePerMonth(periodStart, (periodEnd.isBefore(LocalDate.now()))?periodEnd:LocalDate.now().withDayOfMonth(1)), "Invoiced Amount", "#CFD6E3"));
        if(showEarnings) chart.getConfiguration().addSeries(statisticsService.calcEarningsPerMonth(periodStart, (periodEnd.isBefore(LocalDate.now()))?periodEnd:LocalDate.now().withDayOfMonth(1)));
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}