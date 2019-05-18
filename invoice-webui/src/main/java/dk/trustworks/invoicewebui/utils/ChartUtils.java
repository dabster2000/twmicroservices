package dk.trustworks.invoicewebui.utils;


import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ChartUtils {
    public static DataSeries createDataSeries(Map<LocalDate, Double> map, String name) {
        DataSeries dataSeries = new DataSeries(name);

        for (LocalDate localDate : map.keySet().stream().sorted(LocalDate::compareTo).collect(Collectors.toList())) {
            dataSeries.add(new DataSeriesItem(localDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), map.get(localDate)));
        }
        return dataSeries;
    }
}
