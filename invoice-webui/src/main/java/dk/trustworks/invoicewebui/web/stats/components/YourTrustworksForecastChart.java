package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class YourTrustworksForecastChart {

    private final StatisticsService statisticsService;

    private final UserService userService;

    @Autowired
    public YourTrustworksForecastChart(StatisticsService statisticsService, UserService userService) {
        this.statisticsService = statisticsService;
        this.userService = userService;
    }

    public Chart createChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setSizeFull();
        int period = (int) ChronoUnit.MONTHS.between(periodStart, LocalDate.now().withDayOfMonth(1));

        chart.setCaption("Your Trustworks Forecast");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getLegend().setEnabled(false);
        chart.getConfiguration().setTitle("");

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0.0) +' %'");
        chart.getConfiguration().setTooltip(tooltip);

        XAxis x = new XAxis();
        x.setType(AxisType.CATEGORY);
        chart.getConfiguration().addxAxis(x);

        YAxis y = new YAxis();
        y.setTitle("Payout Factor");
        chart.getConfiguration().addyAxis(y);

        DataSeries payoutSeries = new DataSeries("Payout Factor");

        double forecastedExpenses = 33000;
        double forecastedSalaries = 60000;
        double forecastedConsultants = userService.findCurrentlyEmployedUsers(ConsultantType.CONSULTANT, ConsultantType.STAFF).size();
        double totalForecastedExpenses = (forecastedExpenses + forecastedSalaries) * forecastedConsultants;

        double totalCumulativeRevenue = 0.0;
        Number[] payout = new Number[period];
        String[] categories = new String[period];

        for (int i = 0; i < period; i++) {
            System.out.println("------------------------");
            LocalDate currentDate = periodStart.plusMonths(i);
            System.out.println("currentDate = " + currentDate);

            totalCumulativeRevenue += statisticsService.getMonthRevenue(currentDate);
            double grossMargin = totalCumulativeRevenue - totalForecastedExpenses;
            double grossMarginPerConsultant = grossMargin / forecastedConsultants;
            double consultantPayout = grossMarginPerConsultant * 0.1;
            payout[i] = NumberUtils.round((consultantPayout / forecastedSalaries) * 100.0 - 100.0, 2);

            categories[i] = periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }

        payoutSeries.setData(categories, payout);

        chart.getConfiguration().addSeries(payoutSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}