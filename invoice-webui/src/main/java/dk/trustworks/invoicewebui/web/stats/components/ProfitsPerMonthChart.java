package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.BudgetNew;
import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.ContractConsultant;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.WorkService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ProfitsPerMonthChart {

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final ContractService contractService;

    private final BudgetNewRepository budgetNewRepository;

    private final WorkService workService;

    private final StatisticsService statisticsService;

    @Autowired
    public ProfitsPerMonthChart(GraphKeyValueRepository graphKeyValueRepository, ContractService contractService, BudgetNewRepository budgetNewRepository, WorkService workService, StatisticsService statisticsService) {
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.contractService = contractService;
        this.budgetNewRepository = budgetNewRepository;
        this.workService = workService;
        this.statisticsService = statisticsService;
    }

    public Chart createCumulativeRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
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

        String[] categories = new String[period];
        DataSeries revenueSeries = new DataSeries("Revenue");
        DataSeries budgetSeries = new DataSeries("Budget");
        DataSeries earningsSeries = new DataSeries("Earnings");

        // DOESN'T WORK
        List<GraphKeyValue> amountPerItemList = new ArrayList<>(); //graphKeyValueRepository.findRevenueByMonthByPeriod(periodStart.format(DateTimeFormatter.ofPattern("yyyyMMdd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
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

            double invoicedAmountByMonth = statisticsService.getTotalInvoiceSumByMonth(currentDate);
            //double invoicedAmountByMonth = invoiceService.invoicedAmountByMonth(currentDate);
            if(invoicedAmountByMonth > 0.0) {
                cumulativeRevenuePerMonth += invoicedAmountByMonth;
                expense = statisticsService.calcAllUserExpensesByMonth(periodStart.plusMonths(i).withDayOfMonth(1));
                //expense = expenseRepository.findByPeriod(periodStart.plusMonths(i).withDayOfMonth(1)).stream().mapToDouble(Expense::getAmount).sum();
                cumulativeExpensePerMonth += expense;
            } else {
                if(amountPerItemList.size() > i && amountPerItemList.get(i) != null) {
                    cumulativeRevenuePerMonth += amountPerItemList.get(i).getValue();
                    expense = statisticsService.calcAllUserExpensesByMonth(periodStart.plusMonths(i).withDayOfMonth(1));
                    //expense = expenseRepository.findByPeriod(periodStart.plusMonths(i).withDayOfMonth(1)).stream().mapToDouble(Expense::getAmount).sum();
                    cumulativeExpensePerMonth += expense;
                }
            }

            /*
            if(amountPerItemList.size() > i && amountPerItemList.get(i) != null) {
                cumulativeRevenuePerMonth += amountPerItemList.get(i).getValue();
                expense = expenseRepository.findByPeriod(Date.from(periodStart.plusMonths(i).atStartOfDay(ZoneId.systemDefault()).toInstant())).stream().mapToDouble(Expense::getAmount).sum();
                cumulativeExpensePerMonth += expense;
            }
            */
            revenueSeries.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), cumulativeRevenuePerMonth));
            if(expense > 0.0) earningsSeries.add(new DataSeriesItem(periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM-yyyy")), cumulativeRevenuePerMonth-cumulativeExpensePerMonth));

            List<Contract> contracts = contractService.findActiveContractsByDate(currentDate, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            for (Contract contract : contracts) {
                if(contract.getContractType().equals(ContractType.PERIOD)) {
                    for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                        double weeks = workService.getWorkDaysInMonth(contractConsultant.getUser().getUuid(), currentDate) / 5.0;
                        cumulativeBudgetPerMonth += (contractConsultant.getHours() * weeks) * contractConsultant.getRate();
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
