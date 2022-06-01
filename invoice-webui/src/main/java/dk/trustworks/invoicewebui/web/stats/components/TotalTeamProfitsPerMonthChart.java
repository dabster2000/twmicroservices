package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.services.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class TotalTeamProfitsPerMonthChart {

    private final UserService userService;

    private final RevenueService revenueService;

    @Autowired
    public TotalTeamProfitsPerMonthChart(UserService userService, RevenueService revenueService) {
        this.userService = userService;
        this.revenueService = revenueService;
    }

    public Chart createCumulativeRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setSizeFull();
        int period = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);

        chart.setCaption("Cumulative Revenue during Fiscal Year 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);



        revenueService.getRegisteredRevenueByPeriodAndSingleConsultant(null, null, null);


        String[] categories = new String[period];
        DataSeries earningsSeries = new DataSeries("Earnings");

        DataSeries avgRevenueList = new DataSeries("Projected Revenue");
        PlotOptionsLine options2 = new PlotOptionsLine();
        options2.setColor(SolidColor.BLACK);
        options2.setMarker(new Marker(false));
        avgRevenueList.setPlotOptions(options2);

        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(earningsSeries);
        chart.getConfiguration().addSeries(avgRevenueList);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}
