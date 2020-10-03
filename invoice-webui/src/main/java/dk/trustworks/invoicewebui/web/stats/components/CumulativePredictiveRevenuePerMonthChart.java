package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.jobs.CountEmployeesJob;
import dk.trustworks.invoicewebui.model.IncomeForecast;
import dk.trustworks.invoicewebui.repositories.IncomeForcastRepository;
import dk.trustworks.invoicewebui.services.RevenueService;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class CumulativePredictiveRevenuePerMonthChart {

    @Autowired
    private CountEmployeesJob countEmployeesJob;

    @Autowired
    private RevenueService revenueService;

    @Autowired
    IncomeForcastRepository incomeForcastRepository;

    public Chart createCumulativePredictiveRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        System.out.println("CumulativePredictiveRevenuePerMonthChart.createCumulativePredictiveRevenuePerMonthChart");
        System.out.println("periodStart = " + periodStart + ", periodEnd = " + periodEnd);

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
        int months = (int) ChronoUnit.MONTHS.between(periodStart, LocalDate.now().withDayOfMonth(1));
        System.out.println("Registered months = " + months);
        if(months>12) months = 12;
        System.out.println("Adjusted months = " + months);
        String[] monthNames = new String[12];

        DataSeries revenueSeries = new DataSeries("Revenue");
        PlotOptionsAreaspline plotOptionsArea = new PlotOptionsAreaspline();
        plotOptionsArea.setColor(new SolidColor("#123375"));
        revenueSeries.setPlotOptions(plotOptionsArea);

        //int month = periodStart.getMonthValue();
        //double monthSum = 0.0;

        //String[] monthNames = new String[months];//categories[i++] = periodStart.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM-yyyy"));

        int historicalMonthsCount = 0;

        List<IncomeForecast> incomeForecastList = incomeForcastRepository.findByCreatedAndItemtypeOrderBySortAsc(LocalDate.now().minusDays(1), "INCOME");

        System.out.println("Known dates: RUNNING");

        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            monthNames[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
            revenueSeries.add(new DataSeriesItem(stringIt(currentDate, "MMM-yyyy"), revenueService.getRegisteredRevenueForSingleMonth(currentDate)));
            historicalMonthsCount++;
            System.out.println("currentDate = " + monthNames[i]);
            System.out.println("historicalMonthsCount = " + historicalMonthsCount);
            System.out.println("statisticsService.getMonthRevenue(currentDate) = " + revenueService.getRegisteredRevenueForSingleMonth(currentDate));
        }

        System.out.println("Known dates: DONE");

        System.out.println("historicalMonthsCount = " + historicalMonthsCount);

        System.out.println("Forecast dates: RUNNING");

        for (int i = historicalMonthsCount; i < 12; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            revenueSeries.add(new DataSeriesItem(stringIt(currentDate, "MMM-yyyy"), NumberUtils.round(incomeForecastList.get(i-historicalMonthsCount).getAmount(),0)));
            monthNames[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
            System.out.println("currentDate = " + monthNames[i]);
            System.out.println("statisticsService.getMonthRevenue(currentDate) = " + revenueService.getRegisteredRevenueForSingleMonth(currentDate));
        }

        System.out.println("Forecast dates: DONE");
/*
        for (Double amount : dailyForecast) {
            if(periodStart.getMonthValue() != month) {
                revenueSeries.add(new DataSeriesItem(periodStart.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(monthSum)));
                monthNames[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
                monthSum = 0.0;
                month = periodStart.getMonthValue();
            }
            monthSum += amount;
            periodStart = periodStart.plusMonths(1);
        }
        */

        chart.getConfiguration().getxAxis().setCategories(monthNames);
        chart.getConfiguration().addSeries(revenueSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}
