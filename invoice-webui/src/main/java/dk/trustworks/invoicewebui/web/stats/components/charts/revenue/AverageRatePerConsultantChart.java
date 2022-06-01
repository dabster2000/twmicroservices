package dk.trustworks.invoicewebui.web.stats.components.charts.revenue;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class AverageRatePerConsultantChart {

    private final UserService userService;

    private final TeamRestService teamRestService;

    @Autowired
    public AverageRatePerConsultantChart(UserService userService, TeamRestService teamRestService) {
        this.userService = userService;
        this.teamRestService = teamRestService;
    }

    public Chart createAverageRatePerConsultantChart(int fiscalYear) {
        return createAverageRatePerConsultantChart(fiscalYear, null);
    }

    public Chart createAverageRatePerConsultantChart(int intFiscalYear, String... teamuuids) {
        Chart chart = new Chart();
        chart.setSizeFull();

        LocalDate fiscalYear = LocalDate.of(intFiscalYear, 7, 1);

        chart.setCaption("Average rate per consultant");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0) +' kr'");
        chart.getConfiguration().setTooltip(tooltip);

        List<GraphKeyValue> consultantAverageRate = new ArrayList<>();

        List<User> consultants = (teamuuids==null)?
                userService.findWorkingUsersByDate(fiscalYear, false, ConsultantType.CONSULTANT):
                teamRestService.getUniqueUsersFromTeamsByFiscalYear(fiscalYear.getYear(), teamuuids);

        for (User consultant : consultants) {
            consultantAverageRate.add(userService.calculateAverageRatePerFiscalYear(consultant.getUuid(), intFiscalYear));
        }

        String[] categories = new String[consultantAverageRate.size()];

        DataSeries consultantCountList = new DataSeries("Consultant average rate");
        PlotOptionsColumn poc3 = new PlotOptionsColumn();
        poc3.setColor(new SolidColor("#123375"));
        consultantCountList.setPlotOptions(poc3);

        int i = 0;
        for (GraphKeyValue amountPerItem : consultantAverageRate.stream().sorted(Comparator.comparing(GraphKeyValue::getDescription)).collect(Collectors.toList())) {
            consultantCountList.add(new DataSeriesItem(amountPerItem.getDescription(), NumberUtils.round(amountPerItem.getValue(), 0)));
            categories[i++] = amountPerItem.getDescription();
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(consultantCountList);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}
