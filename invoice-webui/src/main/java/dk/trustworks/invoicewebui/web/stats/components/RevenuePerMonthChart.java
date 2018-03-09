package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class RevenuePerMonthChart {

    @Autowired
    private GraphKeyValueRepository graphKeyValueRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public Chart createRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        System.out.println("RevenuePerMonthChart.createRevenuePerMonthChart");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
        Chart chart = new Chart();
        chart.setSizeFull();
        Period period = new Period(periodStart, periodEnd, PeriodType.months());

        chart.setCaption("Revenue during Fiscal Year 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.findRevenueByMonthByPeriod(periodStart.toString("yyyyMMdd"), periodEnd.toString("yyyyMMdd"));

        //List<GraphKeyValue> budgetAmountPerItemList = graphKeyValueRepository.findBudgetByMonthByPeriod(periodStart.toString("yyyyMMdd"), periodEnd.toString("yyyyMMdd"));
        String[] categories = new String[period.getMonths()];
        DataSeries revenueSeries = new DataSeries("Revenue");
        DataSeries earningsSeries = new DataSeries("Earnings");
        DataSeries budgetSeries = new DataSeries("Budget");
        amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription()))).collect(Collectors.toList());
        //budgetAmountPerItemList = budgetAmountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription()))).collect(Collectors.toList());
        for (int i = 0; i < period.getMonths(); i++) {
            if(amountPerItemList.size() > i) {
                GraphKeyValue amountPerItem = amountPerItemList.get(i);
                revenueSeries.add(new DataSeriesItem(LocalDate.parse(amountPerItem.getDescription()).toString("MMM-yyyy"), amountPerItem.getValue()));
                double expense = expenseRepository.findByPeriod(periodStart.plusMonths(i).toDate()).stream().mapToDouble(value -> value.getAmount()).sum();
                if(expense>0.0) earningsSeries.add(new DataSeriesItem(LocalDate.parse(amountPerItem.getDescription()).toString("MMM-yyyy"), amountPerItem.getValue()-expense));
            }
            System.out.println("(periodStart.plusMonths(i).getMonthOfYear()-1) = " + (periodStart.plusMonths(i).getMonthOfYear() - 1));
            System.out.println("periodStart.plusMonths(i).getYear() = " + periodStart.plusMonths(i).getYear());
            System.out.println("periodStart.plusMonths(i).toString(\"yyyy-MM-dd\") = " + periodStart.plusMonths(i).toString("yyyy-MM-dd"));
            List<GraphKeyValue> budget = graphKeyValueRepository.findBudgetByMonthAndHistory(periodStart.plusMonths(i).getMonthOfYear() - 1, periodStart.plusMonths(i).getYear(), periodStart.plusMonths(i).toString("yyyy-MM-dd"));

            int sum = 0;
            System.out.println("budget = " + budget);
            System.out.println("budget = " + budget.size());
            if(budget != null && budget.size() > 0 && budget.get(0) != null) sum = budget.stream().mapToInt(value -> value.getValue()).sum();

            budgetSeries.add(new DataSeriesItem(periodStart.plusMonths(i).toString("MMM-yyyy"), sum));
            categories[i] = periodStart.plusMonths(i).toString("MMM-yyyy");
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueSeries);
        chart.getConfiguration().addSeries(budgetSeries);
        chart.getConfiguration().addSeries(earningsSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}
