package dk.trustworks.invoicewebui.web.resourceplanning.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.Budget;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.repositories.BudgetRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hans on 19/12/2016.
 */

@SpringComponent
@SpringUI
public class SalesHeatMap {

    private final BudgetRepository budgetRepository;

    private final UserRepository userRepository;

    private final ContractService contractService;

    double[] monthTotalAvailabilites;
    double[] monthAvailabilites;

    @Autowired
    public SalesHeatMap(BudgetRepository budgetRepository, UserRepository userRepository, ContractService contractService) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.contractService = contractService;
    }

    public Component getChart(LocalDate localDateStart, LocalDate localDateEnd) {
        int monthPeriod = new Period(localDateStart, localDateEnd, PeriodType.months()).getMonths()+1;
        monthTotalAvailabilites = new double[monthPeriod];
        monthAvailabilites = new double[monthPeriod];
        List<User> users = userRepository.findByActiveTrue();
        String[] monthNames = getMonthNames(localDateStart, localDateEnd);

        Chart chart = new Chart();
        chart.setWidth("100%");

        Configuration config = chart.getConfiguration();
        config.getChart().setType(ChartType.HEATMAP);
        config.getChart().setMarginTop(40);
        config.getChart().setMarginBottom(40);

        config.getTitle().setText("Employee Availability Per Month");

        config.getColorAxis().setMin(0);
        config.getColorAxis().setMax(100);
        config.getColorAxis().setMinColor(SolidColor.WHITE);
        config.getColorAxis().setMaxColor(SolidColor.GREEN);

        config.getLegend().setLayout(LayoutDirection.VERTICAL);
        config.getLegend().setAlign(HorizontalAlign.RIGHT);
        config.getLegend().setMargin(0);
        config.getLegend().setVerticalAlign(VerticalAlign.TOP);
        config.getLegend().setY(25);
        config.getLegend().setSymbolHeight(320);

        HeatSeries rs = new HeatSeries("% availability");
        int userNumber = 0;

        for (User user : users) {
            List<Budget> userBudgets = budgetRepository.findByPeriodAndUseruuid(
                    Integer.parseInt(localDateStart.toString("yyyyMMdd")),
                    Integer.parseInt(localDateEnd.toString("yyyyMMdd")),
                    user.getUuid());
            LocalDate localDate = localDateStart;
            int m = 0;
            while(localDate.isBefore(localDateEnd) || localDate.isEqual(localDateEnd)) {
                final LocalDate tempDate = localDate;
                double budgetSum = userBudgets.stream()
                        .filter(budget -> budget.getYear() == tempDate.getYear() && budget.getMonth() + 1 == tempDate.getMonthOfYear())
                        .mapToDouble(value -> value.getBudget() / contractService.findConsultantRate(value.getYear(), value.getMonth(), 1, user, value.getTask()))
                        .sum();
                List<UserStatus> userStatuses = user.getStatuses().stream().sorted(Comparator.comparing(UserStatus::getStatusdate)).collect(Collectors.toList());
                UserStatus userStatus = userStatuses.get(0);
                for (UserStatus userStatusIteration : userStatuses) {
                    if(userStatusIteration.getStatusdate().isAfter(userStatus.getStatusdate())) continue;
                    userStatus = userStatusIteration;
                }

                int weekDays = countWeekDays(localDate, localDate.plusMonths(1));

                double budget = Math.round((weekDays * (userStatus.getAllocation()/5.0)) - budgetSum);

                if(budget < 0.0) budget = 0.0;

                budget = Math.round(budget / Math.round(weekDays * (userStatus.getAllocation()/5.0)) * 100.0);

                if(user.getUuid().equals("7948c5e8-162c-4053-b905-0f59a21d7746")) {
                    System.out.println("userNumber = " + userNumber);
                    System.out.println("localDate = " + localDate);
                    System.out.println("weekDays = " + weekDays);
                    System.out.println("userStatus = " + (userStatus.getAllocation()/5.0));
                    System.out.println("budgetSum = " + budgetSum);
                    System.out.println("budget = " + budget);
                    System.out.println("--- --- --- --- --- ---");
                }

                monthAvailabilites[m] += Math.round(budget);
                monthTotalAvailabilites[m] += 100;

                rs.addHeatPoint(m, userNumber, Math.round(budget));

                localDate = localDate.plusMonths(1);
                m++;
            }
            userNumber++;
        }

        config.getxAxis().setCategories(monthNames);
        config.getyAxis().setCategories(users.stream().map(User::getUsername).toArray(String[]::new));

        PlotOptionsHeatmap plotOptionsHeatmap = new PlotOptionsHeatmap();
        plotOptionsHeatmap.setDataLabels(new DataLabels());
        plotOptionsHeatmap.getDataLabels().setEnabled(true);
        plotOptionsHeatmap.getStates().getHover().setFillColor(SolidColor.BLACK);

        SeriesTooltip tooltip = new SeriesTooltip();
        tooltip.setHeaderFormat("{series.name}<br/>");
        tooltip.setPointFormat("Amount: <b>{point.value}</b> ");
        plotOptionsHeatmap.setTooltip(tooltip);
        config.setPlotOptions(plotOptionsHeatmap);

        config.setSeries(rs);

        chart.drawChart(config);
        chart.setHeight("700px");

        return chart;
    }

    public Component getAvailabilityChart(LocalDate localDateStart, LocalDate localDateEnd) {
        int monthPeriod = new Period(localDateStart, localDateEnd, PeriodType.months()).getMonths()+1;
        String[] monthNames = getMonthNames(localDateStart, localDateEnd);
        Chart chart = new Chart(ChartType.AREASPLINE);
        chart.setHeight("450px");

        Configuration conf = chart.getConfiguration();

        conf.setTitle(new Title("Total % availability"));

        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setAlign(HorizontalAlign.LEFT);
        legend.setFloating(true);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(150);
        legend.setY(100);
        conf.setLegend(legend);

        XAxis xAxis = new XAxis();
        xAxis.setCategories(monthNames);
        xAxis.setLineColor(SolidColor.GREEN);
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle(new AxisTitle("Total Availability"));
        conf.addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        // Customize tooltip formatting
        tooltip.setHeaderFormat("");
        tooltip.setPointFormat("{series.name}: {point.y} %");
        conf.setTooltip(tooltip);

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setColor(SolidColor.GREEN);
        plotOptions.setFillOpacity(0.5);
        conf.setPlotOptions(plotOptions);

        ListSeries listSeries = new ListSeries();
        for (int j = 0; j < monthPeriod; j++) {
            listSeries.addData(Math.round(monthAvailabilites[j] / monthTotalAvailabilites[j] * 100.0));
        }

        conf.addSeries(listSeries);

        chart.drawChart(conf);
        chart.setHeight("700px");

        return chart;
    }


    private String[] getMonthNames(LocalDate localDateStart, LocalDate localDateEnd) {
        String[] monthNames = new String[new Period(localDateStart, localDateEnd, PeriodType.months()).getMonths()+1];
        for (int i = 0; i < monthNames.length; i++) {
            monthNames[i] = localDateStart.plusMonths(i).monthOfYear().getAsShortText();
        }
        return monthNames;
    }

    public int countWeekDays(LocalDate periodStart, LocalDate periodEnd) {
        LocalDate weekday = periodStart;

        if (periodStart.getDayOfWeek() == DateTimeConstants.SATURDAY ||
                periodStart.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            weekday = weekday.plusWeeks(1).withDayOfWeek(DateTimeConstants.MONDAY);
        }

        int count = 0;
        while (weekday.isBefore(periodEnd)) {
            count++;
            if (weekday.getDayOfWeek() == DateTimeConstants.FRIDAY)
                weekday = weekday.plusDays(3);
            else
                weekday = weekday.plusDays(1);
        }
        return count;
    }
}
