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

        chart.setCaption("Gross profit for ");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        DataSeries hoursRegisteredSeries = new DataSeries("hours registered");
        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setColorByPoint(true);
        hoursRegisteredSeries.setPlotOptions(plotOptionsColumn);

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
        chart.getConfiguration().addSeries(budgetHoursSeries);

        List<User> users = userService.findEmployedUsersByDate(month, ConsultantType.CONSULTANT);
        String[] categories = new String[users.size()];

        int i = 0;
        for (User user : users) {
            double consultantRevenueHoursByMonth = statisticsService.getConsultantRevenueHoursByMonth(user, month);
            double budgetHoursByMonth = statisticsService.getConsultantBudgetHoursByMonth(user, month);
            categories[i++] = user.getUsername();
            hoursRegisteredSeries.add(new DataSeriesItem(user.getUsername(), consultantRevenueHoursByMonth));
            budgetHoursSeries.add(new DataSeriesItem(user.getUsername(), budgetHoursByMonth));
        }

        XAxis x = new XAxis();
        x.setType(AxisType.CATEGORY);
        x.setCategories(categories);
        chart.getConfiguration().addxAxis(x);

        chart.getConfiguration().addSeries(budgetHoursSeries);
        chart.getConfiguration().addSeries(hoursRegisteredSeries);


        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}