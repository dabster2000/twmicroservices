package dk.trustworks.invoicewebui.web.employee.components.charts;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class BillableConsultantHoursPerMonthChart {

    private final GraphKeyValueRepository graphKeyValueRepository;

    @Autowired
    public BillableConsultantHoursPerMonthChart(GraphKeyValueRepository graphKeyValueRepository) {
        this.graphKeyValueRepository = graphKeyValueRepository;
    }

    @Cacheable("revenueChart")
    public Chart createBillableConsultantHoursPerMonthChart(User user, LocalDate periodStart, LocalDate periodEnd) {
        System.out.println("ConsultantHoursPerMonthChart.createBillableConsultantHoursPerMonthChart");
        System.out.println("user = [" + user + "], periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);
        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);

        chart.setCaption("Hours during Fiscal Year 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0) +' hours'");
        chart.getConfiguration().setTooltip(tooltip);

        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.findBillableHoursByMonthByPeriod(user.getUuid(), periodStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        String[] categories = new String[months];
        DataSeries revenueSeries = new DataSeries("Hours");
        amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")))).collect(Collectors.toList());
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            if(amountPerItemList.size() > i) {
                GraphKeyValue amountPerItem = amountPerItemList.get(i);
                if(amountPerItem!=null) revenueSeries.add(new DataSeriesItem(LocalDate.parse(amountPerItem.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")).format(DateTimeFormatter.ofPattern("MMM-yyyy")), amountPerItem.getValue()));
            }
            categories[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }
        chart.getConfiguration().getxAxis().setCategories(categories);
        chart.getConfiguration().addSeries(revenueSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}