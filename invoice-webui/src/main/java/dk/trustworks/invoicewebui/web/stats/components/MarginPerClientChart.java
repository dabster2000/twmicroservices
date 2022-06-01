package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.network.dto.ClientMarginResult;
import dk.trustworks.invoicewebui.services.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class MarginPerClientChart {

    @Autowired
    private MarginService marginService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private RevenueService revenueService;

    public MarginPerClientChart() {
    }

    public Chart createMarginPerClientChart(int fiscalYear) {
        Chart chart = new Chart(ChartType.SCATTER);
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Margin and revenue per customer");
        Configuration conf = chart.getConfiguration();
        conf.setTitle("");
        //chart.getConfiguration().getChart().setType(ChartType.BUBBLE);
        conf.getChart().setAnimation(true);
        conf.getyAxis().setTitle("");
        conf.getLegend().setEnabled(false);
        conf.getChart().setZoomType(ZoomType.XY);



        //PlotOptionsBubble opts = new PlotOptionsBubble();
        //opts.setMaxSize("120");
        //opts.setMinSize("3");
        //chart.getConfiguration().setPlotOptions(opts);

        XAxis xAxis = conf.getxAxis();
        xAxis.setStartOnTick(true);
        xAxis.setEndOnTick(true);
        xAxis.setShowLastLabel(true);
        xAxis.setMin(0);
        xAxis.setTitle("Fsical Budget (kr)");

        YAxis yAxis = conf.getyAxis();
        yAxis.setMax(500);
        yAxis.setTickPixelInterval(50);
        yAxis.setTitle("Margin (kr)");

        List<ClientMarginResult> clientMarginResult = marginService.calculateMarginPerCustomer(fiscalYear);
        List<GraphKeyValue> clientFiscalBudgetSums = clientService.findClientFiscalBudgetSums(fiscalYear);
        //List<GraphKeyValue> revenueByClientList = revenueService.getSumOfRegisteredRevenueByClientByYear(fiscalYear);

        for (Client client : clientService.findAll()) {
            if(client.getName().contains("Trustworks")) continue;
            PlotOptionsScatter plotOptionsScatter = new PlotOptionsScatter();
            DataSeries scatter = new DataSeries(client.getName());
            String scatterChartId = UUID.randomUUID().toString();
            scatter.setId(scatterChartId);
            scatter.setPlotOptions(plotOptionsScatter);

            OptionalDouble clientMargin = clientMarginResult.stream().filter(c -> c.getClientuuid().equals(client.getUuid()) && c.getMarginResult() != null && c.getMarginResult().getMargin() != 0).mapToInt(c -> c.getMarginResult().getMargin()).average();
            Optional<GraphKeyValue> clientBudget = clientFiscalBudgetSums.stream().filter(c -> c.getUuid().equals(client.getUuid())).findFirst();
            //Optional<GraphKeyValue> clientRevenue = revenueByClientList.stream().filter(r -> r.getUuid().equals(client.getUuid())).findFirst();
            if(!clientMargin.isPresent()) continue;
            scatter.add(item(client.getName(), (int) clientBudget.orElse(new GraphKeyValue(client.getUuid(),client.getName(),0.0)).getValue(), (int) clientMargin.getAsDouble())); // , (int) clientBudget.orElse(new GraphKeyValue(client.getUuid(),client.getName(),0.0)).getValue())
            conf.addSeries(scatter);

            DataSeries flagsOnSeries = new DataSeries();
            flagsOnSeries.setName("");
            PlotOptionsFlags plotOptionsFlags = new PlotOptionsFlags();
            plotOptionsFlags.setOnSeries(scatterChartId);
            flagsOnSeries.setPlotOptions(plotOptionsFlags);

            flagsOnSeries.add(new FlagItem(scatter.get(0).getX(),
                    client.getName(), "Margin: "+((int) clientMargin.getAsDouble())));
            conf.addSeries(flagsOnSeries);
        }

        /*
        YAxis yAxis = new YAxis();
        yAxis.setMin(0);
        chart.getConfiguration().getyAxis().setTitle("");
        StackLabels sLabels = new StackLabels(true);
        yAxis.setStackLabels(sLabels);
        chart.getConfiguration().addyAxis(yAxis);
         */
/*
        Tooltip tooltip = new Tooltip();
        tooltip.setShared(true);
        tooltip.setUseHTML(true);
        tooltip.setHeaderFormat("<small>{point.key}</small><table>");
        tooltip.setPointFormat("<tr><td>Margin: </td><td style=\"text-align: right\"><b>{Highcharts.numberFormat(point.x/1000, 0)} TKR</b></td></tr>");
        tooltip.setFooterFormat("</table>");
        conf.setTooltip(tooltip);
*/

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': Margin = '+ Highcharts.numberFormat(this.y, 0) +' kr, Fiscal Budget = '+ Highcharts.numberFormat(this.x/1000, 0) +' tkr'");
        conf.setTooltip(tooltip);




        Credits c = new Credits("");
        conf.setCredits(c);
        return chart;
    }

    public DataSeriesItem item(String name, int x, int y) {
        DataSeriesItem dataSeriesItem = new DataSeriesItem(name, y);
        dataSeriesItem.setX(x);
        dataSeriesItem.setY(y);
        return dataSeriesItem;
    }
}