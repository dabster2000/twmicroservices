package dk.trustworks.invoicewebui.web.stats.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.stats.model.FiscalPeriod;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hans on 20/09/2017.
 */
@SpringComponent
@SpringUI
public class TrustworksStatsLayout extends VerticalLayout {

    @Autowired
    private RevenuePerMonthChart revenuePerMonthChart;

    @Autowired
    private CumulativeRevenuePerMonthChart cumulativeRevenuePerMonthChart;

    @Autowired
    private TopGrossingConsultantsChart topGrossingConsultantsChart;

    @Autowired
    private ConsultantHoursPerMonthChart consultantHoursPerMonthChart;

    @Autowired
    private RevenuePerMonthEmployeeAvgChart revenuePerMonthEmployeeAvgChart;

    @Autowired
    private CumulativePredictiveRevenuePerMonthChart cumulativePredictiveRevenuePerMonthChart;

    @Autowired
    private CumulativePredictiveRevenuePerYearChart cumulativePredictiveRevenuePerYearChart;


    public TrustworksStatsLayout init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        ResponsiveRow searchRow = responsiveLayout.addRow();
        final ResponsiveRow chartRow = responsiveLayout.addRow();

        LocalDate startFiscalPeriod = new LocalDate(2014, 7, 1);
        ComboBox<FiscalPeriod> fiscalPeriodComboBox = new ComboBox<>();
        LocalDate currentFiscalYear = (LocalDate.now().getMonthOfYear()>6 && LocalDate.now().getMonthOfYear()<13)?LocalDate.now().withMonthOfYear(7).withDayOfMonth(1):LocalDate.now().minusYears(1).withMonthOfYear(7).withDayOfMonth(1);
        List<FiscalPeriod> fiscalPeriodList = new ArrayList<>();
        while(startFiscalPeriod.isBefore(currentFiscalYear) || startFiscalPeriod.isEqual(currentFiscalYear)) {
            System.out.println("startFiscalPeriod = " + startFiscalPeriod);
            System.out.println("startFiscalPeriod.plusYears(1) = " + startFiscalPeriod.plusYears(1));
            fiscalPeriodList.add(new FiscalPeriod(startFiscalPeriod, startFiscalPeriod.plusYears(1)));
            startFiscalPeriod = startFiscalPeriod.plusYears(1);
        }
        fiscalPeriodComboBox.setItems(fiscalPeriodList);
        fiscalPeriodComboBox.setItemCaptionGenerator(FiscalPeriod::getName);
        fiscalPeriodComboBox.addValueChangeListener(event -> {
            chartRow.removeAllComponents();
            createCharts(chartRow, event.getValue().getFromDate(), event.getValue().getToDate());
        });

        searchRow.addColumn().withComponent(fiscalPeriodComboBox);

        int adjustStartYear = 0;
        if(LocalDate.now().getMonthOfYear() >= 1 && LocalDate.now().getMonthOfYear() <=6)  adjustStartYear = 1;
        LocalDate localDateStart = LocalDate.now().withMonthOfYear(7).withDayOfMonth(1).minusYears(adjustStartYear);
        LocalDate localDateEnd = localDateStart.plusYears(1);

        createCharts(chartRow, localDateStart, localDateEnd);

        this.addComponent(responsiveLayout);

        return this;
    }

    private void createCharts(ResponsiveRow chartRow, LocalDate localDateStart, LocalDate localDateEnd) {
        Card revenuePerMonthCard = new Card();
        revenuePerMonthCard.getLblTitle().setValue("Revenue Per Month");
        revenuePerMonthCard.getContent().addComponent(revenuePerMonthChart.createRevenuePerMonthChart(localDateStart, localDateEnd));

        Card cumulativeRevenuePerMonthCard = new Card();
        cumulativeRevenuePerMonthCard.getLblTitle().setValue("Cumulative Revenue Per Month");
        cumulativeRevenuePerMonthCard.getContent().addComponent(cumulativeRevenuePerMonthChart.createCumulativeRevenuePerMonthChart(localDateStart, localDateEnd));

        Card consultantGrossingCard = new Card();
        consultantGrossingCard.getLblTitle().setValue("Top Grossing Consultants");
        consultantGrossingCard.getContent().addComponent(topGrossingConsultantsChart.createTopGrossingConsultantsChart(localDateStart, localDateEnd));

        Card consultantHoursPerMonth = new Card();
        consultantHoursPerMonth.getLblTitle().setValue("Consultant Hours Per Month");
        consultantHoursPerMonth.getContent().addComponent(consultantHoursPerMonthChart.createTopGrossingConsultantsChart(localDateStart, localDateEnd));

        Card cumulativePredictiveRevenuePerMonthCard = new Card();
        cumulativePredictiveRevenuePerMonthCard.getLblTitle().setValue("Cumulative Predicted Revenue");
        cumulativePredictiveRevenuePerMonthCard.getContent().addComponent(cumulativePredictiveRevenuePerMonthChart.createCumulativePredictiveRevenuePerMonthChart());

        Card cumulativePredictiveRevenuePerYearCard = new Card();
        cumulativePredictiveRevenuePerYearCard.getLblTitle().setValue("Cumulative Predicted Revenue");
        cumulativePredictiveRevenuePerYearCard.getContent().addComponent(cumulativePredictiveRevenuePerYearChart.createCumulativePredictiveRevenuePerYearChart());

        Card revenuePerMonthEmployeeAvgCard = new Card();
        revenuePerMonthEmployeeAvgCard.getLblTitle().setValue("Average Revenue per Consultant");
        revenuePerMonthEmployeeAvgCard.getContent().addComponent(revenuePerMonthEmployeeAvgChart.createRevenuePerMonthChart(localDateStart, localDateEnd));

        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(revenuePerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(cumulativeRevenuePerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(consultantGrossingCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(revenuePerMonthEmployeeAvgCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(cumulativePredictiveRevenuePerYearCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(cumulativePredictiveRevenuePerMonthCard);
        chartRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(consultantHoursPerMonth);
    }

}
