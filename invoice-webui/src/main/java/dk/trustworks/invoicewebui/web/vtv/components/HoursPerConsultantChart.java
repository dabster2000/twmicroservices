package dk.trustworks.invoicewebui.web.vtv.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.AvailabilityDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.services.AvailabilityService;
import dk.trustworks.invoicewebui.services.BudgetService;
import dk.trustworks.invoicewebui.services.RevenueService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class HoursPerConsultantChart {

    private final RevenueService revenueService;

    private final BudgetService budgetService;

    private final AvailabilityService availabilityService;

    private final UserService userService;

    @Autowired
    public HoursPerConsultantChart(RevenueService revenueService, BudgetService budgetService, AvailabilityService availabilityService, UserService userService) {
        this.revenueService = revenueService;
        this.budgetService = budgetService;
        this.availabilityService = availabilityService;
        this.userService = userService;
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
        chart.getConfiguration().setPlotOptions(plotOptionsColumn);

        List<User> users = userService.findEmployedUsersByDate(month, true, ConsultantType.CONSULTANT);
        String[] categories = new String[users.size()];
        Number[] revenueData = new Number[users.size()];
        Number[] availableHours = new Number[users.size()];
        Number[] budgetHours = new Number[users.size()];
        Number[] vacationHours = new Number[users.size()];
        Number[] sickHours = new Number[users.size()];
        Number[] maternityLeaveHours = new Number[users.size()];

        int i = 0;
        for (User user : users) {
            double revenueHoursByMonth = revenueService.getRegisteredHoursForSingleMonthAndSingleConsultant(user.getUuid(), month); // 59
            double budgetHoursByMonth = budgetService.getConsultantBudgetHoursByMonth(user.getUuid(), month); // 117
            budgetHoursByMonth -= revenueHoursByMonth; // 58
            if(budgetHoursByMonth < 0) budgetHoursByMonth = 0;

            AvailabilityDocument availability = availabilityService.getConsultantAvailabilityByMonth(user.getUuid(), month);
            double availableHoursByMonth = availability.getNetAvailableHours(); // 147
            availableHoursByMonth -= revenueHoursByMonth + budgetHoursByMonth; // 147 - 59 - 58 = 30
            if(availableHoursByMonth < 0) availableHoursByMonth = 0;

            double vacationHoursByMonth = availability.getNetVacation();  // 44
            double sickHoursByMonth = availability.getNetSickdays(); // 15
            double maternityLeaveHoursByMonth = availability.getNetMaternityLeave();

            if(availableHoursByMonth < 0) {
                budgetHoursByMonth += availableHoursByMonth; // 58 + (-14) = 44
                availableHoursByMonth = 0; // 0
            }
            if(budgetHoursByMonth < 0) budgetHoursByMonth = 0;

            revenueData[i] = NumberUtils.round(revenueHoursByMonth, 0);
            budgetHours[i] = NumberUtils.round(budgetHoursByMonth, 0);
            availableHours[i] = NumberUtils.round(availableHoursByMonth, 0);
            vacationHours[i] = NumberUtils.round(vacationHoursByMonth, 0);
            sickHours[i] = NumberUtils.round(sickHoursByMonth, 0);
            maternityLeaveHours[i] = NumberUtils.round(maternityLeaveHoursByMonth, 0);

            categories[i++] = user.getUsername();
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

        ListSeries sickHoursSeries = new ListSeries("sick hours", sickHours);
        PlotOptionsColumn poc4 = new PlotOptionsColumn();
        poc4.setColor(new SolidColor("#FD5F5B"));
        sickHoursSeries.setPlotOptions(poc4);
        chart.getConfiguration().addSeries(sickHoursSeries);

        ListSeries vacationHoursSeries = new ListSeries("vacation hours", vacationHours);
        PlotOptionsColumn poc5 = new PlotOptionsColumn();
        poc5.setColor(new SolidColor("#FFD864"));
        vacationHoursSeries.setPlotOptions(poc5);
        chart.getConfiguration().addSeries(vacationHoursSeries);

        ListSeries maternityLeaveHoursSeries = new ListSeries("maternity leave hours", maternityLeaveHours);
        PlotOptionsColumn poc6 = new PlotOptionsColumn();
        poc6.setColor(new SolidColor("#FFE7A2"));
        vacationHoursSeries.setPlotOptions(poc6);
        chart.getConfiguration().addSeries(maternityLeaveHoursSeries);

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