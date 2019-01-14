package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class RevenuePerMonthChart {

    private final StatisticsService statisticsService;

    @Autowired
    public RevenuePerMonthChart(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public Chart createRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        return createRevenuePerMonthChart(periodStart, periodEnd, true);
    }

    public Chart createRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd, boolean showEarnings) {
        System.out.println("RevenuePerMonthChart.createRevenuePerMonthChart");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "], showEarnings = [" + showEarnings + "]");
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Revenue during Fiscal Year 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
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

        chart.getConfiguration().getxAxis().setCategories(statisticsService.getCategories(periodStart, periodEnd));
        chart.getConfiguration().addSeries(statisticsService.calcBillableHoursRevenuePerMonth(periodStart, periodEnd));
        if(showEarnings) chart.getConfiguration().addSeries(statisticsService.calcRevenuePerMonth(periodStart, periodEnd));
        chart.getConfiguration().addSeries(statisticsService.calcBudgetPerMonth(periodStart, periodEnd));
        if(showEarnings) chart.getConfiguration().addSeries(statisticsService.calcEarningsPerMonth(periodStart, periodEnd));
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    /*
    private void calcRevenuePerMonth2(LocalDate periodStart, int months, List<GraphKeyValue> amountPerItemList, String[] categories, DataSeries revenueSeries, DataSeries earningsSeries, DataSeries budgetSeries) {
        amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")))).collect(Collectors.toList());
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            if(amountPerItemList.size() > i) {
                GraphKeyValue amountPerItem = amountPerItemList.get(i);
                if(amountPerItem!=null) revenueSeries.add(new DataSeriesItem(LocalDate.parse(amountPerItem.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")).format(DateTimeFormatter.ofPattern("MMM-yyyy")), amountPerItem.getValue()));
                double expense = expenseRepository.findByPeriod(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant())).stream().mapToDouble(Expense::getAmount).sum();
                if(amountPerItem!=null) if(expense>0.0) earningsSeries.add(new DataSeriesItem(LocalDate.parse(amountPerItem.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")).format(DateTimeFormatter.ofPattern("MMM-yyyy")), amountPerItem.getValue()-expense));
            }
            List<Contract> contracts = contractService.findActiveContractsByDate(currentDate, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            double budgetSum = 0.0;
            for (Contract contract : contracts) {
                if(contract.getContractType().equals(ContractType.PERIOD)) {
                    //currentDate.getMonth().maxLength() / 7.0;
                    for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                        double weeks = workService.getWorkDaysInMonth(contractConsultant.getUser().getUuid(), currentDate) / 5.0;
                        List<Work> workList = workRepository.findByPeriodAndUserUUID(
                                currentDate.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                currentDate.withDayOfMonth(currentDate.lengthOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                contractConsultant.getUser().getUuid());
                        double notWork = 0.0;
                        for (Work work : workList) {
                            if(work.getTask().getUuid().equals("02bf71c5-f588-46cf-9695-5864020eb1c4") ||
                                    work.getTask().getUuid().equals("f585f46f-19c1-4a3a-9ebd-1a4f21007282")) notWork += work.getWorkduration();
                        }
                        budgetSum += ((contractConsultant.getHours() * weeks) - notWork) * contractConsultant.getRate();
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
    }
    */
}