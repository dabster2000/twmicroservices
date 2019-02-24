package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.Expense;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.services.InvoiceService;
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
public class ExpensesPerMonthChart {

    private final ExpenseRepository expenseRepository;

    private final UserService userService;

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final InvoiceService invoiceService;

    @Autowired
    public ExpensesPerMonthChart(ExpenseRepository expenseRepository, UserService userService, GraphKeyValueRepository graphKeyValueRepository, InvoiceService invoiceService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.invoiceService = invoiceService;
    }

    public Chart createExpensesPerMonthChart() {
        System.out.println("ExpensesPerMonthChart.createExpensesPerMonthChart");
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        LocalDate periodStart = LocalDate.of(2014, 03, 01);
        LocalDate periodEnd = LocalDate.now().withDayOfMonth(1);

        chart.setCaption("Expenses, Salaries and Revenue per Employee");
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

        DataSeries revenueSeries = new DataSeries("Revenue");
        DataSeries expensesSeries = new DataSeries("Expenses");
        DataSeries salariesSeries = new DataSeries("Salaries");

        double expensesSum = 0.0;
        double salariesSum = 0.0;
        double revenueSum = 0.0;
        int count = 1;

        int average = 3;

        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.findRevenueByMonthByPeriod(periodStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")))).collect(Collectors.toList());

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            int consultantCount = userService.findWorkingEmployeesByDate(currentDate, ConsultantType.CONSULTANT).size();

            double expense = expenseRepository.findByPeriod(currentDate.withDayOfMonth(1)).stream().filter(expense1 -> !expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum() / consultantCount;
            int consultantSalaries = userService.getMonthSalaries(currentDate, ConsultantType.CONSULTANT.toString()) / consultantCount;
            double expenseSalaries = expenseRepository.findByPeriod(currentDate.withDayOfMonth(1)).stream().filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum() / consultantCount;
            double staffSalaries = (expenseSalaries - consultantSalaries);
            double invoicedAmountByMonth = invoiceService.invoicedAmountByMonth(currentDate);
            double revenue;
            if(invoicedAmountByMonth > 0.0) {
                revenue = invoicedAmountByMonth / consultantCount;
            } else {
                if(amountPerItemList.size() <= i) continue;
                revenue = ((amountPerItemList.get(i) != null) ? amountPerItemList.get(i).getValue() : 0.0) / consultantCount;
            }


            if(revenue == 0.0) {
                count = 1;
                revenueSum = 0.0;
                expensesSum = 0.0;
                salariesSum = 0.0;
                continue;
            }

            if(expense > 0.0) expensesSum += (expense + staffSalaries);
            salariesSum += consultantSalaries;
            revenueSum += revenue;

            if(count == average) {
                expensesSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), expensesSum / average));
                salariesSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), salariesSum / average));
                revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), revenueSum / average));
                chart.getConfiguration().getxAxis().addCategory(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")));
                expensesSum = 0.0;
                salariesSum = 0.0;
                revenueSum = 0.0;
                count = 1;
                continue;
            }

            count++;
        }

        chart.getConfiguration().addSeries(revenueSeries);
        chart.getConfiguration().addSeries(salariesSeries);
        chart.getConfiguration().addSeries(expensesSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}