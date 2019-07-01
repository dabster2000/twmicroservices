package dk.trustworks.invoicewebui.web.resourceplanning.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class ResourcePlanningLayout extends VerticalLayout {

    @Autowired
    private SalesHeatMap salesHeatMap;

    public ResourcePlanningLayout() {
    }

    @Transactional
    public ResourcePlanningLayout init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        ResponsiveRow row = responsiveLayout.addRow();

        LocalDate localDateStart = LocalDate.now().withDayOfMonth(1);
        LocalDate localDateEnd = LocalDate.now().withDayOfMonth(1).plusMonths(11);

        localDateStart = LocalDate.of(2018,7,1).withDayOfMonth(1);
        localDateEnd = localDateStart.plusMonths(11);

        Card heatMapCard = new Card();
        heatMapCard.getCardHolder().addComponent(salesHeatMap.getChart(localDateStart, localDateEnd));

        Card availabilityChartCard = new Card();
        availabilityChartCard.getCardHolder().addComponent(salesHeatMap.getAvailabilityChart(localDateStart, localDateEnd));

        row.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(heatMapCard);
        row.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(availabilityChartCard);

        this.addComponent(responsiveLayout);

        return this;
    }
}
