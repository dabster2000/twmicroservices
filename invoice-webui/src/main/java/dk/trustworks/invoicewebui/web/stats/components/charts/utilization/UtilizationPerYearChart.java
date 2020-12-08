package dk.trustworks.invoicewebui.web.stats.components.charts.utilization;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.AvailabilityDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.services.AvailabilityService;
import dk.trustworks.invoicewebui.services.RevenueService;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class UtilizationPerYearChart {

    private final UserService userService;

    private final StatisticsService statisticsService;

    private final RevenueService revenueService;

    private final AvailabilityService availabilityService;

    @Autowired
    public UtilizationPerYearChart(UserService userService, StatisticsService statisticsService, RevenueService revenueService, AvailabilityService availabilityService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
        this.revenueService = revenueService;
        this.availabilityService = availabilityService;
    }

    public Chart createChart() {
        LocalDate periodStart = LocalDate.of(2016, 7, 1);

        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Actual Utilization");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("%");
        chart.getConfiguration().getyAxes().getAxis(0).setMax(100);
        chart.getConfiguration().getLegend().setEnabled(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0) +' %'");
        chart.getConfiguration().setTooltip(tooltip);

        DataSeries actualDataSeries = new DataSeries("Actual utilization");
        actualDataSeries.setData(getAverageAllocationByYear(periodStart));
        chart.getConfiguration().addSeries(actualDataSeries);

        chart.getConfiguration().getxAxis().setCategories(statisticsService.getYearCategories(periodStart, LocalDate.now().withDayOfMonth(1).plusMonths(11)));
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    private List<DataSeriesItem> getAverageAllocationByYear(LocalDate startDate) {
        startDate = startDate.withDayOfMonth(1);
        List<DataSeriesItem> dataSeriesItemList = new ArrayList<>();
        int count = 0;
        double tempSum = 0.0;

        List<AvailabilityDocument> availabilityDocuments = availabilityService.getConsultantAvailabilityByPeriod(startDate, LocalDate.now().withDayOfMonth(1));

        do {
            double totalBillableHours = 0.0;
            double totalAvailableHours = 0.0;
            double countEmployees = 0.0;
            List<GraphKeyValue> registeredHoursPerConsultant = revenueService.getRegisteredHoursPerConsultantForSingleMonth(startDate);
            for (User user : userService.findEmployedUsersByDate(startDate, true, ConsultantType.CONSULTANT)) {
                if(user.getUsername().equals("hans.lassen") || user.getUsername().equals("tobias.kjoelsen") || user.getUsername().equals("lars.albert") || user.getUsername().equals("thomas.gammelvind")) continue;

                LocalDate finalStartDate = startDate;

                double billableWorkHours = registeredHoursPerConsultant.stream().filter(g ->
                        g.getUuid().equals(user.getUuid()) && g.getDescription().equals(DateUtils.stringIt(finalStartDate))).mapToDouble(GraphKeyValue::getValue).sum();
                //double billableWorkHours = registeredHours.map(GraphKeyValue::getValue).orElse(0.0);

                //AvailabilityDocument availability = availabilityService.getConsultantAvailabilityByMonth(user.getUuid(), startDate);
                Optional<AvailabilityDocument> availabilityDocument = availabilityDocuments.stream().filter(ad ->
                        ad.getMonth().isEqual(finalStartDate) && ad.getUser().getUuid().equals(user.getUuid())).findAny();

                if (!availabilityDocument.isPresent() || !availabilityDocument.get().getStatusType().equals(StatusType.ACTIVE)) {
                    continue;
                }
                totalAvailableHours += availabilityDocument.get().getNetAvailableHours();
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
        } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1)));
        dataSeriesItemList.add(new DataSeriesItem(startDate.format(DateTimeFormatter.ofPattern("yyyy")), NumberUtils.round(tempSum / count, 0)));
        return dataSeriesItemList;
    }
}