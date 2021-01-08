package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.services.FinanceService;
import dk.trustworks.invoicewebui.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class YourTrustworksForecastChart {

    private final StatisticsService statisticsService;

    private final FinanceService financeService;

    @Autowired
    public YourTrustworksForecastChart(StatisticsService statisticsService, FinanceService financeService) {
        this.statisticsService = statisticsService;
        this.financeService = financeService;
    }

    public Chart createChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setSizeFull();
        periodEnd = (periodEnd.isBefore(LocalDate.now().withDayOfMonth(1))) ? periodEnd : LocalDate.now().withDayOfMonth(1);
        int period = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

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

        GraphKeyValue[] payout = financeService.getPayoutsByPeriod(periodStart, periodEnd);

        String[] categories = new String[period];
        for (int i = 0; i < period; i++) {
            categories[i] = periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }

        if(payout.length>0) payoutSeries.setData(categories, Arrays.stream(payout).map(graphKeyValue -> graphKeyValue==null?0:(Number) graphKeyValue.getValue()).toArray(Number[]::new));

        chart.getConfiguration().addSeries(payoutSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}