package dk.trustworks.invoicewebui.web.stats.components;

/**
 * Created by hans on 20/09/2017.
 */

//@SpringComponent
//@SpringUI
public class TopGrossingProjectsChart {
/*
    @Autowired
    private GraphKeyValueRepository graphKeyValueRepository;

    public Chart createTopGrossingProjectsChart(LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        //chart.setWidth("100%");  // 100% by default
        //chart.setHeight("280px"); // 400px by default
        chart.setSizeFull();

        chart.setCaption("Top Grossing Projects Fiscal Year 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.COLUMN);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.findProjectRevenueByPeriod(periodStart.stringIt("yyyyMMdd"), periodEnd.stringIt("yyyyMMdd"));
        System.out.println("amountPerItemList.size() = " + amountPerItemList.size());
        String[] categories = new String[amountPerItemList.size()];
        DataSeries listSeries = new DataSeries("Revenue");
        int i = 0;
        double sumOfRemainingProjects = 0.0;
        for (GraphKeyValue amountPerItem : amountPerItemList) {
            if(i<10) {
                listSeries.add(new DataSeriesItem(amountPerItem.getDescription(), amountPerItem.getValue()));
                StringBuilder shortname = new StringBuilder();
                String[] s = amountPerItem.getDescription().split(" ");
                //for (String s : amountPerItem.description.split(" ")) {
                int subLength = s[0].length()<3?s[0].length():3;
                shortname.append(s[0], 0, subLength);
                //}
                categories[i] = shortname.stringIt();
            } else {
                sumOfRemainingProjects += amountPerItem.getValue();
                categories[10] = "Rest";
            }

            i++;
        }
        listSeries.add(new DataSeriesItem("Remaining projects", sumOfRemainingProjects));
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(listSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
*/
}
