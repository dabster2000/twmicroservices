package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.services.InvoiceService;
import dk.trustworks.invoicewebui.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class CumulativeRevenuePerMonthChart {

    private final StatisticsService statisticsService;

    private final InvoiceService invoiceService;

    @Autowired
    public CumulativeRevenuePerMonthChart(StatisticsService statisticsService, InvoiceService invoiceService) {
        this.statisticsService = statisticsService;
        this.invoiceService = invoiceService;
    }

    public Chart createCumulativeRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setSizeFull();
        int period = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        chart.setCaption("Cumulative Revenue, Budget, and Earnings");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        String[] categories = new String[period];
        DataSeries registeredRevenueSeries = new DataSeries("Registered Revenue");
        DataSeries budgetSeries = new DataSeries("Budget");
        DataSeries earningsSeries = new DataSeries("Earnings");

        //List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.findRevenueByMonthByPeriod(periodStart.format(DateTimeFormatter.ofPattern("yyyyMMdd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        //amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")))).collect(Collectors.toList());

/*
        TrendLine t = new PolyTrendLine(2);
        if(amountPerItemList.size()>2) {
            double[] x = new double[amountPerItemList.size()];
            double[] y = new double[amountPerItemList.size()];

            double sum = 0.0;
            for (int j = 0; j < Period.between(periodStart, LocalDate.now()).getMonths(); j++) {
                if (amountPerItemList.size() > j) {
                    sum += amountPerItemList.get(j).getValue();
                    y[j] = sum;
                    x[j] = j;
                }
            }
            t.setValues(y, x);
        }

 */


        DataSeries avgRevenueList = new DataSeries("Projected Revenue");
        PlotOptionsLine options2 = new PlotOptionsLine();
        options2.setColor(SolidColor.BLACK);
        options2.setMarker(new Marker(false));
        avgRevenueList.setPlotOptions(options2);

        double cumulativeRevenuePerMonth = 0.0;
        double cumulativeBudgetPerMonth = 0.0;
        double cumulativeExpensePerMonth = 0.0;
        for (int i = 0; i < period; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            double expense;

            double invoicedAmountByMonth = statisticsService.getTotalInvoiceSumByMonth(currentDate);
            if(invoicedAmountByMonth > 0.0) {
                cumulativeRevenuePerMonth += invoicedAmountByMonth;
                expense = statisticsService.getAllExpensesByMonth(periodStart.plusMonths(i).withDayOfMonth(1));//expenseRepository.findByPeriod(periodStart.plusMonths(i).withDayOfMonth(1)).stream().mapToDouble(Expense::getAmount).sum();
                cumulativeExpensePerMonth += expense;
            } else {
                cumulativeRevenuePerMonth += statisticsService.getMonthRevenue(currentDate);//amountPerItemList.get(i).getValue();
                expense = statisticsService.getAllExpensesByMonth(periodStart.plusMonths(i).withDayOfMonth(1));//expenseRepository.findByPeriod(periodStart.plusMonths(i).withDayOfMonth(1)).stream().mapToDouble(Expense::getAmount).sum();
                cumulativeExpensePerMonth += expense;
            }

            registeredRevenueSeries.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), cumulativeRevenuePerMonth));
            if(expense > 0.0) earningsSeries.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), cumulativeRevenuePerMonth-cumulativeExpensePerMonth));


            cumulativeBudgetPerMonth += statisticsService.getMonthBudget(periodStart.plusMonths(i).withDayOfMonth(1));

            budgetSeries.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(cumulativeBudgetPerMonth)));
            categories[i] = periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(registeredRevenueSeries);
        chart.getConfiguration().addSeries(budgetSeries);
        chart.getConfiguration().addSeries(earningsSeries);
        chart.getConfiguration().addSeries(avgRevenueList);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}
