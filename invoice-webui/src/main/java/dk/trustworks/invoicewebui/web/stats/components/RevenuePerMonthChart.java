package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class RevenuePerMonthChart {

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final ExpenseRepository expenseRepository;

    private final ContractService contractService;

    private final BudgetNewRepository budgetNewRepository;

    private final WorkRepository workRepository;

    @Autowired
    public RevenuePerMonthChart(GraphKeyValueRepository graphKeyValueRepository, ExpenseRepository expenseRepository, ContractService contractService, BudgetNewRepository budgetNewRepository, WorkRepository workRepository) {
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.expenseRepository = expenseRepository;
        this.contractService = contractService;
        this.budgetNewRepository = budgetNewRepository;
        this.workRepository = workRepository;
    }

    public Chart createRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        return createRevenuePerMonthChart(periodStart, periodEnd, true);
    }

    public Chart createRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd, boolean showEarnings) {
        System.out.println("RevenuePerMonthChart.createRevenuePerMonthChart");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "], showEarnings = [" + showEarnings + "]");
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);
        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);

        chart.setCaption("Revenue during Fiscal Year 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<Work> workList = workRepository.findByPeriod(periodStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        List<GraphKeyValue> amountPerItemList = new ArrayList<>();
        for (int i = 0; i < months; i++) {
            amountPerItemList.add(new GraphKeyValue(i+"", periodStart.plusMonths(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 0));
        }
        for (Work work : workList) {
            if(work.getWorkduration()==0.0) continue;
            Double rate = contractService.findConsultantRateByWork(work);
            if(rate==null) continue;
            WorkAmount workAmount = new WorkAmount(LocalDate.of(work.getYear(), work.getMonth()+1, 1), work.getWorkduration(), work.getWorkduration() * rate);
            Optional<GraphKeyValue> keyValue = amountPerItemList.stream().filter(graphKeyValue -> graphKeyValue.getDescription().equals(work.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))+"-01")).findFirst();
            if(!keyValue.isPresent()) {
                System.out.println("work = " + work);
                continue;
            }
            keyValue.get().addValue((int)Math.round(workAmount.amount));
        }

        String[] categories = new String[months];
        DataSeries revenueSeries = new DataSeries("Revenue");
        DataSeries earningsSeries = new DataSeries("Earnings");
        DataSeries budgetSeries = new DataSeries("Budget");
        amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))).collect(Collectors.toList());
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            if(amountPerItemList.size() > i) {
                GraphKeyValue amountPerItem = amountPerItemList.get(i);
                revenueSeries.add(new DataSeriesItem(LocalDate.parse(amountPerItem.getDescription(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).format(DateTimeFormatter.ofPattern("MMM-yyyy")), amountPerItem.getValue()));
                double expense = expenseRepository.findByPeriod(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant())).stream().mapToDouble(Expense::getAmount).sum();
                if(expense>0.0) earningsSeries.add(new DataSeriesItem(LocalDate.parse(amountPerItem.getDescription(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).format(DateTimeFormatter.ofPattern("MMM-yyyy")), amountPerItem.getValue()-expense));
            }
            List<Contract> contracts = contractService.findActiveContractsByDate(currentDate);
            double budgetSum = 0.0;
            for (Contract contract : contracts) {
                if(contract.getContractType().equals(ContractType.PERIOD)) {
                    double weeks = currentDate.getMonth().length(true) / 7.0;
                    for (Consultant consultant : contract.getConsultants()) {
                        budgetSum += (consultant.getHours() * weeks) * consultant.getRate();
                    }
                }
            }
            List<BudgetNew> budgets = budgetNewRepository.findByMonthAndYear(currentDate.getMonthValue() - 1, currentDate.getYear());
            for (BudgetNew budget : budgets) {
                budgetSum += budget.getBudget();
            }

            budgetSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(budgetSum)));
            categories[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueSeries);
        chart.getConfiguration().addSeries(budgetSeries);
        if(showEarnings) chart.getConfiguration().addSeries(earningsSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}

class WorkAmount {
    LocalDate date;
    double duration;
    double amount;

    public WorkAmount(LocalDate date, double duration, double amount) {
        this.date = date;
        this.duration = duration;
        this.amount = amount;
    }
}