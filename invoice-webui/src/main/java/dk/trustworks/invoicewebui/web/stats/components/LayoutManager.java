package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.ui.VerticalLayout;


/**
 * Created by hans on 20/09/2017.
 */
//@SpringComponent
//@SpringUI
public class LayoutManager extends VerticalLayout {
/*
    @Autowired
    private TopGrossingProjectsChart topGrossingProjectsChart;

    @Autowired
    private ConsultantsPerProjectChart consultantsPerProjectChart;

    public LayoutManager init() {
        this.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        ResponsiveRow row = responsiveLayout.addRow();

        LocalDate localDateStart = LocalDate.now().withMonthOfYear(7).withDayOfMonth(1).minusYears(1);
        LocalDate localDateEnd = localDateStart.plusYears(1);

        Card projectGrossingCard = new Card();
        projectGrossingCard.getLblTitle().setValue("Top Grossing Projects");
        projectGrossingCard.getContent().addComponent(topGrossingProjectsChart.createTopGrossingProjectsChart(localDateStart, localDateEnd));

        Card consultantsPerProjectCard = new Card();
        consultantsPerProjectCard.getLblTitle().setValue("Number of Consultants per Project");
        consultantsPerProjectCard.getContent().addComponent(consultantsPerProjectChart.createConsultantsPerProjectChart(localDateStart, localDateEnd));

        row.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(projectGrossingCard);
        row.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(consultantsPerProjectCard);

        this.addComponent(responsiveLayout);

        return this;
    }
*/
}
