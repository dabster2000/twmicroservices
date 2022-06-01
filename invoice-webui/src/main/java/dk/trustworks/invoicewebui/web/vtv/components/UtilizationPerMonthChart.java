package dk.trustworks.invoicewebui.web.vtv.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.EmployeeAggregateData;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.BiService;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import dk.trustworks.invoicewebui.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class UtilizationPerMonthChart {

    private final UserService userService;

    private final BiService biService;

    private final TeamRestService teamRestService;

    @Autowired
    public UtilizationPerMonthChart(UserService userService, BiService biService, TeamRestService teamRestService) {
        this.userService = userService;
        this.biService = biService;
        this.teamRestService = teamRestService;
    }

    public Chart createGroupUtilizationPerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        return createGroupUtilizationPerMonthChart(periodStart, periodEnd, null);
    }

    public Chart createGroupUtilizationPerMonthChart(LocalDate periodStart, LocalDate periodEnd, String... teamuuids) {
        System.out.println("UtilizationPerMonthChart.createUtilizationPerMonthChart");
        System.out.println("periodStart = " + periodStart + ", periodEnd = " + periodEnd + ", teamuuids = " + Arrays.deepToString(teamuuids));
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
        chart.getConfiguration().getLegend().setEnabled(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0) +' %'");
        chart.getConfiguration().setTooltip(tooltip);

        double[] monthTotalNetAvailabilites = new double[monthPeriod+1];
        double[] monthTotalGrossAvailabilites = new double[monthPeriod+1];
        double[] monthBudgetUtilization = new double[monthPeriod+1];
        double[] monthActualUtilization = new double[monthPeriod+1];
        double[] monthEmployeeCount = new double[monthPeriod+1];

        List<EmployeeAggregateData> employeeAggregateData = biService.getEmployeeAggregateDataByPeriod(periodStart, periodEnd);

        for (int m = 0; m < monthPeriod; m++) {
            LocalDate localDate = periodStart.plusMonths(m);
            List<User> consultants = (teamuuids==null)?
                    userService.findWorkingUsersByDate(localDate, true, ConsultantType.CONSULTANT):
                    teamRestService.getUniqueUsersFromTeamsByMonth(localDate, teamuuids);
            for (User user : consultants) {
                Optional<EmployeeAggregateData> employeeData = employeeAggregateData.stream().filter(e -> e.getMonth().isEqual(localDate) && e.getUseruuid().equals(user.getUuid())).findFirst();
                if(!employeeData.isPresent() || !employeeData.get().getStatusType().equals(StatusType.ACTIVE)) continue;

                monthEmployeeCount[m] += 1.0;
                double budget = employeeData.map(EmployeeAggregateData::getBudgetHours).orElse(0.0);

                monthBudgetUtilization[m] += budget;

                double netAvailability = employeeData.map(EmployeeAggregateData::getNetAvailableHours).orElse(0.0);//document.map(AvailabilityDocument::getNetAvailableHours).orElse(0.0);

                monthTotalNetAvailabilites[m] += netAvailability;

                double grossAvailability = employeeData.map(EmployeeAggregateData::getGrossAvailableHours).orElse(0.0);//document.map(AvailabilityDocument::getGrossAvailableHours).orElse(0.0);
                monthTotalGrossAvailabilites[m] += grossAvailability;
                double actualUtilization = employeeData.map(EmployeeAggregateData::getActualUtilization).orElse(0.0);
                monthActualUtilization[m] += actualUtilization;
            }
        }

        ListSeries budgetListSeries = new ListSeries("Net Budget utilization");
        PlotOptionsAreaspline poc1 = new PlotOptionsAreaspline();
        poc1.setColor(new SolidColor("#123375"));
        budgetListSeries.setPlotOptions(poc1);
        for (int j = 0; j < monthPeriod; j++) {
            budgetListSeries.addData(Math.round((monthBudgetUtilization[j] / monthTotalNetAvailabilites[j]) * 100.0));
        }

        chart.getConfiguration().addSeries(budgetListSeries);

        ListSeries grossBudgetListSeries = new ListSeries("Gross Budget utilization");
        PlotOptionsSpline poc3 = new PlotOptionsSpline();
        poc3.setColor(new SolidColor("#A3D3D2"));
        poc3.setThreshold(80);
        poc3.setNegativeColor(new SolidColor("#FD5F5B"));
        grossBudgetListSeries.setPlotOptions(poc3);
        for (int j = 0; j < monthPeriod; j++) {
            grossBudgetListSeries.addData(Math.round((monthBudgetUtilization[j] / monthTotalGrossAvailabilites[j]) * 100.0));
        }

        chart.getConfiguration().addSeries(grossBudgetListSeries);

        ListSeries actualDataSeries = new ListSeries("Actual utilization");
        PlotOptionsSpline poc2 = new PlotOptionsSpline();
        poc2.setColor(new SolidColor("#54D69E"));
        poc2.setThreshold(80);
        poc2.setNegativeColor(new SolidColor("#FD5F5B"));
        actualDataSeries.setPlotOptions(poc2);
        for (int j = 0; j < monthPeriod; j++) {
            actualDataSeries.addData(Math.round((monthActualUtilization[j] / monthEmployeeCount[j]) * 100.0));
        }
        chart.getConfiguration().addSeries(actualDataSeries);

        chart.getConfiguration().getxAxis().setCategories(StatisticsService.getMonthCategories(periodStart, periodEnd));
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    public Chart createConsultantUtilizationPerMonthChart(LocalDate periodStart, LocalDate periodEnd, User user) {
        int monthPeriod = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption(user.getFirstname()+" "+user.getLastname()+" Budget and Realized Utilization");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0) +' %'");
        chart.getConfiguration().setTooltip(tooltip);

        double[] monthTotalNetAvailabilites = new double[monthPeriod+1];
        double[] monthTotalGrossAvailabilites = new double[monthPeriod+1];
        double[] monthAvailabilites = new double[monthPeriod+1];

        List<EmployeeAggregateData> data = biService.getEmployeeAggregateDataByPeriod(periodStart, periodEnd);
        for (int m = 0; m < monthPeriod; m++) {
            LocalDate localDate = periodStart.plusMonths(m);
            double budget = data.stream().filter(e -> e.getUseruuid().equals(user.getUuid()) && e.getMonth().isEqual(localDate)).mapToDouble(EmployeeAggregateData::getBudgetHours).sum();//budgetDocuments.stream().filter(b -> b.getUser().getUuid().equals(user.getUuid()) && b.getMonth().isEqual(localDate.withDayOfMonth(1))).mapToDouble(BudgetDocument::getBudgetHours).sum();
            monthAvailabilites[m] += budget;
            monthTotalNetAvailabilites[m] += data.stream().filter(e -> e.getUseruuid().equals(user.getUuid()) && e.getMonth().isEqual(localDate)).mapToDouble(EmployeeAggregateData::getNetAvailableHours).sum();//netAvailability;
            monthTotalGrossAvailabilites[m] += data.stream().filter(e -> e.getUseruuid().equals(user.getUuid()) && e.getMonth().isEqual(localDate)).mapToDouble(EmployeeAggregateData::getGrossAvailableHours).sum();//grossAvailability;

        }

        ListSeries budgetListSeries = new ListSeries("Net Budget utilization");
        PlotOptionsAreaspline poc1 = new PlotOptionsAreaspline();
        poc1.setColor(new SolidColor("#123375"));
        budgetListSeries.setPlotOptions(poc1);
        for (int j = 0; j < monthPeriod; j++) {
            budgetListSeries.addData(Math.round((monthAvailabilites[j] / monthTotalNetAvailabilites[j]) * 100.0));
        }

        chart.getConfiguration().addSeries(budgetListSeries);

        ListSeries grossBudgetListSeries = new ListSeries("Gross Budget utilization");
        PlotOptionsSpline poc3 = new PlotOptionsSpline();
        poc3.setColor(new SolidColor("#A3D3D2"));
        poc3.setThreshold(80);
        poc3.setNegativeColor(new SolidColor("#FD5F5B"));
        grossBudgetListSeries.setPlotOptions(poc3);
        for (int j = 0; j < monthPeriod; j++) {
            grossBudgetListSeries.addData(Math.round((monthAvailabilites[j] / monthTotalGrossAvailabilites[j]) * 100.0));
        }

        chart.getConfiguration().addSeries(grossBudgetListSeries);

        DataSeries actualDataSeries = new DataSeries("Actual utilization");
        PlotOptionsSpline poc2 = new PlotOptionsSpline();
        poc2.setColor(new SolidColor("#54D69E"));
        poc2.setThreshold(80);
        poc2.setNegativeColor(new SolidColor("#FD5F5B"));
        actualDataSeries.setPlotOptions(poc2);
        actualDataSeries.setData(getConsultantAverageAllocationByYear(periodStart, periodEnd, user));
        chart.getConfiguration().addSeries(actualDataSeries);

        chart.getConfiguration().getxAxis().setCategories(StatisticsService.getMonthCategories(periodStart, periodEnd));
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    private List<DataSeriesItem> getConsultantAverageAllocationByYear(LocalDate startDate, LocalDate endDate, User user) {
        startDate = startDate.withDayOfMonth(1);
        List<DataSeriesItem> dataSeriesItemList = new ArrayList<>();

        List<EmployeeAggregateData> data = biService.getEmployeeAggregateDataByPeriod(startDate, endDate);

        do {
            double totalBillableHours = 0.0;
            double totalAvailableHours = 0.0;
            double totalAllocation;
            double countEmployees = 0.0;

            LocalDate finalStartDate1 = startDate;
            double billableWorkHours = data.stream().filter(e -> e.getUseruuid().equals(user.getUuid()) && e.getMonth().isEqual(finalStartDate1)).mapToDouble(EmployeeAggregateData::getRegisteredHours).sum();//revenueService.getRegisteredHoursForSingleMonthAndSingleConsultant(user.getUuid(), finalStartDate);


            totalAvailableHours += data.stream().filter(e -> e.getUseruuid().equals(user.getUuid()) && e.getMonth().isEqual(finalStartDate1)).mapToDouble(EmployeeAggregateData::getNetAvailableHours).sum(); //availabilityDocument.get().getNetAvailableHours();
            totalBillableHours += billableWorkHours;
            countEmployees++;

            totalAllocation = Math.floor(((totalBillableHours / countEmployees) / (totalAvailableHours / countEmployees)) * 100.0);
            dataSeriesItemList.add(new DataSeriesItem(startDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), NumberUtils.round(totalAllocation, 0)));
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(endDate));
        return dataSeriesItemList;
    }


}