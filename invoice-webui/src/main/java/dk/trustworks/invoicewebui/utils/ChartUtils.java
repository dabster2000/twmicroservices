package dk.trustworks.invoicewebui.utils;


import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsArea;
import com.vaadin.addon.charts.model.PlotOptionsAreaspline;
import com.vaadin.addon.charts.model.style.SolidColor;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ChartUtils {
    public static DataSeries createDataSeries(Map<LocalDate, Double> map, String name, String color) {
        DataSeries dataSeries = new DataSeries(name);
        PlotOptionsAreaspline plotOptionsArea = new PlotOptionsAreaspline();
        plotOptionsArea.setColor(new SolidColor(color));
        dataSeries.setPlotOptions(plotOptionsArea);



        for (LocalDate localDate : map.keySet().stream().sorted(LocalDate::compareTo).collect(Collectors.toList())) {
            dataSeries.add(new DataSeriesItem(localDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), map.get(localDate)));
        }
        return dataSeries;
    }
}
