package dk.trustworks.invoicewebui.web.vtv.layouts;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.TaskOffering;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.TaskOfferingRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.web.resourceplanning.components.Card;
import dk.trustworks.invoicewebui.web.resourceplanning.components.SalesHeatMap;
import dk.trustworks.invoicewebui.web.stats.components.ConsultantsBudgetRealizationChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hans on 21/12/2016.
 */
@SpringComponent
@SpringUI
public class SalesLayout extends VerticalLayout {

    @Autowired
    private TaskOfferingRepository taskOfferingRepository;

    @Autowired
    private WorkRepository workRepository;

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

        Card offeringCard = new Card();
        offeringCard.getCardHolder().addComponent(createOfferingChart(2018));

        row.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(consultantsBudgetRealizationCard);

        row.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(salesViewCard);

        row.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(offeringCard);

        this.addComponent(responsiveLayout);

        return this;
    }

    private Chart createOfferingChart(int year) {
        Chart chart = new Chart(ChartType.COLUMN);

        Configuration conf = chart.getConfiguration();

        conf.setTitle("Offerings billable hours");

        List<Work> workList = workRepository.findBillableWorkByPeriod(
                LocalDate.of(year, 1, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                LocalDate.of(year, 12, 31).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );

        Map<String, Double> hoursByOfferingsMap = new HashMap<>();

        for (Work work : workList) {
            for (TaskOffering taskOffering : work.getTask().getTaskOfferings()) {
                hoursByOfferingsMap.putIfAbsent(taskOffering.getName(), 0.0);
                double hours = hoursByOfferingsMap.get(taskOffering.getName());
                hours += work.getWorkduration();
                hoursByOfferingsMap.replace(taskOffering.getName(), hours);
            }
        }


        XAxis x = new XAxis();
        x.setCategories(hoursByOfferingsMap.keySet().toArray(new String[0]));
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Hours");
        conf.addyAxis(y);

        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setBackgroundColor(new SolidColor("#FFFFFF"));
        legend.setAlign(HorizontalAlign.LEFT);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(100);
        legend.setY(70);
        legend.setFloating(true);
        legend.setShadow(true);
        conf.setLegend(legend);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.x +': '+ this.y +' hours'");
        conf.setTooltip(tooltip);

        PlotOptionsColumn plot = new PlotOptionsColumn();
        plot.setPointPadding(0.2);
        plot.setBorderWidth(0);

        conf.addSeries(new ListSeries(hoursByOfferingsMap.values().stream().map(aDouble -> (Number) aDouble).collect(Collectors.toList())));

        chart.drawChart(conf);
        return chart;
    }
}
