package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ConsultantsPerProjectChart {

    @Autowired
    private GraphKeyValueRepository graphKeyValueRepository;

    public Chart createConsultantsPerProjectChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setSizeFull();

        chart.setCaption("Number of Consultants per Project 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.countConsultantsPerProject(periodStart.toString("yyyyMMdd"), periodEnd.toString("yyyyMMdd"));
        double sumRevenue = 0.0;
        for (GraphKeyValue amountPerItem : amountPerItemList) {
            sumRevenue += amountPerItem.getValue();
        }
        double avgRevenue = sumRevenue / amountPerItemList.size();

        String[] categories = new String[amountPerItemList.size()];
        DataSeries revenueList = new DataSeries("Number of Consultants");

        int i = 0;
        for (GraphKeyValue amountPerItem : amountPerItemList) {
            revenueList.add(new DataSeriesItem(amountPerItem.getDescription(), amountPerItem.getValue()));
            StringBuilder shortname = new StringBuilder();
            for (String s : amountPerItem.getDescription().split(" ")) {
                shortname.append(s.charAt(0));
            }
            categories[i++] = shortname.toString();
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueList);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}
