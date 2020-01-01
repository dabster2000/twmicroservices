package dk.trustworks.invoicewebui.web.stats.components.charts.utilization;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.AvailabilityDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class UtilizationPerYearChart {

    private UserService userService;

    private StatisticsService statisticsService;

    @Autowired
    public UtilizationPerYearChart(UserService userService, StatisticsService statisticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    public Chart createChart() {
        LocalDate periodStart = LocalDate.of(2016, 7, 1);
        int monthPeriod = (int) ChronoUnit.MONTHS.between(periodStart, LocalDate.now().withDayOfMonth(1).plusMonths(10))+1;

        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption(null);
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("%");
        chart.getConfiguration().getyAxes().getAxis(0).setMax(100);
        chart.getConfiguration().getLegend().setEnabled(true    );

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0) +' %'");
        chart.getConfiguration().setTooltip(tooltip);

        double[] monthTotalAvailabilites = new double[monthPeriod+1];
        double[] monthAvailabilites = new double[monthPeriod+1];

        LocalDate localDate = periodStart.withDayOfMonth(1);
        int m = 0;
        do {
            for (User user : userService.findWorkingUsersByDate(localDate, ConsultantType.CONSULTANT)) {
                if(user.getUsername().equals("hans.lassen") || user.getUsername().equals("tobias.kjoelsen") || user.getUsername().equals("lars.albert") || user.getUsername().equals("thomas.gammelvind")) continue;
                double budget = statisticsService.getConsultantBudgetHoursByMonth(user, localDate);
                monthAvailabilites[m] += budget;
                double availability = statisticsService.getConsultantAvailabilityByMonth(user, localDate).getNetAvailableHours();
                monthTotalAvailabilites[m] += availability;
            }
            m++;
            localDate = localDate.plusMonths(1);
        } while (m<=monthPeriod);

        DataSeries budgetListSeries = new DataSeries("Contract utilization");

        int count = 0;
        double tempSum = 0.0;
        List<DataSeriesItem> dataSeriesItemList = new ArrayList<>();
        for (int j = 0; j < monthPeriod; j++) {
            tempSum += (Math.round((monthAvailabilites[j] / monthTotalAvailabilites[j]) * 100.0));
            count++;
            if(count==12) {
                dataSeriesItemList.add(new DataSeriesItem(periodStart.plusMonths(j).format(DateTimeFormatter.ofPattern("yyyy")), tempSum / 12.0));
                tempSum = 0.0;
                count = 0;
            }
        }
        dataSeriesItemList.add(new DataSeriesItem(periodStart.plusMonths(monthPeriod-1).format(DateTimeFormatter.ofPattern("yyyy")), tempSum / count));
        budgetListSeries.setData(dataSeriesItemList);

        chart.getConfiguration().addSeries(budgetListSeries);

        DataSeries actualDataSeries = new DataSeries("Actual utilization");
        actualDataSeries.setData(getAverageAllocationByYear(periodStart));
        chart.getConfiguration().addSeries(actualDataSeries);

        chart.getConfiguration().getxAxis().setCategories(statisticsService.getCategories(periodStart, LocalDate.now().withDayOfMonth(1).plusMonths(11)));
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    private List<DataSeriesItem> getAverageAllocationByYear(LocalDate startDate) {
        startDate = startDate.withDayOfMonth(1);
        List<DataSeriesItem> dataSeriesItemList = new ArrayList<>();
        int count = 0;
        double tempSum = 0.0;
        do {
            double totalBillableHours = 0.0;
            double totalAvailableHours = 0.0;
            double countEmployees = 0.0;
            for (User user : userService.findEmployedUsersByDate(startDate, ConsultantType.CONSULTANT)) {
                if(user.getUsername().equals("hans.lassen") || user.getUsername().equals("tobias.kjoelsen") || user.getUsername().equals("lars.albert") || user.getUsername().equals("thomas.gammelvind")) continue;

                double billableWorkHours = statisticsService.getConsultantRevenueHoursByMonth(user, startDate);
                AvailabilityDocument availability = statisticsService.getConsultantAvailabilityByMonth(user, startDate);
                if (availability == null || !availability.getStatusType().equals(StatusType.ACTIVE)) {
                    continue;
                }
                totalAvailableHours += availability.getNetAvailableHours();
                totalBillableHours += billableWorkHours;
                countEmployees++;
            }
            tempSum += Math.floor(((totalBillableHours / countEmployees) / (totalAvailableHours / countEmployees)) * 100.0);
            count++;
            if(count==12) {
                dataSeriesItemList.add(new DataSeriesItem(startDate.format(DateTimeFormatter.ofPattern("yyyy")), NumberUtils.round(tempSum / 12, 0)));
                tempSum = 0.0;
                count = 0;
            }
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(LocalDate.now()));
        dataSeriesItemList.add(new DataSeriesItem(startDate.format(DateTimeFormatter.ofPattern("yyyy")), NumberUtils.round(tempSum / count, 0)));
        return dataSeriesItemList;
    }
}