package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.services.WorkService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class AverageConsultantAllocationChart {

    private final WorkService workService;

    private final UserService userService;

    @Autowired
    public AverageConsultantAllocationChart(UserService userService, WorkService workService) {
        this.userService = userService;
        this.workService = workService;
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

        LocalDate startDate = LocalDate.of(2014, 7, 1);
        Map<User, Map<LocalDate, Double>> averagePerUserPerYear = new HashMap<>();

        do {
            for (User user : userService.findCurrentlyEmployedUsers(ConsultantType.CONSULTANT)) {
                LocalDate endDate = startDate.plusYears(1).minusDays(1);
                if(endDate.isAfter(LocalDate.now())) {
                    endDate = LocalDate.now().withDayOfMonth(1).minusDays(1);
                }
                double workdaysInPeriod = DateUtils.getWeekdaysInPeriod(startDate, endDate);
                System.out.println("workdaysInPeriod before vacation = " + workdaysInPeriod);
                double vacationByUser = workService.countVacationByUser(user);
                System.out.println("vacationByUser = " + vacationByUser);
                workdaysInPeriod = (workdaysInPeriod - (vacationByUser / 7.4));
                System.out.println("workdaysInPeriod after vacation = " + workdaysInPeriod);
                double weeksInPeriod = DAYS.between(startDate, endDate) / 7.0;
                System.out.println("weeksInPeriod = " + weeksInPeriod);
                double avgWorkHoursPerWeek = (workdaysInPeriod / weeksInPeriod * 7.0);
                System.out.println("avgWorkHoursPerWeek = " + avgWorkHoursPerWeek);

                double billableWorkHours = workService.countBillableWorkByUserInPeriod(user.getUuid(), DateUtils.stringIt(startDate), DateUtils.stringIt(endDate));
                System.out.println("billableWorkHours = " + billableWorkHours);
                double antalTimerPerUge = billableWorkHours / weeksInPeriod;
                System.out.println("antalTimerPerUge = " + antalTimerPerUge);

                double allocation = (antalTimerPerUge / avgWorkHoursPerWeek) * 100.0;

                System.out.println(user.getUsername()+" ("+startDate.getYear()+"): "+billableWorkHours);

                Map<LocalDate, Double> averagePerYearMap = averagePerUserPerYear.getOrDefault(user, new HashMap<>());
                averagePerUserPerYear.putIfAbsent(user, averagePerYearMap);
                averagePerYearMap.put(startDate, averagePerYearMap.getOrDefault(startDate, 0.0) + allocation);
            }
            startDate = startDate.plusYears(1);
        } while (startDate.isBefore(LocalDate.now()));

        for (User user : averagePerUserPerYear.keySet().stream().sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toList())) {
            Map<LocalDate, Double> userAverageByYearMap = averagePerUserPerYear.get(user);
            OptionalDouble result = userAverageByYearMap.values().stream().mapToDouble(Double::doubleValue).filter(value -> value > 0.0).average();
            if(!result.isPresent()) continue;
            DataSeriesItem item = new DataSeriesItem(user.getUsername(), result.getAsDouble());
            DataSeries drillSeries = new DataSeries(user.getUsername()+" by year");
            drillSeries.setId(user.getUsername());

            String[] categories = userAverageByYearMap.keySet().stream().sorted(Comparator.naturalOrder()).map(localDate -> Integer.toString(localDate.getYear())).toArray(String[]::new);
            Number[] values = new Number[userAverageByYearMap.size()];
            int i = 0;
            for (LocalDate localDate : userAverageByYearMap.keySet().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList())) {
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