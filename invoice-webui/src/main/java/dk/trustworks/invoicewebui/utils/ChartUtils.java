package dk.trustworks.invoicewebui.utils;


import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@UtilityClass
public class ChartUtils {
    public static DataSeries createDataSeries(Map<LocalDate, Double> map) {
        DataSeries dataSeries = new DataSeries();

        for (LocalDate localDate : map.keySet()) {
            dataSeries.add(new DataSeriesItem(localDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), map.get(localDate)));
        }
        return dataSeries;
    }
}
