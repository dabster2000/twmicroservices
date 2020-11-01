package dk.trustworks.invoicewebui.utils;


import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsAreaspline;
import com.vaadin.addon.charts.model.style.SolidColor;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@UtilityClass
public class ChartUtils {
    public static DataSeries createDataSeries(List<GraphKeyValue> data, String name, String color) {
        DataSeries dataSeries = new DataSeries(name);
        PlotOptionsAreaspline plotOptionsArea = new PlotOptionsAreaspline();
        plotOptionsArea.setColor(new SolidColor(color));
        dataSeries.setPlotOptions(plotOptionsArea);

        data.stream().sorted(Comparator.comparing(o -> DateUtils.dateIt(o.getDescription()))).forEach(graphKeyValue -> {
            dataSeries.add(new DataSeriesItem(DateUtils.dateIt(graphKeyValue.getDescription()).format(DateTimeFormatter.ofPattern("MMM-yyyy")), graphKeyValue.getValue()));
        });
        return dataSeries;
    }
}
