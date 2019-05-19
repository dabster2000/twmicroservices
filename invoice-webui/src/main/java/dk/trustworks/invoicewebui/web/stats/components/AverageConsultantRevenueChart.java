package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.jobs.CountEmployeesJob;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class AverageConsultantRevenueChart {

    private final StatisticsService statisticsService;

    private final UserService userService;

    @Autowired
    public AverageConsultantRevenueChart(StatisticsService statisticsService, GraphKeyValueRepository graphKeyValueRepository, UserService userService, ExpenseRepository expenseRepository, CountEmployeesJob countEmployeesJob) {
        this.statisticsService = statisticsService;
        this.userService = userService;
    }

    public Chart createRevenuePerConsultantChart() {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Average Revenue Per Consultant");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getLegend().setEnabled(false);
        chart.getConfiguration().setTitle("");

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        XAxis x = new XAxis();
        x.setType(AxisType.CATEGORY);
        chart.getConfiguration().addxAxis(x);

        YAxis y = new YAxis();
        y.setTitle("Revenue");
        chart.getConfiguration().addyAxis(y);

        DataSeries revenueSeries = new DataSeries("Revenue");
        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setColorByPoint(true);
        revenueSeries.setPlotOptions(plotOptionsColumn);

        Map<User, Map<LocalDate, Double>> averagePerUserPerYear = new HashMap<>();
        for (User user : userService.findCurrentlyEmployedUsers(ConsultantType.CONSULTANT)) {
            LocalDate currentDate = LocalDate.of(2014,7,1);//userService.getStatus(user, true, StatusType.ACTIVE).getStatusdate();
            HashMap<LocalDate, Double> map = new HashMap<>();
            averagePerUserPerYear.put(user, map);

            do {
                double revenue = statisticsService.getConsultantRevenueByMonth(user, currentDate);
                double expenseSum = statisticsService.getConsultantExpensesByMonth(user, currentDate).getExpenseSum();
                //if(revenue > 0)
                    map.put(currentDate, revenue - expenseSum);

                currentDate = currentDate.plusMonths(1);
            } while (currentDate.isBefore(LocalDate.now().withDayOfMonth(1).minusDays(1)));
        }


        for (User user : averagePerUserPerYear.keySet().stream().sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toList())) {
            Map<LocalDate, Double> userAverageByYearMap = averagePerUserPerYear.get(user);
            DataSeriesItem item = new DataSeriesItem(user.getUsername(), userAverageByYearMap.values().stream().mapToDouble(Double::doubleValue).filter(value -> value != 0.0).average().orElse(0.0));
            DataSeries drillSeries = new DataSeries(user.getUsername()+" by year");
            drillSeries.setId(user.getUsername());

            String[] categories = userAverageByYearMap.keySet().stream().sorted(LocalDate::compareTo).map(localDate -> DateUtils.stringIt(localDate, "MMM yy")).toArray(String[]::new);
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