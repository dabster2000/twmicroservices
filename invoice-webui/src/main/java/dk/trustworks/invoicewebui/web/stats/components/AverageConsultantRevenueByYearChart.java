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
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class AverageConsultantRevenueByYearChart {

    private final StatisticsService statisticsService;

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final UserService userService;

    private final ExpenseRepository expenseRepository;

    @Autowired
    public AverageConsultantRevenueByYearChart(StatisticsService statisticsService, GraphKeyValueRepository graphKeyValueRepository, UserService userService, ExpenseRepository expenseRepository, CountEmployeesJob countEmployeesJob) {
        this.statisticsService = statisticsService;
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.userService = userService;
        this.expenseRepository = expenseRepository;
    }

    public Chart createRevenuePerConsultantChart() {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Gross profit for ");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        DataSeries revenueSeries = new DataSeries("Revenue");

        LocalDate currentDate = LocalDate.of(2014, 7, 1);

        Map<LocalDate, Double> averagePerYearMap = new HashMap<>();
        do {
            double countUsers = 0.0;
            double sum = 0.0;
            for (User user : userService.findCurrentlyEmployedUsers(ConsultantType.CONSULTANT)) {
                LocalDate periodStart = currentDate;//user.getStatuses().stream().min(Comparator.comparing(UserStatus::getStatusdate)).orElse(new UserStatus(user, ConsultantType.CONSULTANT, StatusType.ACTIVE, LocalDate.now(), 0)).getStatusdate();//LocalDate.of(2017, 07, 01);
                LocalDate periodEnd = currentDate.plusYears(1); //LocalDate.now().withDayOfMonth(1);
                Map<LocalDate, Double> resultMap = statisticsService.calculateConsultantRevenue(user, periodStart, periodEnd, 3);

                OptionalDouble average = resultMap.values().stream().filter(aDouble -> aDouble != 0.0).mapToDouble(value -> value).average();
                if(average.isPresent()) {
                    //averagePerYearMap.put(currentDate, averagePerYearMap.getOrDefault(currentDate, 0.0) + average.getAsDouble());
                    sum += averagePerYearMap.getOrDefault(currentDate, 0.0) + average.getAsDouble();
                    countUsers++;
                }
            }
            averagePerYearMap.put(currentDate, averagePerYearMap.getOrDefault(currentDate, 0.0)/countUsers);
            currentDate = currentDate.plusYears(1);
        } while (currentDate.isBefore(LocalDate.now()));

        for (LocalDate date : averagePerYearMap.keySet().stream().sorted().collect(Collectors.toList())) {
            revenueSeries.add(new DataSeriesItem(date.format(DateTimeFormatter.ofPattern("MMM-yyyy")), averagePerYearMap.get(date)));
            chart.getConfiguration().getxAxis().addCategory(date.format(DateTimeFormatter.ofPattern("MMM-yyyy")));
        }

        chart.getConfiguration().addSeries(revenueSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}