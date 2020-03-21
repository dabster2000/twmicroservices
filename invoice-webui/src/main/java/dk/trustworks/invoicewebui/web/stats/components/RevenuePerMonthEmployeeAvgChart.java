package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class RevenuePerMonthEmployeeAvgChart {

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final UserService userService;

    private final StatisticsService statisticsService;

    @Autowired
    public RevenuePerMonthEmployeeAvgChart(GraphKeyValueRepository graphKeyValueRepository, ExpenseRepository expenseRepository, UserService userService, StatisticsService statisticsService) {
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    public Chart createRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setSizeFull();
        int period = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);

        chart.setCaption("Average Revenue and Earnings per Consultant");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.findRevenueByMonthByPeriod(periodStart.format(DateTimeFormatter.ofPattern("yyyyMMdd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        String[] categories = new String[period];
        DataSeries revenueSeries = new DataSeries("Revenue");
        DataSeries earningsSeries = new DataSeries("Earnings");
        DataSeries avgRevenueList = new DataSeries("Average Revenue");
        PlotOptionsLine options2 = new PlotOptionsLine();
        options2.setColor(SolidColor.BLACK);
        options2.setMarker(new Marker(false));
        avgRevenueList.setPlotOptions(options2);

        amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")))).collect(Collectors.toList());
        double avg = 0.0;
        int count = 0;
        for (int i = 0; i < period; i++) {
            if(amountPerItemList.size() > i && amountPerItemList.get(i) != null) {
                GraphKeyValue amountPerItem = amountPerItemList.get(i);
                LocalDate javaDate = LocalDate.parse(amountPerItem.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd"));
                if(javaDate.isAfter(LocalDate.now())) continue;

                int consultants = userService.findEmployedUsersByDate(javaDate, ConsultantType.CONSULTANT).size();

                revenueSeries.add(new DataSeriesItem(LocalDate.parse(amountPerItem.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")).format(DateTimeFormatter.ofPattern("MMM-yyyy")), (amountPerItem.getValue() / consultants)));

                double expense = statisticsService.calcAllExpensesByMonth(periodStart.plusMonths(i).withDayOfMonth(1));
                //double expense = expenseRepository.findByPeriod(periodStart.plusMonths(i).withDayOfMonth(1)).stream().mapToDouble(Expense::getAmount).sum();
                if(expense>0.0) earningsSeries.add(new DataSeriesItem(LocalDate.parse(amountPerItem.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")).format(DateTimeFormatter.ofPattern("MMM-yyyy")), ((amountPerItem.getValue() - expense) / consultants)));

                if(periodStart.plusMonths(i).isBefore(LocalDate.now().withDayOfMonth(1))) {
                    avg += (amountPerItem.getValue() / consultants);
                    count++;
                }
            }
            categories[i] = periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }
        LocalDate localDate = periodStart;
        for (int i = 0; i < period; i++) {
            avgRevenueList.add(new DataSeriesItem(localDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), (avg / count)));
            localDate = localDate.plusMonths(1);
        }

        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueSeries);
        chart.getConfiguration().addSeries(avgRevenueList);
        chart.getConfiguration().addSeries(earningsSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}
