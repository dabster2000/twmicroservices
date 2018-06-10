package dk.trustworks.invoicewebui.web.bubbles.components;

import com.vaadin.addon.charts.Sparkline;
import com.vaadin.ui.Component;

public class ActivityGauge {

    public static Component getChart(Number... activity) {
        return new Sparkline(100, 28, activity);
    }
}
