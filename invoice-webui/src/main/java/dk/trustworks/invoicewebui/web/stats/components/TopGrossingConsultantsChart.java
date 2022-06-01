package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.EmployeeAggregateData;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.BiService;
import dk.trustworks.invoicewebui.services.UserService;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@JBossLog
@SpringComponent
@SpringUI
public class TopGrossingConsultantsChart {

    private final BiService biService;

    private final UserService userService;

    private final TeamRestService teamRestService;

    @Autowired
    public TopGrossingConsultantsChart(UserService userService, BiService biService, TeamRestService teamRestService) {
        this.biService = biService;
        //this.revenueService = revenueService;
        this.userService = userService;
        this.teamRestService = teamRestService;
    }

    public Chart createTopGrossingConsultantsChart(LocalDate periodStart, LocalDate periodEnd) {
        return createTopGrossingConsultantsChart(periodStart, periodEnd, null);
    }

    public Chart createTopGrossingConsultantsChart(LocalDate periodStart, LocalDate periodEnd, String... teamuuids) {
        Chart chart = new Chart();
        chart.setSizeFull();

        chart.setCaption("Total Revenue");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<GraphKeyValue> amountPerItemList = new ArrayList<>();

        List<User> consultants = (teamuuids==null)?
                userService.getActiveConsultantsByFiscalYear(periodStart.getYear()):
                teamRestService.getUniqueUsersFromTeamsByFiscalYear(periodStart.getYear(), teamuuids);

        for (User user : consultants) {
            double sum = biService.getEmployeeAggregateDataByPeriod(periodStart, periodEnd).stream().filter(e -> e.getUseruuid().equals(user.getUuid())).mapToDouble(EmployeeAggregateData::getRegisteredAmount).sum();//revenueService.getRegisteredRevenueByPeriodAndSingleConsultant(user.getUuid(), periodStart, periodEnd).values().stream().mapToDouble(Double::doubleValue).sum();
            GraphKeyValue gkv = new GraphKeyValue(user.getUuid(), user.getInitials(), sum);
            amountPerItemList.add(gkv);
        }

        double sumRevenue = 0.0;
        for (GraphKeyValue amountPerItem : amountPerItemList) {
            sumRevenue += amountPerItem.getValue();
        }
        double avgRevenue = sumRevenue / amountPerItemList.size();

        String[] categories = new String[amountPerItemList.size()];

        DataSeries revenueList = new DataSeries("Revenue");
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#123375"));
        revenueList.setPlotOptions(poc3);

        DataSeries avgRevenueList = new DataSeries("Average Revenue");
        PlotOptionsLine options2 = new PlotOptionsLine();
        options2.setColor(SolidColor.BLACK);
        options2.setMarker(new Marker(false));
        avgRevenueList.setPlotOptions(options2);

        int i = 0;
        for (GraphKeyValue amountPerItem : amountPerItemList.stream().sorted(Comparator.comparing(GraphKeyValue::getValue)).collect(Collectors.toList())) {
            revenueList.add(new DataSeriesItem(amountPerItem.getDescription(), amountPerItem.getValue()));
            avgRevenueList.add(new DataSeriesItem("Average revenue", avgRevenue));
            categories[i++] = amountPerItem.getDescription();
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueList);
        chart.getConfiguration().addSeries(avgRevenueList);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}
