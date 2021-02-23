package dk.trustworks.invoicewebui.web.vtv.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.AvailabilityDocument;
import dk.trustworks.invoicewebui.model.dto.BudgetDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.services.*;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class UtilizationPerMonthChart {

    private final UserService userService;

    private final BudgetService budgetService;

    private final RevenueService revenueService;

    private final AvailabilityService availabilityService;

    private final StatisticsService statisticsService;

    @Autowired
    public UtilizationPerMonthChart(UserService userService, BudgetService budgetService, RevenueService revenueService, AvailabilityService availabilityService, StatisticsService statisticsService) {
        this.userService = userService;
        this.budgetService = budgetService;
        this.revenueService = revenueService;
        this.availabilityService = availabilityService;
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

        double[] monthTotalNetAvailabilites = new double[monthPeriod+1];
        double[] monthTotalGrossAvailabilites = new double[monthPeriod+1];
        double[] monthAvailabilites = new double[monthPeriod+1];

        List<AvailabilityDocument> availabilityDocuments = availabilityService.getConsultantAvailabilityByPeriod(periodStart, periodEnd);
        List<BudgetDocument> budgetDocuments = budgetService.getConsultantBudgetHoursByPeriodDocuments(periodStart, periodEnd);
        for (int m = 0; m < monthPeriod; m++) {
            LocalDate localDate = periodStart.plusMonths(m);
            for (User user : userService.findWorkingUsersByDate(localDate, true, ConsultantType.CONSULTANT)) {
                if(user.getUsername().equals("hans.lassen") || user.getUsername().equals("tobias.kjoelsen") || user.getUsername().equals("lars.albert") || user.getUsername().equals("thomas.gammelvind")) continue;
                double budget = budgetDocuments.stream().filter(b -> b.getUser().getUuid().equals(user.getUuid()) && b.getMonth().isEqual(localDate.withDayOfMonth(1)))
                        .mapToDouble(BudgetDocument::getGrossBudgetHours).sum();
                monthAvailabilites[m] += budget;
                Optional<AvailabilityDocument> document = availabilityDocuments.stream().filter(availabilityDocument ->
                        availabilityDocument.getMonth().isEqual(localDate) && availabilityDocument.getUser().getUuid().equals(user.getUuid())).findAny();
                double netAvailability = document.map(AvailabilityDocument::getNetAvailableHours).orElse(0.0);
                monthTotalNetAvailabilites[m] += netAvailability;
                double grossAvailability = document.map(AvailabilityDocument::getGrossAvailableHours).orElse(0.0);
                monthTotalGrossAvailabilites[m] += grossAvailability;
            }
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
        actualDataSeries.setData(getAverageAllocationByYear(periodStart, periodEnd));
        chart.getConfiguration().addSeries(actualDataSeries);

        chart.getConfiguration().getxAxis().setCategories(StatisticsService.getMonthCategories(periodStart, periodEnd));
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    private List<DataSeriesItem> getAverageAllocationByYear(LocalDate startDate, LocalDate endDate) {
        startDate = startDate.withDayOfMonth(1);
        List<DataSeriesItem> dataSeriesItemList = new ArrayList<>();

        List<AvailabilityDocument> availabilityDocuments = availabilityService.getConsultantAvailabilityByPeriod(startDate, endDate);

        do {
            double totalBillableHours = 0.0;
            double totalAvailableHours = 0.0;
            double totalAllocation;
            double countEmployees = 0.0;
            List<GraphKeyValue> registeredHoursPerConsultant = revenueService.getRegisteredHoursPerConsultantForSingleMonth(startDate);
            for (User user : userService.findEmployedUsersByDate(startDate, true, ConsultantType.CONSULTANT)) {
                if(user.getUsername().equals("hans.lassen") || user.getUsername().equals("tobias.kjoelsen") || user.getUsername().equals("lars.albert") || user.getUsername().equals("thomas.gammelvind")) continue;

                //double billableWorkHours = revenueService.getRegisteredHoursForSingleMonthAndSingleConsultant(user.getUuid(), startDate);

                LocalDate finalStartDate = startDate;

                double billableWorkHours = registeredHoursPerConsultant.stream().filter(g ->
                        g.getUuid().equals(user.getUuid()) && g.getDescription().equals(DateUtils.stringIt(finalStartDate))).mapToDouble(GraphKeyValue::getValue).sum();
                //double billableWorkHours = registeredHours.map(GraphKeyValue::getValue).orElse(0.0);

                Optional<AvailabilityDocument> availabilityDocument = availabilityDocuments.stream().filter(ad ->
                        ad.getMonth().isEqual(finalStartDate) && ad.getUser().getUuid().equals(user.getUuid())).findAny();
                if (!availabilityDocument.isPresent() || !availabilityDocument.get().getStatusType().equals(StatusType.ACTIVE)) {
                    continue;
                }

                totalAvailableHours += availabilityDocument.get().getNetAvailableHours();
                totalBillableHours += billableWorkHours;
                countEmployees++;
            }
            totalAllocation = Math.floor(((totalBillableHours / countEmployees) / (totalAvailableHours / countEmployees)) * 100.0);
            dataSeriesItemList.add(new DataSeriesItem(startDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), NumberUtils.round(totalAllocation, 0)));
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(endDate));
        return dataSeriesItemList;
    }
}