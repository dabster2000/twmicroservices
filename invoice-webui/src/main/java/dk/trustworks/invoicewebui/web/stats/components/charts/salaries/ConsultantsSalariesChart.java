package dk.trustworks.invoicewebui.web.stats.components.charts.salaries;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ConsultantsSalariesChart {

    private final UserService userService;

    private final TeamRestService teamRestService;

    @Autowired
    public ConsultantsSalariesChart(UserService userService, TeamRestService teamRestService) {
        this.userService = userService;
        this.teamRestService = teamRestService;
    }

    public Chart createConsultantsSalariesChart(LocalDate periodStart, LocalDate periodEnd) {
        return createConsultantsSalariesChart(periodStart, periodEnd, null);
    }

    public Chart createConsultantsSalariesChart(LocalDate periodStart, LocalDate periodEnd, String... teamuuids) {
        Chart chart = new Chart();
        chart.setSizeFull();

        chart.setCaption("Team salaries");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<GraphKeyValue> salariesPerUser = new ArrayList<>();//graphKeyValueRepository.findConsultantRevenueByPeriod(periodStart.format(DateTimeFormatter.ofPattern("yyyyMMdd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        List<User> consultants = (teamuuids==null)?
                userService.getActiveConsultantsByFiscalYear(periodStart.getYear()):
                teamRestService.getUniqueUsersFromTeamsByFiscalYear(periodStart.getYear(), teamuuids);
        for (User user : consultants) {
            if(user.getSalaries().size()==0) user.setSalaries(userService.findUserSalaries(user.getUuid()));
            GraphKeyValue gkv = new GraphKeyValue(user.getUuid(), user.getInitials(), userService.getUserSalary(user, periodEnd));
            if(gkv.getValue()>0) salariesPerUser.add(gkv);
        }

        double sumSalaries = 0.0;
        for (GraphKeyValue amountPerItem : salariesPerUser) {
            sumSalaries += amountPerItem.getValue();
        }
        double avgRevenue = sumSalaries / salariesPerUser.size();

        String[] categories = new String[salariesPerUser.size()];

        DataSeries revenueList = new DataSeries("Salaries");
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#123375"));
        revenueList.setPlotOptions(poc3);

        DataSeries avgRevenueList = new DataSeries("Average Salary");
        PlotOptionsLine options2 = new PlotOptionsLine();
        options2.setColor(SolidColor.BLACK);
        options2.setMarker(new Marker(false));
        avgRevenueList.setPlotOptions(options2);

        int i = 0;
        for (GraphKeyValue amountPerItem : salariesPerUser.stream().sorted(Comparator.comparing(GraphKeyValue::getValue)).collect(Collectors.toList())) {
            revenueList.add(new DataSeriesItem(amountPerItem.getDescription(), amountPerItem.getValue()));
            avgRevenueList.add(new DataSeriesItem("Average Salary", avgRevenue));
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
