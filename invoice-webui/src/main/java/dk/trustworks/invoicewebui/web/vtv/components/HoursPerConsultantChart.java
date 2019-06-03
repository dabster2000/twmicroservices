package dk.trustworks.invoicewebui.web.vtv.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.jobs.CountEmployeesJob;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class HoursPerConsultantChart {

    private final StatisticsService statisticsService;

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final UserService userService;

    private final ExpenseRepository expenseRepository;

    @Autowired
    public HoursPerConsultantChart(StatisticsService statisticsService, GraphKeyValueRepository graphKeyValueRepository, UserService userService, ExpenseRepository expenseRepository, CountEmployeesJob countEmployeesJob) {
        this.statisticsService = statisticsService;
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.userService = userService;
        this.expenseRepository = expenseRepository;
    }

    public Chart createHoursPerConsultantChart(LocalDate month) {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Consultant hours distribution");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setStacking(Stacking.NORMAL);
        //plotOptionsColumn.setColorByPoint(true);
        chart.getConfiguration().setPlotOptions(plotOptionsColumn);
/*
        DataSeries budgetHoursSeries = new DataSeries();
        PlotOptionsSpline splinePlotOptions = new PlotOptionsSpline();
        Marker marker = new Marker();
        marker.setLineWidth(2);
        marker.setLineColor(new SolidColor("black"));
        marker.setFillColor(new SolidColor("white"));
        splinePlotOptions.setMarker(marker);
        splinePlotOptions.setColor(new SolidColor("black"));
        budgetHoursSeries.setPlotOptions(splinePlotOptions);
        budgetHoursSeries.setName("budget hours");
*/
        List<User> users = userService.findEmployedUsersByDate(month, ConsultantType.CONSULTANT);
        String[] categories = new String[users.size()];
        Number[] revenueData = new Number[users.size()];
        Number[] availableHours = new Number[users.size()];
        Number[] budgetHours = new Number[users.size()];

        int i = 0;
        for (User user : users) {
            double revenueHoursByMonth = statisticsService.getConsultantRevenueHoursByMonth(user, month);
            double budgetHoursByMonth = statisticsService.getConsultantBudgetHoursByMonth(user, month);
            budgetHoursByMonth -= revenueHoursByMonth;
            if(budgetHoursByMonth < 0) budgetHoursByMonth = 0;
            double availableHoursByMonth = statisticsService.getConsultantAvailabilityByMonth(user, month).getAvailableHours();
            availableHoursByMonth -= revenueHoursByMonth + budgetHoursByMonth;
            if(availableHoursByMonth < 0) availableHoursByMonth = 0;

            revenueData[i] = revenueHoursByMonth;
            budgetHours[i] = budgetHoursByMonth;
            availableHours[i] = availableHoursByMonth;

            categories[i++] = user.getUsername();
            //budgetHoursSeries.add(new DataSeriesItem(user.getUsername(), budgetHoursByMonth));
        }

        XAxis x = new XAxis();
        x.setType(AxisType.CATEGORY);
        x.setCategories(categories);
        chart.getConfiguration().addxAxis(x);

        YAxis yAxis = new YAxis();
        yAxis.setMin(0);
        yAxis.setTitle(new AxisTitle("hours"));
        StackLabels sLabels = new StackLabels(true);
        yAxis.setStackLabels(sLabels);
        chart.getConfiguration().addyAxis(yAxis);

        ListSeries availableHoursSeries = new ListSeries("available hours", availableHours);
        PlotOptionsColumn poc1 = new PlotOptionsColumn();
        poc1.setColor(new SolidColor("#CFD6E3"));
        availableHoursSeries.setPlotOptions(poc1);
        chart.getConfiguration().addSeries(availableHoursSeries);

        ListSeries budgetHoursSeries = new ListSeries("budget hours", budgetHours);
        PlotOptionsColumn poc2 = new PlotOptionsColumn();
        poc2.setColor(new SolidColor("#7084AC"));
        budgetHoursSeries.setPlotOptions(poc2);
        chart.getConfiguration().addSeries(budgetHoursSeries);

        ListSeries hoursRegisteredSeries = new ListSeries("registered hours", revenueData);
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#123375"));
        hoursRegisteredSeries.setPlotOptions(poc3);
        chart.getConfiguration().addSeries(hoursRegisteredSeries);

        //chart.getConfiguration().addSeries(budgetHoursSeries);


        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;


    }

}