package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ConsultantHoursPerMonthChart {


    private final ContractService contractService;

    private final UserService userRepository;

    @Autowired
    public ConsultantHoursPerMonthChart(ContractService contractService, UserService userRepository) {
        this.contractService = contractService;
        this.userRepository = userRepository;
    }
/*
    public Chart createTopGrossingConsultantsChart(LocalDate periodStart, LocalDate periodEnd) {
        System.out.println("ConsultantHoursPerMonthChart.createTopGrossingConsultantsChart");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
        int period = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);
        Chart chart = new Chart();
        chart.setSizeFull();

        chart.setCaption("Consultants Below 100 Hours 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.BAR);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getxAxis().setTitle("");
        chart.getConfiguration().getyAxis().setTitle("hours");
        chart.getConfiguration().getLegend().setEnabled(true);

        String[] categories = new String[period+1];
        LocalDate iteratorDate = periodStart;
        for (int i = 0; i < categories.length; i++) {
            categories[i] = iteratorDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
            iteratorDate = iteratorDate.plusMonths(1);
        }

        PlotOptionsSeries plot = new PlotOptionsSeries();
        plot.setStacking(Stacking.NORMAL);
        chart.getConfiguration().setPlotOptions(plot);


        Map<String, double[]> userMonths = new HashMap<>();
        List<Work> workList = contractService.findBillableWorkByPeriod(periodStart, periodEnd);

        for (Work work : workList) {
            if(!userMonths.containsKey(work.getUser().getUuid())) userMonths.put(work.getUser().getUuid(), new double[period+1]);
            userMonths.get(work.getUser().getUuid())[Period.between(periodStart, work.getRegistered()).getMonths()] += work.getWorkduration();
        }

        for (String useruuid : userMonths.keySet()) {
            User user = userRepository.findByUUID(useruuid);
            chart.getConfiguration().addSeries(new ListSeries(user.getFirstname()+" "+user.getLastname(), Arrays.asList(ArrayUtils.toObject(userMonths.get(useruuid)))));
        }
        chart.getConfiguration().getxAxis().setCategories(categories);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

 */

}
