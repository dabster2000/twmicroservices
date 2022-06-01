package dk.trustworks.invoicewebui.web.stats.components.charts.revenue;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.EmployeeAggregateData;
import dk.trustworks.invoicewebui.services.BiService;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class RevenuePerConsultantChart {

    private final BiService biService;

    @Autowired
    public RevenuePerConsultantChart(BiService biService) {
        this.biService = biService;
    }

    public Chart createRevenuePerConsultantChart(User user, int fiscalYear) {
        LocalDate periodStart = LocalDate.of(fiscalYear, 7,1);//userService.findByUUID(user.getUuid(), false).getStatuses().stream().min(Comparator.comparing(UserStatus::getStatusdate)).orElse(new UserStatus(ConsultantType.CONSULTANT, StatusType.ACTIVE, LocalDate.now(), 0)).getStatusdate();//LocalDate.of(2017, 07, 01);
        LocalDate periodEnd = periodStart.plusYears(1);

        return getChart(user, periodStart, periodEnd);
    }

    public Chart createRevenuePerConsultantChart(User user, LocalDate periodStart, LocalDate periodEnd) {
        return getChart(user, periodStart, periodEnd);
    }

    private Chart getChart(User user, LocalDate periodStart, LocalDate periodEnd) {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Revenue for " + user.getUsername());
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        DataSeries series = new DataSeries();
        PlotOptionsLine plotOptions = new PlotOptionsLine();
        plotOptions.setColor(SolidColor.BLACK);
        series.setPlotOptions(plotOptions);
        series.setName("Threshold");

        List<DataSeriesItem> list = new ArrayList<>();
        list.add(new DataSeriesItem(0, 1100000));

        series.setData(list);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        DataSeries revenueSeries = new DataSeries("Revenue");
        PlotOptionsAreaspline poc3 = new PlotOptionsAreaspline();
        poc3.setColor(new SolidColor("#123375"));
        revenueSeries.setPlotOptions(poc3);

        double sum = 0.0;
        List<EmployeeAggregateData> data = biService.getEmployeeAggregateDataByPeriod(periodStart, periodEnd).stream().filter(e -> e.getUseruuid().equals(user.getUuid())).collect(Collectors.toList());//revenueService.getRegisteredRevenueByPeriodAndSingleConsultant(user.getUuid(), periodStart, periodEnd);
        int count = 0;
        for (int monthNumber = 0; monthNumber < 12; monthNumber++) {
            LocalDate currentDate = periodStart.plusMonths(monthNumber);
            if(!currentDate.isBefore(periodEnd)) continue;
            sum += data.stream().filter(e -> e.getMonth().isEqual(currentDate)).mapToDouble(EmployeeAggregateData::getRegisteredAmount).sum();
            revenueSeries.add(new DataSeriesItem(stringIt(currentDate, "MMMM"), sum));
            chart.getConfiguration().getxAxis().addCategory(stringIt(currentDate, "MMMM"));
            count = monthNumber;
        }
        list.add(new DataSeriesItem(count, 1100000));

        chart.getConfiguration().addSeries(revenueSeries);
        chart.getConfiguration().addSeries(series);

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

}