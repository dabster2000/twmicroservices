package dk.trustworks.invoicewebui.web.vtv.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.BudgetDocument;
import dk.trustworks.invoicewebui.model.dto.EmployeeAggregateData;
import dk.trustworks.invoicewebui.services.BiService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class HoursPerConsultantChart {

    private final BiService biService;

    @Autowired
    public HoursPerConsultantChart(BiService biService) {
        this.biService = biService;
    }

    public Chart createHoursPerConsultantChart(LocalDate month, List<User> users, boolean adjustBudgets) {
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

        //List<User> users = userService.findEmployedUsersByDate(month, true, ConsultantType.CONSULTANT);
        String[] categories = new String[users.size()];
        Number[] revenueData = new Number[users.size()];
        Number[] availableHours = new Number[users.size()];
        Number[] budgetHours = new Number[users.size()];
        Number[] vacationHours = new Number[users.size()];
        Number[] sickHours = new Number[users.size()];
        Number[] maternityLeaveHours = new Number[users.size()];

        int i = 0;

        List<EmployeeAggregateData> dataList = biService.getEmployeeAggregateDataByPeriod(month, month);
        for (User user : users) {
            Optional<EmployeeAggregateData> userData = dataList.stream().filter(e -> e.getUseruuid().equals(user.getUuid())).findFirst();
            if(!userData.isPresent()) continue;

            double revenueHoursByMonth = userData.get().getRegisteredHours(); //registeredHoursPerConsultant.stream().filter(g -> g.getUuid().equals(user.getUuid()) && g.getDescription().equals(DateUtils.stringIt(month))).mapToDouble(GraphKeyValue::getValue).sum();;
            double budgetHoursByMonth = adjustBudgets?userData.get().getBudgetHours():userData.get().getBudgetHoursWithNoAvailabilityAdjustment(); //budgetDocuments.stream().filter(b -> b.getUser().getUuid().equals(user.getUuid()) && b.getMonth().isEqual(month.withDayOfMonth(1))).mapToDouble(BudgetDocument::getBudgetHours).sum();
            budgetHoursByMonth -= revenueHoursByMonth; // 58
            if(budgetHoursByMonth < 0) budgetHoursByMonth = 0;

            double availableHoursByMonth = userData.get().netAvailableHours; //availabilityDocument.getNetAvailableHours();

            availableHoursByMonth -= revenueHoursByMonth + budgetHoursByMonth; // 147 - 59 - 58 = 30
            if(availableHoursByMonth < 0) availableHoursByMonth = 0;

            double vacationHoursByMonth = userData.get().getVacation();//availabilityDocument.getNetVacation();  // 44
            double sickHoursByMonth = userData.get().getSickdays(); //availabilityDocument.getNetSickdays(); // 15
            double maternityLeaveHoursByMonth = userData.get().getMaternityLeave(); //availabilityDocument.getNetMaternityLeave();

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
        budgetHoursSeries.setId("budget_hours");
        PlotOptionsColumn poc2 = new PlotOptionsColumn();
        poc2.setColor(new SolidColor("#7084AC"));
        budgetHoursSeries.setPlotOptions(poc2);
        chart.getConfiguration().addSeries(budgetHoursSeries);

        chart.addPointClickListener(event -> {
            if(!event.getSeries().getId().equals("budget_hours")) return;
            Optional<EmployeeAggregateData> userData = dataList.stream().filter(e -> e.getUseruuid().equals(users.get(event.getPointIndex()).getUuid())).findFirst();
            if(!userData.isPresent()) return;
            EmployeeAggregateData data = userData.get();
            StringBuilder text = new StringBuilder();
            for (BudgetDocument budgetDocument : data.getBudgetDocuments()) {
                if(budgetDocument.getBudgetHours()==0.0) continue;
                text.append(budgetDocument.getClient().getName()).append(" [").append(budgetDocument.getContract().getName()).append("]: ").append(budgetDocument.getBudgetHoursWithNoAvailabilityAdjustment()).append("\n");
            }
            Notification.show(event.getSeries().getId(), text.toString(), Notification.Type.TRAY_NOTIFICATION);
        });

        ListSeries hoursRegisteredSeries = new ListSeries("registered hours", revenueData);
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#123375"));
        hoursRegisteredSeries.setPlotOptions(poc3);
        chart.getConfiguration().addSeries(hoursRegisteredSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;


    }


    public Chart createNonAdjustedHoursPerConsultantChart(LocalDate month, List<User> users) {
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

        //List<User> users = userService.findEmployedUsersByDate(month, true, ConsultantType.CONSULTANT);
        String[] categories = new String[users.size()];
        Number[] revenueData = new Number[users.size()];
        Number[] availableHours = new Number[users.size()];
        Number[] budgetHours = new Number[users.size()];
        Number[] vacationHours = new Number[users.size()];
        Number[] sickHours = new Number[users.size()];
        Number[] maternityLeaveHours = new Number[users.size()];

        int i = 0;
        //List<BudgetDocument> budgetDocuments = budgetService.getConsultantBudgetHoursByPeriodDocuments(month.withDayOfMonth(1), month.withDayOfMonth(1).plusMonths(1));
        //List<AvailabilityDocument> availabilityDocuments = availabilityService.getConsultantAvailabilityByPeriod(month.withDayOfMonth(1), month.withDayOfMonth(1).plusMonths(1));
        //List<GraphKeyValue> registeredHoursPerConsultant = revenueService.getRegisteredHoursPerConsultantForSingleMonth(month);

        for (User user : users) {
            List<EmployeeAggregateData> dataList = biService.getEmployeeAggregateDataByPeriod(month, month);
            Optional<EmployeeAggregateData> userData = dataList.stream().filter(e -> e.getUseruuid().equals(user.getUuid())).findFirst();
            if(!userData.isPresent()) continue;

            //double revenueHoursByMonth = revenueService.getRegisteredHoursForSingleMonthAndSingleConsultant(user.getUuid(), month); // 59
            double revenueHoursByMonth = userData.get().getRegisteredHours(); //registeredHoursPerConsultant.stream().filter(g -> g.getUuid().equals(user.getUuid()) && g.getDescription().equals(DateUtils.stringIt(month))).mapToDouble(GraphKeyValue::getValue).sum();;
            //double revenueHoursByMonth = registeredHours.map(GraphKeyValue::getValue).orElse(0.0);
            //double budgetHoursByMonth = budgetService.getConsultantBudgetHoursByMonth(user.getUuid(), month); // 117
            double budgetHoursByMonth = userData.get().getBudgetHoursWithNoAvailabilityAdjustment(); //budgetDocuments.stream().filter(b -> b.getUser().getUuid().equals(user.getUuid()) && b.getMonth().isEqual(month.withDayOfMonth(1))).mapToDouble(BudgetDocument::getBudgetHours).sum();
            budgetHoursByMonth -= revenueHoursByMonth; // 58
            if(budgetHoursByMonth < 0) budgetHoursByMonth = 0;

            //AvailabilityDocument availability = availabilityService.getConsultantAvailabilityByMonth(user.getUuid(), month);
            //AvailabilityDocument availabilityDocument = availabilityDocuments.stream().filter(a -> a.getUser().getUuid().equals(user.getUuid()) && a.getMonth().isEqual(month)).findAny().orElse(new AvailabilityDocument(user, month, 0.0, 0.0, 0.0, 0.0, ConsultantType.CONSULTANT, StatusType.TERMINATED));
            double availableHoursByMonth = userData.get().netAvailableHours; //availabilityDocument.getNetAvailableHours();

            //double availableHoursByMonth = availability.getNetAvailableHours(); // 147
            availableHoursByMonth -= revenueHoursByMonth + budgetHoursByMonth; // 147 - 59 - 58 = 30
            if(availableHoursByMonth < 0) availableHoursByMonth = 0;

            double vacationHoursByMonth = userData.get().getVacation();//availabilityDocument.getNetVacation();  // 44
            double sickHoursByMonth = userData.get().getSickdays(); //availabilityDocument.getNetSickdays(); // 15
            double maternityLeaveHoursByMonth = userData.get().getMaternityLeave(); //availabilityDocument.getNetMaternityLeave();
/*
            if(availableHoursByMonth < 0) {
                budgetHoursByMonth += availableHoursByMonth; // 58 + (-14) = 44
                availableHoursByMonth = 0; // 0
            }
            if(budgetHoursByMonth < 0) budgetHoursByMonth = 0;

 */

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