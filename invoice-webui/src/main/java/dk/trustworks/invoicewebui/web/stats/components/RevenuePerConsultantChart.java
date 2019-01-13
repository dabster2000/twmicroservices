package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.jobs.CountEmployeesJob;
import dk.trustworks.invoicewebui.model.Expense;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.repositories.InvoiceRepository;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.services.WorkService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class RevenuePerConsultantChart {

    private final StatisticsService statisticsService;

    private final InvoiceRepository invoiceService;

    private final WorkService workService;

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final UserService userService;

    private final ExpenseRepository expenseRepository;

    private final CountEmployeesJob countEmployees;

    @Autowired
    public RevenuePerConsultantChart(StatisticsService statisticsService, InvoiceRepository invoiceService, WorkService workService, GraphKeyValueRepository graphKeyValueRepository, UserService userService, ExpenseRepository expenseRepository, CountEmployeesJob countEmployeesJob) {
        this.statisticsService = statisticsService;
        this.invoiceService = invoiceService;
        this.workService = workService;
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.userService = userService;
        this.expenseRepository = expenseRepository;
        this.countEmployees = countEmployeesJob;
    }

    public Chart createRevenuePerConsultantChart(User user) {
        System.out.println("RevenuePerPerConsultantChart.createRevenuePerConsultantChart");
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        LocalDate periodStart = user.getStatuses().get(0).getStatusdate();//LocalDate.of(2017, 07, 01);
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

        chart.getConfiguration().getxAxis().setCategories(statisticsService.getCategories(periodStart, periodEnd));

        DataSeries revenueSeries = new DataSeries("Revenue");

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            //double revenueSum = invoiceService.findByYearAndMonth(currentDate.getYear(), currentDate.getMonthValue() - 1).stream().filter(invoice -> invoice.type.equals(InvoiceType.INVOICE)).mapToDouble(value -> value.getInvoiceitems().stream().filter(invoiceItem -> invoiceItem.itemname.equals(user.getFirstname() + " " + user.getLastname())).mapToDouble(value1 -> value1.rate * value1.hours).sum()).sum();
            double revenue = graphKeyValueRepository.findConsultantRevenueByPeriod(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), currentDate.withDayOfMonth(currentDate.getMonth().length(currentDate.isLeapYear())).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).stream().filter(graphKeyValue -> graphKeyValue.getUuid().equals(user.getUuid())).mapToDouble(GraphKeyValue::getValue).sum();
            int userSalary = userService.getUserSalary(user, currentDate);
            double expense = expenseRepository.findByPeriod(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant())).stream().filter(expense1 -> !expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum() / countEmployees.getUsersByLocalDate(currentDate).size();

            revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), revenue - userSalary - expense));

        }

        chart.getConfiguration().addSeries(revenueSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}