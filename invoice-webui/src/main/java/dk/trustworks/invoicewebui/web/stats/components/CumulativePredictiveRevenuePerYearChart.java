package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
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
public class CumulativePredictiveRevenuePerYearChart {

    @Autowired
    private CountEmployeesJob countEmployeesJob;

    public Chart createCumulativePredictiveRevenuePerYearChart() {
        System.out.println("createCumulativePredictiveRevenuePerYearChart.createCumulativePredictiveRevenuePerYearChart");
        Chart chart = new Chart();
        chart.setSizeFull();

        chart.setCaption("Cumulative Predicted Revenue");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<Double> dailyForecast = countEmployeesJob.getDailyForecast();
        LocalDate localDate = countEmployeesJob.getStartDate();
        int monthsInPeriod = Math.toIntExact(ChronoUnit.MONTHS.between(localDate, LocalDate.now()))+24;
        System.out.println("countEmployeesJob.getStartDate() = " + countEmployeesJob.getStartDate());
        System.out.println("period = " + monthsInPeriod);
        String[] categories = new String[monthsInPeriod];
        DataSeries revenueSeries = new DataSeries("Revenue");

        double monthSum = 0.0;
        int i = 0;
        categories[i++] = createFiscalYearName(localDate.minusYears(1));
        for (Double amount : dailyForecast) {
            if(localDate.getMonthValue() == 7 && localDate.getDayOfMonth() == 1) {
                revenueSeries.add(new DataSeriesItem(createFiscalYearName(localDate.minusYears(1)), Math.round(monthSum)));
                categories[i++] = createFiscalYearName(localDate.minusYears(1));
                monthSum = 0.0;
            }
            monthSum += amount;
            localDate = localDate.plusDays(1);
        }

        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    private String createFiscalYearName(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy")) + "/" + localDate.plusYears(1).format(DateTimeFormatter.ofPattern("yy"));
    }
}
