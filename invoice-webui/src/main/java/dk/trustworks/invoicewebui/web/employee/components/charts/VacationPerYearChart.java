package dk.trustworks.invoicewebui.web.employee.components.charts;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import dk.trustworks.invoicewebui.homeauto.model.Person;
import dk.trustworks.invoicewebui.model.ExpenseDetails;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.ExpenseDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.network.clients.EconomicsAPI;
import dk.trustworks.invoicewebui.network.clients.VacationAPI;
import dk.trustworks.invoicewebui.network.clients.model.vacation.VacationPeriod;
import dk.trustworks.invoicewebui.repositories.ExpenseDetailsRepository;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.services.WorkService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.apache.commons.lang3.Range;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class VacationPerYearChart {

    @Autowired
    private VacationAPI vacationAPI;

    @Autowired
    private UserService userService;

    @Autowired
    private WorkService workService;

    public Chart createExpensePerMonthChart(User user) {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Vacation Per Year");
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
        tooltip.setFormatter("this.series.name +': '+ this.y +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        Map<String, Range<Integer>> listSeriesRangeMap = new HashMap<>();

        PlotOptionsColumn poc4 = new PlotOptionsColumn();
        poc4.setColor(new SolidColor("#54D69E"));
        ListSeries hoursUsedSeries = new ListSeries("Used vacation");
        hoursUsedSeries.setId(UUID.randomUUID().toString());
        hoursUsedSeries.setPlotOptions(poc4);

        ListSeries hoursLeftSeries = new ListSeries("Unused vacation");
        hoursLeftSeries.setId(UUID.randomUUID().toString());
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#98E6C4"));
        hoursLeftSeries.setPlotOptions(poc3);

        List<VacationPeriod> vacationPeriods = vacationAPI.getVacationPeriods(userService.findByUsername("emilie.duedahl"), workService.findVacationByUser(userService.findByUsername("emilie.duedahl")));

        String[] monthNames = vacationPeriods.stream().map(VacationPeriod::getFrom).sorted().toArray(String[]::new);

        for (VacationPeriod vacationPeriod : vacationPeriods) {

            hoursUsedSeries.addData(vacationPeriod.getHoursUsed());
            hoursLeftSeries.addData(vacationPeriod.getHoursLeft());

        }

        chart.getConfiguration().getxAxis().setCategories(monthNames);
        chart.getConfiguration().addSeries(hoursUsedSeries);
        chart.getConfiguration().addSeries(hoursLeftSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }


}