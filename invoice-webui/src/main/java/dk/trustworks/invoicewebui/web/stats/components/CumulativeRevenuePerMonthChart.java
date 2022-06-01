package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.dto.CompanyAggregateData;
import dk.trustworks.invoicewebui.services.BiService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@SpringComponent
@SpringUI
public class CumulativeRevenuePerMonthChart {

    private final BiService biService;

    @Autowired
    public CumulativeRevenuePerMonthChart(BiService biService) {
        this.biService = biService;
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
        DataSeries expensesSeries = new DataSeries("Expenses");
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

        List<CompanyAggregateData> data = biService.getCompanyAggregateDataByPeriod(periodStart, periodEnd);

        for (int i = 0; i < period; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            Optional<CompanyAggregateData> revenueDataOptional = data.stream().filter(monthRevenueData -> monthRevenueData.getMonth().isEqual(currentDate)).findAny();
            if(!revenueDataOptional.isPresent()) continue;
            CompanyAggregateData revenueData = revenueDataOptional.get();

            double expense;

            double invoicedAmountByMonth = revenueData.getInvoicedAmount();//revenueService.getInvoicedRevenueForSingleMonth(currentDate);
            if(invoicedAmountByMonth > 0.0) {
                cumulativeRevenuePerMonth += invoicedAmountByMonth;
            } else {
                cumulativeRevenuePerMonth += revenueData.getRegisteredAmount();//revenueService.getRegisteredRevenueForSingleMonth(currentDate);
            }

            expense = revenueData.calcExpensesSum();//financeService.calcAllExpensesByMonth(periodStart.plusMonths(i).withDayOfMonth(1));
            cumulativeExpensePerMonth += expense;

            registeredRevenueSeries.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(cumulativeRevenuePerMonth)));
            if(currentDate.isBefore(LocalDate.now().withDayOfMonth(1))) earningsSeries.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(cumulativeRevenuePerMonth-cumulativeExpensePerMonth)));

            cumulativeBudgetPerMonth += revenueData.getBudgetAmount();//budgetService.getMonthBudget(periodStart.plusMonths(i).withDayOfMonth(1));

            expensesSeries.add(new DataSeriesItem(periodStart.plusMonths(1).format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(cumulativeExpensePerMonth)));
            budgetSeries.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(cumulativeBudgetPerMonth)));
            categories[i] = periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(budgetSeries);
        chart.getConfiguration().addSeries(registeredRevenueSeries);
        chart.getConfiguration().addSeries(expensesSeries);
        chart.getConfiguration().addSeries(earningsSeries);
        chart.getConfiguration().addSeries(avgRevenueList);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}
