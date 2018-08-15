package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
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
public class CumulativeRevenuePerMonthChart {

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final ExpenseRepository expenseRepository;

    private final ContractService contractService;

    private final BudgetNewRepository budgetNewRepository;

    @Autowired
    public CumulativeRevenuePerMonthChart(GraphKeyValueRepository graphKeyValueRepository, ExpenseRepository expenseRepository, ContractService contractService, BudgetNewRepository budgetNewRepository) {
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.expenseRepository = expenseRepository;
        this.contractService = contractService;
        this.budgetNewRepository = budgetNewRepository;
    }

    public Chart createCumulativeRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        System.out.println("CumulativeRevenuePerMonthChart.createCumulativeRevenuePerMonthChart");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
        Chart chart = new Chart();
        chart.setSizeFull();
        int period = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        chart.setCaption("Cumulative Revenue during Fiscal Year 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
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
        DataSeries budgetSeries = new DataSeries("Budget");
        DataSeries earningsSeries = new DataSeries("Earnings");
        amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")))).collect(Collectors.toList());

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
            double expense = 0.0;
            if(amountPerItemList.size() > i && amountPerItemList.get(i) != null) {
                cumulativeRevenuePerMonth += amountPerItemList.get(i).getValue();
                expense = expenseRepository.findByPeriod(Date.from(periodStart.plusMonths(i).atStartOfDay(ZoneId.systemDefault()).toInstant())).stream().mapToDouble(Expense::getAmount).sum();
                cumulativeExpensePerMonth += expense;
            }
            //System.out.println("periodStart.plusMonths(i) = " + periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")));
            //System.out.println("amountPerItemList = " + amountPerItemList.size());
            //System.out.println("t = " + t.predict(i));
            //System.out.println("avgRevenueList = " + avgRevenueList.size());
            //if(amountPerItemList.size()>2) avgRevenueList.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), t.predict(i)));
            revenueSeries.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), cumulativeRevenuePerMonth));
            if(expense > 0.0) earningsSeries.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), cumulativeRevenuePerMonth-cumulativeExpensePerMonth));

            List<Contract> contracts = contractService.findActiveContractsByDate(currentDate, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            for (Contract contract : contracts) {
                if(contract.getContractType().equals(ContractType.PERIOD)) {
                    double weeks = currentDate.getMonth().length(true) / 7.0;
                    for (Consultant consultant : contract.getConsultants()) {
                        cumulativeBudgetPerMonth += (consultant.getHours() * weeks) * consultant.getRate();
                    }
                }
            }
            List<BudgetNew> budgets = budgetNewRepository.findByMonthAndYear(currentDate.getMonthValue() - 1, currentDate.getYear());
            for (BudgetNew budget : budgets) {
                cumulativeBudgetPerMonth += budget.getBudget();
            }

            budgetSeries.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(cumulativeBudgetPerMonth)));
            categories[i] = periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }
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
