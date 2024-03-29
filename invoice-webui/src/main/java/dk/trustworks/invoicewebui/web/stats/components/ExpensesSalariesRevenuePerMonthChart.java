package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.Team;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.AvailabilityService;
import dk.trustworks.invoicewebui.services.FinanceService;
import dk.trustworks.invoicewebui.services.RevenueService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static dk.trustworks.invoicewebui.model.enums.ConsultantType.CONSULTANT;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ExpensesSalariesRevenuePerMonthChart {

    private final UserService userService;

    private final RevenueService revenueService;

    private final FinanceService financeService;

    private final AvailabilityService availabilityService;

    private final TeamRestService teamRestService;

    @Autowired
    public ExpensesSalariesRevenuePerMonthChart(UserService userService, RevenueService revenueService, FinanceService financeService, AvailabilityService availabilityService, TeamRestService teamRestService) {
        this.userService = userService;
        this.revenueService = revenueService;
        this.financeService = financeService;
        this.availabilityService = availabilityService;
        this.teamRestService = teamRestService;
    }

    public Chart createExpensesPerMonthChart() {
        System.out.println("ExpensesPerMonthChart.createExpensesPerMonthChart");
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        LocalDate periodStart = LocalDate.of(2014, 7, 1);
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
        PlotOptionsAreaspline poc3 = new PlotOptionsAreaspline();
        poc3.setColor(new SolidColor("#123375"));
        revenueSeries.setPlotOptions(poc3);

        DataSeries expensesSeries = new DataSeries("Expenses");
        PlotOptionsAreaspline poc2 = new PlotOptionsAreaspline();
        poc2.setColor(new SolidColor("#FD5F5B"));
        expensesSeries.setPlotOptions(poc2);

        DataSeries salariesSeries = new DataSeries("Salaries");
        PlotOptionsAreaspline poc1 = new PlotOptionsAreaspline();
        poc1.setColor(new SolidColor("#54D69E"));
        salariesSeries.setPlotOptions(poc1);

        double expensesSum = 0.0;
        double salariesSum = 0.0;
        double revenueSum = 0.0;
        int count = 1;

        int average = 3;

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        List<GraphKeyValue> revenuePerMonth = revenueService.getInvoicedOrRegisteredRevenueByPeriod(periodStart, periodEnd.plusMonths(2));

        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            //double consultantCount = availabilityService.countActiveConsultantsByMonth(currentDate);

            String[] teamList = teamRestService.getAllTeams().stream().filter(Team::isTeamleadbonus).map(Team::getUuid).toArray(String[]::new);
            List<User> usersFromTeams = teamRestService.getUniqueUsersFromTeamsByMonth(currentDate, teamList);
            double consultantCount = usersFromTeams.size();

            double consultantSalariesByMonth = NumberUtils.round((UserService.calcMonthSalaries(usersFromTeams, currentDate, CONSULTANT.toString()) / consultantCount), 0);
            double sharedExpensesAndStaffSalariesByMonth =  NumberUtils.round((financeService.calcAllExpensesByMonth(currentDate) / consultantCount) - consultantSalariesByMonth, 0);

            //double sharedExpensesAndStaffSalariesByMonth = NumberUtils.round(statisticsService.getSharedExpensesAndStaffSalariesByMonth(currentDate) / consultantCount, 0);
            //double consultantSalariesByMonth = NumberUtils.round(((statisticsService.calcAllUserExpensesByMonth(currentDate) / consultantCount) - sharedExpensesAndStaffSalariesByMonth) , 0);
            double revenue = NumberUtils.round(revenuePerMonth.get(i).getValue() / consultantCount, 0);

            if(revenue == 0.0 || sharedExpensesAndStaffSalariesByMonth == 0.0 || consultantSalariesByMonth == 0.0) {
                count = 1;
                revenueSum = 0.0;
                expensesSum = 0.0;
                salariesSum = 0.0;
                continue;
            }

            expensesSum += (sharedExpensesAndStaffSalariesByMonth);
            salariesSum += consultantSalariesByMonth;
            revenueSum += revenue;

            if(count == average) {
                expensesSeries.add(new DataSeriesItem(currentDate.minusMonths(2).format(DateTimeFormatter.ofPattern("QQQ/yy")), expensesSum / average));
                salariesSeries.add(new DataSeriesItem(currentDate.minusMonths(2).format(DateTimeFormatter.ofPattern("QQQ/yy")), salariesSum / average));
                revenueSeries.add(new DataSeriesItem(currentDate.minusMonths(2).format(DateTimeFormatter.ofPattern("QQQ/yy")), revenueSum / average));
                chart.getConfiguration().getxAxis().addCategory(currentDate.minusMonths(2).format(DateTimeFormatter.ofPattern("QQQ/yy")));
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