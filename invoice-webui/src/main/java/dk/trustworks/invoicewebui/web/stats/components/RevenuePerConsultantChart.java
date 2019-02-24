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
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class RevenuePerConsultantChart {

    private final StatisticsService statisticsService;

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final UserService userService;

    private final ExpenseRepository expenseRepository;

    private final CountEmployeesJob countEmployees;

    @Autowired
    public RevenuePerConsultantChart(StatisticsService statisticsService, GraphKeyValueRepository graphKeyValueRepository, UserService userService, ExpenseRepository expenseRepository, CountEmployeesJob countEmployeesJob) {
        this.statisticsService = statisticsService;
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.userService = userService;
        this.expenseRepository = expenseRepository;
        this.countEmployees = countEmployeesJob;
    }

    public Chart createRevenuePerConsultantChart(User user) {
        System.out.println("RevenuePerPerConsultantChart.createRevenuePerConsultantChart");
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        LocalDate periodStart = user.getStatuses().stream().min(Comparator.comparing(UserStatus::getStatusdate)).orElse(new UserStatus(user, ConsultantType.CONSULTANT, StatusType.ACTIVE, LocalDate.now(), 0)).getStatusdate();//LocalDate.of(2017, 07, 01);
        System.out.println("createRevenuePerConsultantChart periodStart = " + periodStart);
        LocalDate periodEnd = LocalDate.now().withDayOfMonth(1);

        chart.setCaption("Gross profit for "+user.getUsername());
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

        //chart.getConfiguration().getxAxis().setCategories(statisticsService.getCategories(periodStart, periodEnd));

        DataSeries revenueSeries = new DataSeries("Revenue");

        double revenueSum = 0.0;
        int count = 1;
        int average = 3;

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            System.out.println("currentDate = " + currentDate);

            int consultantCount = userService.findWorkingEmployeesByDate(currentDate, ConsultantType.CONSULTANT).size();
            double revenue = graphKeyValueRepository.findConsultantRevenueByPeriod(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), currentDate.withDayOfMonth(currentDate.getMonth().length(currentDate.isLeapYear())).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).stream().filter(graphKeyValue -> graphKeyValue.getUuid().equals(user.getUuid())).mapToDouble(GraphKeyValue::getValue).sum();
            int userSalary = userService.getUserSalary(user, currentDate);
            double expense = expenseRepository.findByPeriod(currentDate.withDayOfMonth(1)).stream().filter(expense1 -> !expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum() / consultantCount;
            int consultantSalaries = userService.getMonthSalaries(currentDate, ConsultantType.CONSULTANT.toString());
            double expenseSalaries = expenseRepository.findByPeriod(currentDate.withDayOfMonth(1)).stream().filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum();
            double staffSalaries = (expenseSalaries - consultantSalaries) / consultantCount;

            if(expense == 0) {
                count = 1;
                revenueSum = 0.0;
                continue;
            }

            revenueSum += (revenue - userSalary - expense - staffSalaries);

            if(count == average) {
                revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), revenueSum / average));
                chart.getConfiguration().getxAxis().addCategory(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")));
                revenueSum = 0.0;
                count = 1;
                continue;
            }

            count++;
        }

        chart.getConfiguration().addSeries(revenueSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}