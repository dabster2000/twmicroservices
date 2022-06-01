package dk.trustworks.invoicewebui.web.stats.components.charts.size;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class TeamConsultantCountChart {

    private final UserService userService;

    private final TeamRestService teamRestService;

    @Autowired
    public TeamConsultantCountChart(UserService userService, TeamRestService teamRestService) {
        this.userService = userService;
        this.teamRestService = teamRestService;
    }

    public Chart createTeamConsultantCountChart(int fiscalYear) {
        return createTeamConsultantCountChart(fiscalYear, null);
    }

    public Chart createTeamConsultantCountChart(int intFiscalYear, String... teamuuids) {
        Chart chart = new Chart();
        chart.setSizeFull();

        LocalDate fiscalYear = LocalDate.of(intFiscalYear, 7, 1);

        chart.setCaption("Team count");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<GraphKeyValue> consultantCountPerMonth = new ArrayList<>();

        for (int i = 0; i < 11; i++) {
            LocalDate date = fiscalYear.plusMonths(i);
            int count = 0;
            if (teamuuids == null)
                count = userService.findWorkingUsersByDate(fiscalYear, false, ConsultantType.CONSULTANT).size();
            else {
                for (String teamuuid : teamuuids) {
                    count += teamRestService.getUsersByTeamByMonth(teamuuid, date).size();
                }
            }

            GraphKeyValue gkv = new GraphKeyValue(UUID.randomUUID().toString(), stringIt(date), count);
            consultantCountPerMonth.add(gkv);
        }

        String[] categories = new String[consultantCountPerMonth.size()];

        DataSeries consultantCountList = new DataSeries("Consultant count");
        PlotOptionsAreaspline poc3 = new PlotOptionsAreaspline();
        poc3.setColor(new SolidColor("#123375"));
        consultantCountList.setPlotOptions(poc3);

        int i = 0;
        for (GraphKeyValue amountPerItem : consultantCountPerMonth.stream().sorted(Comparator.comparing(GraphKeyValue::getDescription)).collect(Collectors.toList())) {
            consultantCountList.add(new DataSeriesItem(amountPerItem.getDescription(), amountPerItem.getValue()));
            categories[i++] = amountPerItem.getDescription();
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(consultantCountList);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}
