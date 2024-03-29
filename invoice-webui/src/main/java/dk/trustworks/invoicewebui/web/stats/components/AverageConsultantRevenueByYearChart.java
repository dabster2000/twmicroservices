package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.services.AvailabilityService;
import dk.trustworks.invoicewebui.services.FinanceService;
import dk.trustworks.invoicewebui.services.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class AverageConsultantRevenueByYearChart {

    private final FinanceService financeService;

    private final RevenueService revenueService;

    private final AvailabilityService availabilityService;

    @Autowired
    public AverageConsultantRevenueByYearChart(FinanceService financeService, RevenueService revenueService, AvailabilityService availabilityService) {
        this.financeService = financeService;
        this.revenueService = revenueService;
        this.availabilityService = availabilityService;
    }

    public Chart createRevenuePerConsultantChart() {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Average Revenue Per Consultant");
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
        PlotOptionsAreaspline poc7 = new PlotOptionsAreaspline();
        poc7.setColor(new SolidColor("#123375"));
        revenueSeries.setPlotOptions(poc7);

        LocalDate currentDate = LocalDate.of(2016, 7, 1);

        Map<LocalDate, Double> averagePerYearMap = new HashMap<>();
        LocalDate periodStart = currentDate;
        do {
            double sum = 0.0;

            averagePerYearMap.put(periodStart, 0.0);
            int countMonthsWithExpenses = 0;

            for (int m = 0; m < 12; m++) {
                if(periodStart.plusMonths(m).isAfter(LocalDate.now().withDayOfMonth(1))) break;
                double expenses = financeService.calcAllExpensesByMonth(periodStart.plusMonths(m));
                if(expenses <= 0.0) continue;
                double revenue = revenueService.getRegisteredRevenueForSingleMonth(periodStart.plusMonths(m));
                double countUsers = availabilityService.countActiveConsultantsByMonth(periodStart.plusMonths(m));
                sum += (revenue - expenses) / countUsers;
                countMonthsWithExpenses++;
            }

            if(sum<=0.0) {
                periodStart = periodStart.plusYears(1);
                continue;
            }

            averagePerYearMap.put(periodStart, sum / countMonthsWithExpenses);
            periodStart = periodStart.plusYears(1);
        } while (periodStart.isBefore(LocalDate.now()));


        for (LocalDate date : averagePerYearMap.keySet().stream().sorted(LocalDate::compareTo).collect(Collectors.toList())) {
            revenueSeries.add(new DataSeriesItem(date.format(DateTimeFormatter.ofPattern("yyyy")), averagePerYearMap.get(date)));
            chart.getConfiguration().getxAxis().addCategory(date.format(DateTimeFormatter.ofPattern("yyyy")));
        }

        chart.getConfiguration().addSeries(revenueSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}