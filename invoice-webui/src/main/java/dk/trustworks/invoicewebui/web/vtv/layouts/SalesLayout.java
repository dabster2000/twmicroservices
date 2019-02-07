package dk.trustworks.invoicewebui.web.vtv.layouts;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.resourceplanning.components.Card;
import dk.trustworks.invoicewebui.web.resourceplanning.components.SalesHeatMap;
import dk.trustworks.invoicewebui.web.stats.components.ConsultantsBudgetRealizationChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class SalesLayout extends VerticalLayout {

    @Autowired
    private SalesHeatMap salesHeatMap;

    @Autowired
    private ConsultantsBudgetRealizationChart consultantsBudgetRealizationChart;

    public SalesLayout() {
    }

    @Transactional
    public SalesLayout init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        ResponsiveRow row = responsiveLayout.addRow();

        LocalDate localDateStart = LocalDate.now().withDayOfMonth(1);
        LocalDate localDateEnd = LocalDate.now().withDayOfMonth(1).plusMonths(11);

        Card salesViewCard = new Card();
        salesViewCard.getCardHolder().addComponent(salesHeatMap.getSalesOverview());

        Card consultantsBudgetRealizationCard = new Card();
        consultantsBudgetRealizationCard.getCardHolder().addComponent(consultantsBudgetRealizationChart.createConsultantsBudgetRealizationChart());

        row.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(consultantsBudgetRealizationCard);

        row.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(salesViewCard);

        this.addComponent(responsiveLayout);

        return this;
    }
}
