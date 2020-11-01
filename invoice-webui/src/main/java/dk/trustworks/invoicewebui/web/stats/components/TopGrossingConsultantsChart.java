package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.RevenueService;
import dk.trustworks.invoicewebui.services.StatisticsService;
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
public class TopGrossingConsultantsChart {

    private final StatisticsService statisticsService;

    private final RevenueService revenueService;

    private final UserService userService;

    @Autowired
    public TopGrossingConsultantsChart(StatisticsService statisticsService, RevenueService revenueService, UserService userService) {
        this.statisticsService = statisticsService;
        this.revenueService = revenueService;
        this.userService = userService;
    }

    public Chart createTopGrossingConsultantsChart(LocalDate periodStart, LocalDate periodEnd) {
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

        List<GraphKeyValue> amountPerItemList = new ArrayList<>();//graphKeyValueRepository.findConsultantRevenueByPeriod(periodStart.format(DateTimeFormatter.ofPattern("yyyyMMdd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        for (User user : userService.findAll(true)) {
            LocalDate currentDate = periodStart;
            GraphKeyValue gkv = new GraphKeyValue(user.getUuid(), user.getInitials(), 0);
            do {
                double revenue = revenueService.getRegisteredRevenueForSingleMonthAndSingleConsultant(user.getUuid(), currentDate);
                gkv.addValue((int) Math.round(revenue));
                currentDate = currentDate.plusMonths(1);
            } while (currentDate.isBefore(periodEnd.plusMonths(1)));
            if(gkv.getValue()>0) amountPerItemList.add(gkv);
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
