package dk.trustworks.invoicewebui.web.employee.components.charts;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.EmployeeAggregateData;
import dk.trustworks.invoicewebui.services.BiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ContractUtilizationPerMonthChart {

    private final BiService biService;

    @Autowired
    public ContractUtilizationPerMonthChart(BiService biService) {
        this.biService = biService;
    }

    public Chart createContractUtilizationPerMonthChart(User user, LocalDate periodStart, LocalDate periodEnd) {
        System.out.println("ConsultantHoursPerMonthChart.createBillableConsultantHoursPerMonthChart");
        System.out.println("user = [" + user + "], periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);
        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);

        chart.setCaption("Contract utilization fiscal year 07/"+(periodStart.getYear())+" - 06/"+periodEnd.getYear());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setStacking(Stacking.NORMAL);
        chart.getConfiguration().setPlotOptions(plotOptions);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y, 0) +' %'");
        chart.getConfiguration().setTooltip(tooltip);

        String[] categories = new String[months];
        DataSeries contractUtilizationSeries = new DataSeries("Contract Utilization");
        List<EmployeeAggregateData> employeeData = biService.getEmployeeAggregateDataByPeriod(periodStart, periodEnd).stream().filter(e -> e.getUseruuid().equals(user.getUuid())).sorted(Comparator.comparing(EmployeeAggregateData::getMonth)).collect(Collectors.toList());
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            if(i < employeeData.size())
                contractUtilizationSeries.add(new DataSeriesItem(stringIt(currentDate, "MMM-yyyy"), employeeData.get(i).getContractUtilization()>0?(1.0 / employeeData.get(i).getContractUtilization())*employeeData.get(i).getActualUtilization()*100:100));

            categories[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }
        chart.getConfiguration().getxAxis().setCategories(categories);

        chart.getConfiguration().addSeries(contractUtilizationSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}