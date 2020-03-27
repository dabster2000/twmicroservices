package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.jobs.CountEmployeesJob;
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
public class CumulativePredictiveRevenuePerMonthChart {

    @Autowired
    private CountEmployeesJob countEmployeesJob;

    public Chart createCumulativePredictiveRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        System.out.println("CumulativePredictiveRevenuePerMonthChart.createCumulativePredictiveRevenuePerMonthChart");
        Chart chart = new Chart();
        chart.setSizeFull();

        chart.setCaption("Predicted Monthly Revenue");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<Double> dailyForecast = countEmployeesJob.getDailyForecast();
        int monthsInPeriod = Math.toIntExact(ChronoUnit.MONTHS.between(periodEnd, periodEnd));
        String[] categories = new String[monthsInPeriod];
        DataSeries revenueSeries = new DataSeries("Revenue");
        PlotOptionsAreaspline plotOptionsArea = new PlotOptionsAreaspline();
        plotOptionsArea.setColor(new SolidColor("#123375"));
        revenueSeries.setPlotOptions(plotOptionsArea);

        int month = periodStart.getMonthValue();
        double monthSum = 0.0;
        int i = 0;
        categories[i++] = periodStart.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        for (Double amount : dailyForecast) {
            if(periodStart.getMonthValue() != month) {
                revenueSeries.add(new DataSeriesItem(periodStart.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(monthSum)));
                categories[i++] = periodStart.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM-yyyy"));
                monthSum = 0.0;
                month = periodStart.getMonthValue();
            }
            monthSum += amount;
            periodStart = periodStart.plusMonths(1);
        }

        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}
