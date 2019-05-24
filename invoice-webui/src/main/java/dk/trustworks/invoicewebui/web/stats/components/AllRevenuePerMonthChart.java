package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class AllRevenuePerMonthChart {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserService userService;

    public Chart createRevenuePerMonthChart() {
        LocalDate periodStart = LocalDate.of(2014, 2, 1);
        LocalDate periodEnd = LocalDate.now().withDayOfMonth(1);

        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Revenue");

        chart.setTimeline(true);

        Configuration configuration = chart.getConfiguration();

        Tooltip tooltip = new Tooltip();
        //tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        tooltip.setFormatter( "function() { if (this.series.name == 'Actual Revenue') { return this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'; } }");
        //chart.getConfiguration().setTooltip(tooltip);

        //DataSeries dataSeries = createDataSeries(, "Actual revenue");

        DataSeries dataSeries = new DataSeries("Actual Revenue");
        dataSeries.setId("Revenue");
        Map<LocalDate, Double> revenuePerMonth = statisticsService.calcActualRevenuePerMonth(periodStart, periodEnd);
        for (LocalDate localDate : revenuePerMonth.keySet().stream().sorted(LocalDate::compareTo).collect(Collectors.toList())) {
            DataSeriesItem item = new DataSeriesItem();
            item.setX(DateUtils.convertLocalDateToDate(localDate));
            item.setY(Math.round(revenuePerMonth.get(localDate)));
            dataSeries.add(item);
        }

        DataSeries expensesDataSeries = new DataSeries("Expenses");
        //expensesDataSeries.getConfiguration().setTooltip(tooltip);
        LocalDate currentDate = periodStart;
        do {
            double allExpensesByMonth = statisticsService.getAllExpensesByMonth(currentDate);
            DataSeriesItem item = new DataSeriesItem();
            item.setX(DateUtils.convertLocalDateToDate(currentDate));
            item.setY(Math.round(allExpensesByMonth));
            expensesDataSeries.add(item);
            currentDate = currentDate.plusMonths(1);
        } while (currentDate.isBefore(periodEnd));

        DataSeries usersEmployedSeries = new DataSeries();
        usersEmployedSeries.setName("Employed");
        PlotOptionsFlags plotOptionsFlags = new PlotOptionsFlags();
        plotOptionsFlags.setOnSeries("Revenue");
        usersEmployedSeries.setPlotOptions(plotOptionsFlags);
        for (User user : userService.findAll()) {
            Optional<LocalDate> employedDate = userService.findEmployedDate(user);
            if(!employedDate.isPresent()) continue;
            int between = (int) ChronoUnit.MONTHS.between(periodStart, employedDate.get());
            usersEmployedSeries.add(new FlagItem(dataSeries.get(between).getX(), user.getFirstname(), user.getFirstname()+" "+user.getLastname()+" was empployed"));
        }

        configuration.setSeries(dataSeries, expensesDataSeries, usersEmployedSeries);

        RangeSelector rangeSelector = new RangeSelector();
        rangeSelector.setSelected(1);
        configuration.setRangeSelector(rangeSelector);

        chart.drawChart(configuration);

        //chart.getConfiguration().getxAxis().setCategories(statisticsService.getCategories(periodStart, periodEnd));
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}