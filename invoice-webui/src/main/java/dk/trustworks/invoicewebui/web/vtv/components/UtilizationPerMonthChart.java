package dk.trustworks.invoicewebui.web.vtv.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
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

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class UtilizationPerMonthChart {

    private UserService userService;

    private StatisticsService statisticsService;

    @Autowired
    public UtilizationPerMonthChart(UserService userService, StatisticsService statisticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    public Chart createUtilizationPerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        int monthPeriod = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Budget and Realized Utilization");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getyAxes().getAxis(0).setMax(100);
        chart.getConfiguration().getLegend().setEnabled(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0) +' %'");
        chart.getConfiguration().setTooltip(tooltip);

        double[] monthTotalAvailabilites = new double[monthPeriod+1];
        double[] monthAvailabilites = new double[monthPeriod+1];

        for (int m = 0; m < monthPeriod; m++) {
            LocalDate localDate = periodStart.plusMonths(m);
            for (User user : userService.findWorkingUsersByDate(localDate, ConsultantType.CONSULTANT)) {
                if(user.getUsername().equals("hans.lassen") || user.getUsername().equals("tobias.kjoelsen") || user.getUsername().equals("lars.albert") || user.getUsername().equals("thomas.gammelvind")) continue;
                double budget = statisticsService.getConsultantBudgetHoursByMonth(user, localDate);
                monthAvailabilites[m] += budget;
                double availability = statisticsService.getConsultantAvailabilityByMonth(user, localDate).getNetAvailableHours();
                monthTotalAvailabilites[m] += availability;
            }
        }
        /*
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

         */

        ListSeries budgetListSeries = new ListSeries("Budget utilization");
        PlotOptionsAreaspline poc1 = new PlotOptionsAreaspline();
        poc1.setColor(new SolidColor("#123375"));
        budgetListSeries.setPlotOptions(poc1);
        for (int j = 0; j < monthPeriod; j++) {
            budgetListSeries.addData(Math.round((monthAvailabilites[j] / monthTotalAvailabilites[j]) * 100.0));
        }

        chart.getConfiguration().addSeries(budgetListSeries);

        DataSeries actualDataSeries = new DataSeries("Actual utilization");
        PlotOptionsSpline poc2 = new PlotOptionsSpline();
        poc2.setColor(new SolidColor("#54D69E"));
        poc2.setThreshold(80);
        poc2.setNegativeColor(new SolidColor("#FD5F5B"));
        actualDataSeries.setPlotOptions(poc2);
        actualDataSeries.setData(getAverageAllocationByYear(periodStart));
        chart.getConfiguration().addSeries(actualDataSeries);

        chart.getConfiguration().getxAxis().setCategories(statisticsService.getMonthCategories(periodStart, periodEnd));
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    private List<DataSeriesItem> getAverageAllocationByYear(LocalDate startDate) {
        startDate = startDate.withDayOfMonth(1);
        List<DataSeriesItem> dataSeriesItemList = new ArrayList<>();
        do {
            double totalBillableHours = 0.0;
            double totalAvailableHours = 0.0;
            double totalAllocation = 0.0;
            double countEmployees = 0.0;
            //System.out.println("*** AVAILABILITY ***");
            for (User user : userService.findEmployedUsersByDate(startDate, ConsultantType.CONSULTANT)) {
                if(user.getUsername().equals("hans.lassen") || user.getUsername().equals("tobias.kjoelsen") || user.getUsername().equals("lars.albert") || user.getUsername().equals("thomas.gammelvind")) continue;

                //System.out.print(user.getUsername()+";");

                double billableWorkHours = statisticsService.getConsultantRevenueHoursByMonth(user, startDate);
                //System.out.print(billableWorkHours+";");
                AvailabilityDocument availability = statisticsService.getConsultantAvailabilityByMonth(user, startDate);
                if (availability == null || !availability.getStatusType().equals(StatusType.ACTIVE)) {
                    //System.out.println("user availability is null or not active = " + user.getUsername());
                    continue;
                    //availability = new AvailabilityDocument(user, startDate, 0.0, 0.0, 0.0, ConsultantType.CONSULTANT, StatusType.TERMINATED);
                }
                //System.out.print(availability.getNetAvailableHours()+";");
                totalAvailableHours += availability.getNetAvailableHours();
                totalBillableHours += billableWorkHours;
                //double monthAllocation = 0.0;
                //if (billableWorkHours > 0.0 && availability.getNetAvailableHours() > 0.0) {
                //    monthAllocation = (billableWorkHours / availability.getNetAvailableHours()) * 100.0;
                //}
                //System.out.print(monthAllocation+";");
                countEmployees++;
                //totalAllocation += monthAllocation;
                //System.out.println();
            }
            totalAllocation = Math.floor(((totalBillableHours / countEmployees) / (totalAvailableHours / countEmployees)) * 100.0);
            //System.out.println("allocation = " + totalAllocation);
            //System.out.println("countEmployees = " + countEmployees);
            dataSeriesItemList.add(new DataSeriesItem(startDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), NumberUtils.round(totalAllocation, 0)));
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(LocalDate.now()));
        return dataSeriesItemList;
    }
}