package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class TopGrossingConsultantsChart {

    private final GraphKeyValueRepository graphKeyValueRepository;

    @Autowired
    public TopGrossingConsultantsChart(GraphKeyValueRepository graphKeyValueRepository) {
        this.graphKeyValueRepository = graphKeyValueRepository;
    }

    public Chart createTopGrossingConsultantsChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setSizeFull();

        chart.setCaption("Top Grossing Consultants Fiscal Year 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.findConsultantRevenueByPeriod(periodStart.format(DateTimeFormatter.ofPattern("yyyyMMdd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        double sumRevenue = 0.0;
        for (GraphKeyValue amountPerItem : amountPerItemList) {
            sumRevenue += amountPerItem.getValue();
        }
        double avgRevenue = sumRevenue / amountPerItemList.size();

        String[] categories = new String[amountPerItemList.size()];
        DataSeries revenueList = new DataSeries("Revenue");
        DataSeries avgRevenueList = new DataSeries("Average Revenue");
        PlotOptionsLine options2 = new PlotOptionsLine();
        options2.setColor(SolidColor.BLACK);
        options2.setMarker(new Marker(false));
        avgRevenueList.setPlotOptions(options2);

        int i = 0;
        for (GraphKeyValue amountPerItem : amountPerItemList) {
            revenueList.add(new DataSeriesItem(amountPerItem.getDescription(), amountPerItem.getValue()));
            avgRevenueList.add(new DataSeriesItem("Average revenue", avgRevenue));
            StringBuilder shortname = new StringBuilder();
            for (String s : amountPerItem.getDescription().split(" ")) {
                shortname.append(s.charAt(0));
            }
            categories[i++] = shortname.toString();
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueList);
        chart.getConfiguration().addSeries(avgRevenueList);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}
