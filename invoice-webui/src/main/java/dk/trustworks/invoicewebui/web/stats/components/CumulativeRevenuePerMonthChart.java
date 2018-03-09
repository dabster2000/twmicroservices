package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.utils.PolyTrendLine;
import dk.trustworks.invoicewebui.utils.TrendLine;
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
public class CumulativeRevenuePerMonthChart {

    @Autowired
    private GraphKeyValueRepository graphKeyValueRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public Chart createCumulativeRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        System.out.println("CumulativeRevenuePerMonthChart.createCumulativeRevenuePerMonthChart");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
        Chart chart = new Chart();
        chart.setSizeFull();
        Period period = new Period(periodStart, periodEnd, PeriodType.months());

        chart.setCaption("Cumulative Revenue during Fiscal Year 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.findRevenueByMonthByPeriod(periodStart.toString("yyyyMMdd"), periodEnd.toString("yyyyMMdd"));
        String[] categories = new String[period.getMonths()];
        DataSeries revenueSeries = new DataSeries("Revenue");
        DataSeries budgetSeries = new DataSeries("Budget");
        DataSeries earningsSeries = new DataSeries("Earnings");
        amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription()))).collect(Collectors.toList());

        TrendLine t = new PolyTrendLine(2);
        double[] x = new double[amountPerItemList.size()];
        double[] y = new double[amountPerItemList.size()];

        double sum = 0.0;
        System.out.println("new Period(periodStart, LocalDate.now(), PeriodType.months()).getMonths() = " + new Period(periodStart, LocalDate.now(), PeriodType.months()).getMonths());
        for (int j = 0; j < new Period(periodStart, LocalDate.now(), PeriodType.months()).getMonths(); j++) {
            if(amountPerItemList.size()>j) {
                System.out.println("did j = " + j);
                sum += amountPerItemList.get(j).getValue();
                y[j] = sum;
                x[j] = j;
            }
            System.out.println("j = " + j);
        }
        t.setValues(y, x);
        System.out.println("(new Period(periodStart, LocalDate.now(), PeriodType.months()).getMonths()-1) = " + (new Period(periodStart, LocalDate.now(), PeriodType.months()).getMonths() - 1));
        double projection = sum / (new Period(periodStart, LocalDate.now(), PeriodType.months()).getMonths());

        DataSeries avgRevenueList = new DataSeries("Projected Revenue");
        PlotOptionsLine options2 = new PlotOptionsLine();
        options2.setColor(SolidColor.BLACK);
        options2.setMarker(new Marker(false));
        avgRevenueList.setPlotOptions(options2);

        double cumulativeRevenuePerMonth = 0.0;
        double cumulativeBudgetPerMonth = 0.0;
        double cumulativeExpensePerMonth = 0.0;
        double projectedSum = 0.0;
        for (int i = 0; i < period.getMonths(); i++) {
            double expense = 0.0;
            if(amountPerItemList.size() > i) {
                cumulativeRevenuePerMonth += amountPerItemList.get(i).getValue();
                expense = expenseRepository.findByPeriod(periodStart.plusMonths(i).toDate()).stream().mapToDouble(value -> value.getAmount()).sum();
                cumulativeExpensePerMonth += expense;
            }
            projectedSum += projection;
            avgRevenueList.add(new DataSeriesItem(periodStart.plusMonths(i).toString("MMM-yyyy"), t.predict(i)));
            revenueSeries.add(new DataSeriesItem(periodStart.plusMonths(i).toString("MMM-yyyy"), cumulativeRevenuePerMonth));
            if(expense > 0.0) earningsSeries.add(new DataSeriesItem(periodStart.plusMonths(i).toString("MMM-yyyy"), cumulativeRevenuePerMonth-cumulativeExpensePerMonth));
            cumulativeBudgetPerMonth += graphKeyValueRepository.findBudgetByMonthAndHistory(periodStart.plusMonths(i).getMonthOfYear()-1, periodStart.plusMonths(i).getYear(), periodStart.plusMonths(i).toString("yyyy-MM-dd")).stream().mapToInt(value -> value.getValue()).sum();
            budgetSeries.add(new DataSeriesItem(periodStart.plusMonths(i).toString("MMM-yyyy"), cumulativeBudgetPerMonth));
            categories[i] = periodStart.plusMonths(i).toString("MMM-yyyy");
        }




        /*
        for (GraphKeyValue amountPerItem : amountPerItemList) {
            cumulativeRevenuePerMonth += amountPerItem.getValue();
            revenueSeries.add(new DataSeriesItem(LocalDate.parse(amountPerItem.getDescription()).toString("MMM-yy"), cumulativeRevenuePerMonth));
            categories[i] = LocalDate.parse(amountPerItem.getDescription()).toString("MMM-yy");

            i++;
        }
        */
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueSeries);
        chart.getConfiguration().addSeries(budgetSeries);
        chart.getConfiguration().addSeries(earningsSeries);
        chart.getConfiguration().addSeries(avgRevenueList);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}
