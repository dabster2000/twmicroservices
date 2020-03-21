package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.dto.ExpenseDocument;
import dk.trustworks.invoicewebui.model.dto.UserExpenseDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ExpensesPerMonthChart {

    private final StatisticsService statisticsService;

    private final UserService userService;

    @Autowired
    public ExpensesPerMonthChart(StatisticsService statisticsService, UserService userService) {
        this.statisticsService = statisticsService;
        this.userService = userService;
    }

    public Chart createUserExpensePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Categorized Expenses");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setStacking(Stacking.NORMAL);
        chart.getConfiguration().setPlotOptions(plotOptionsColumn);

        YAxis yAxis = new YAxis();
        yAxis.setMin(0);
        chart.getConfiguration().getyAxis().setTitle("");
        StackLabels sLabels = new StackLabels(true);
        yAxis.setStackLabels(sLabels);
        chart.getConfiguration().addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        PlotOptionsColumn poc4 = new PlotOptionsColumn();
        poc4.setColor(new SolidColor("#54D69E"));
        ListSeries consultantSalarySeries = new ListSeries("Consultant salaries");
        consultantSalarySeries.setPlotOptions(poc4);

        ListSeries staffSalarySeries = new ListSeries("Staff salaries");
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#98E6C4"));
        staffSalarySeries.setPlotOptions(poc3);

        ListSeries personaleExpensesSeries = new ListSeries("Consultant expenses");
        PlotOptionsColumn poc2 = new PlotOptionsColumn();
        poc2.setColor(new SolidColor("#CFD6E3"));
        personaleExpensesSeries.setPlotOptions(poc2);

        PlotOptionsColumn poc5 = new PlotOptionsColumn();
        poc5.setColor(new SolidColor("#A0ADC7"));
        ListSeries lokaleExensesSeries = new ListSeries("Office expenses");
        lokaleExensesSeries.setPlotOptions(poc5);

        PlotOptionsColumn poc6 = new PlotOptionsColumn();
        poc6.setColor(new SolidColor("#7084AC"));
        ListSeries salgExensesSeries = new ListSeries("Sales expenses");
        salgExensesSeries.setPlotOptions(poc6);

        PlotOptionsColumn poc7 = new PlotOptionsColumn();
        poc7.setColor(new SolidColor("#415B90"));
        ListSeries productionExensesSeries = new ListSeries("Production expenses");
        productionExensesSeries.setPlotOptions(poc7);

        PlotOptionsColumn poc8 = new PlotOptionsColumn();
        poc8.setColor(new SolidColor("#123375"));
        ListSeries administrationExensesSeries = new ListSeries("Administration expenses");
        administrationExensesSeries.setPlotOptions(poc8);

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        String[] monthNames = new String[months];
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            //List<UserExpenseDocument> expensesByMonth = statisticsService.getConsultantsExpensesByMonth(currentDate);

            List<ExpenseDocument> allExpensesByMonth = statisticsService.getAllExpensesByMonth(currentDate);

            double consultantNetSalaries = userService.getMonthSalaries(currentDate, ConsultantType.CONSULTANT.toString());
            double staffNetSalaries = userService.getMonthSalaries(currentDate, ConsultantType.STAFF.toString());

            double totalSalaries = Math.round(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteSalaries).sum());

            //long numberOfConsultants = statisticsService.countActiveConsultantCountByMonth(currentDate);
            //long numberOfStaff = statisticsService.countActiveEmployeeTypesByMonth(currentDate, ConsultantType.STAFF);

            double forholdstal = totalSalaries / (consultantNetSalaries + staffNetSalaries);

            final double staffSalaries = NumberUtils.round(staffNetSalaries * forholdstal, 0);//(expenseSalaries - consultantSalaries) / consultantCount;
            final double consultantSalaries = NumberUtils.round(consultantNetSalaries * forholdstal, 0);

            consultantSalarySeries.addData(consultantSalaries);
            staffSalarySeries.addData(staffSalaries);
            personaleExpensesSeries.addData(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteEmployee_expenses).sum());
            lokaleExensesSeries.addData(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteHousing).sum());
            salgExensesSeries.addData(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteSales).sum());
            productionExensesSeries.addData(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteProduktion).sum());
            administrationExensesSeries.addData(allExpensesByMonth.stream().mapToDouble(ExpenseDocument::geteAdministration).sum());

            /*
            consultantSalarySeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getSalary).sum()));
            personaleExpensesSeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getSharedExpense).sum()));
            staffSalarySeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getStaffSalaries).sum()));
            lokaleExensesSeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getPersonaleExpense).sum()));
            */


            monthNames[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }

        chart.getConfiguration().getxAxis().setCategories(monthNames);
        chart.getConfiguration().addSeries(consultantSalarySeries);
        chart.getConfiguration().addSeries(staffSalarySeries);
        chart.getConfiguration().addSeries(personaleExpensesSeries);
        chart.getConfiguration().addSeries(lokaleExensesSeries);
        chart.getConfiguration().addSeries(salgExensesSeries);
        chart.getConfiguration().addSeries(productionExensesSeries);
        chart.getConfiguration().addSeries(administrationExensesSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    public Chart createExpensePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Categorized Expenses From Economics");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setStacking(Stacking.NORMAL);
        chart.getConfiguration().setPlotOptions(plotOptionsColumn);

        YAxis yAxis = new YAxis();
        yAxis.setMin(0);
        chart.getConfiguration().getyAxis().setTitle("");
        StackLabels sLabels = new StackLabels(true);
        yAxis.setStackLabels(sLabels);
        chart.getConfiguration().addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        PlotOptionsColumn poc4 = new PlotOptionsColumn();
        poc4.setColor(new SolidColor("#54D69E"));
        ListSeries salarySeries = new ListSeries("Average consultant salaries");
        salarySeries.setPlotOptions(poc4);

        ListSeries staffSalarySeries = new ListSeries("Average staff salaries");
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#CFD6E3"));
        staffSalarySeries.setPlotOptions(poc3);

        ListSeries sharedExpensesSeries = new ListSeries("Average shared expenses");
        PlotOptionsColumn poc2 = new PlotOptionsColumn();
        poc2.setColor(new SolidColor("#7084AC"));
        sharedExpensesSeries.setPlotOptions(poc2);

        PlotOptionsColumn poc5 = new PlotOptionsColumn();
        poc5.setColor(new SolidColor("#123375"));
        ListSeries staffExensesSeries = new ListSeries("Average consultant expenses");
        staffExensesSeries.setPlotOptions(poc5);

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        String[] monthNames = new String[months];
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            List<UserExpenseDocument> expensesByMonth = statisticsService.getConsultantsExpensesByMonth(currentDate);

            salarySeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getSalary).sum()));
            sharedExpensesSeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getSharedExpense).sum()));
            staffSalarySeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getStaffSalaries).sum()));
            staffExensesSeries.addData(Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getPersonaleExpense).sum()));

            monthNames[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }

        chart.getConfiguration().getxAxis().setCategories(monthNames);
        chart.getConfiguration().addSeries(salarySeries);
        chart.getConfiguration().addSeries(staffSalarySeries);
        chart.getConfiguration().addSeries(sharedExpensesSeries);
        chart.getConfiguration().addSeries(staffExensesSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}