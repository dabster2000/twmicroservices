package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.AvailabilityDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.services.AvailabilityService;
import dk.trustworks.invoicewebui.services.RevenueService;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class AverageConsultantAllocationChart {

    private final RevenueService revenueService;
    private final AvailabilityService availabilityService;
    private final UserService userService;

    @Autowired
    public AverageConsultantAllocationChart(RevenueService revenueService, AvailabilityService availabilityService, UserService userService) {
        this.revenueService = revenueService;
        this.availabilityService = availabilityService;
        this.userService = userService;
    }

    public Chart createChart() {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Average Allocation Per Consultant");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
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
        y.setTitle("Allocation");
        chart.getConfiguration().addyAxis(y);

        DataSeries revenueSeries = new DataSeries("Allocation");
        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setColorByPoint(true);
        revenueSeries.setPlotOptions(plotOptionsColumn);

        LocalDate startDate;
        Map<User, Map<LocalDate, Double>> averagePerUserPerMonth = new HashMap<>();
        Map<User, Double> averagePerUser = new HashMap<>();

        for (User user : userService.findCurrentlyEmployedUsers(ConsultantType.CONSULTANT)) {
            startDate = LocalDate.of(2014,7,1);//userService.findEmployedDate(user);
            double allocation = 0.0;
            double count = 0.0;
            do {
                double billableWorkHours = revenueService.getRegisteredHoursForSingleMonthAndSingleConsultant(user.getUuid(), startDate);
                AvailabilityDocument availability = availabilityService.getConsultantAvailabilityByMonth(user.getUuid(), startDate);
                if(availability==null) {
                    //startDate = startDate.plusMonths(1);
                    //continue;
                    availability = new AvailabilityDocument(user, startDate, 0.0, 0.0, 0.0, 0.0, ConsultantType.CONSULTANT, StatusType.TERMINATED);
                }
                double monthAllocation = 0.0;
                if(billableWorkHours>0.0 && availability.getNetAvailableHours()>0.0) {
                    monthAllocation = (billableWorkHours / availability.getNetAvailableHours()) * 100.0;
                    count++;
                }

                allocation += monthAllocation;

                Map<LocalDate, Double> averagePerYearMap = averagePerUserPerMonth.getOrDefault(user, new HashMap<>());
                averagePerUserPerMonth.putIfAbsent(user, averagePerYearMap);
                averagePerYearMap = averagePerUserPerMonth.get(user);
                averagePerYearMap.put(startDate, averagePerYearMap.getOrDefault(startDate, 0.0) + monthAllocation);

                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(LocalDate.now()));
            averagePerUser.putIfAbsent(user, (allocation / count));
        }

        for (User user : averagePerUserPerMonth.keySet().stream().sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toList())) {
            Map<LocalDate, Double> userAverageByYearMap = averagePerUserPerMonth.get(user);
            //OptionalDouble result = userAverageByYearMap.values().stream().mapToDouble(Double::doubleValue).filter(value -> value > -1.0).average();
            //if(!result.isPresent()) continue;
            DataSeriesItem item = new DataSeriesItem(user.getUsername(), averagePerUser.get(user));
            DataSeries drillSeries = new DataSeries(user.getUsername()+" by year");
            drillSeries.setId(user.getUsername());

            String[] categories = userAverageByYearMap.keySet().stream().sorted(LocalDate::compareTo).map(localDate -> stringIt(localDate, "MMM yyyy")).toArray(String[]::new);
            Number[] values = new Number[userAverageByYearMap.size()];
            int i = 0;
            for (LocalDate localDate : userAverageByYearMap.keySet().stream().sorted(LocalDate::compareTo).collect(Collectors.toList())) {
                values[i++] = userAverageByYearMap.get(localDate);
            }
            drillSeries.setData(categories, values);
            revenueSeries.addItemWithDrilldown(item, drillSeries);
        }


        chart.getConfiguration().addSeries(revenueSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}