package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.services.RevenueService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ProfitsPerConsultantChart {

    private final RevenueService revenueService;

    private final UserService userService;

    @Autowired
    public ProfitsPerConsultantChart(RevenueService revenueService, UserService userService) {
        this.revenueService = revenueService;
        this.userService = userService;
    }

    public Chart createProfitsPerConsultantChart(User user) {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        LocalDate periodStart = userService.findByUUID(user.getUuid(), false).getStatuses().stream().min(Comparator.comparing(UserStatus::getStatusdate)).orElse(new UserStatus(ConsultantType.CONSULTANT, StatusType.ACTIVE, LocalDate.now(), 0)).getStatusdate();//LocalDate.of(2017, 07, 01);
        LocalDate periodEnd = LocalDate.now().withDayOfMonth(1);
        System.out.println("periodEnd = " + periodEnd);

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

        DataSeries revenueSeries = new DataSeries("Revenue");
        PlotOptionsAreaspline poc3 = new PlotOptionsAreaspline();
        poc3.setColor(new SolidColor("#123375"));
        revenueSeries.setPlotOptions(poc3);

        Map<LocalDate, Double> resultMap = revenueService.getRegisteredProfitsForSingleConsultant(user.getUuid(), periodStart, periodEnd, 3).stream().collect(Collectors.toMap(graphKeyValue -> DateUtils.dateIt(graphKeyValue.getDescription()), GraphKeyValue::getValue));
        for (LocalDate currentDate : resultMap.keySet().stream().sorted().collect(Collectors.toList())) {
            revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), resultMap.get(currentDate)));
            chart.getConfiguration().getxAxis().addCategory(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")));
        }

        chart.getConfiguration().addSeries(revenueSeries);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}