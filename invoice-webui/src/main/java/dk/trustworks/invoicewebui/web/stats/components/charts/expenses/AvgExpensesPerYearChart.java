package dk.trustworks.invoicewebui.web.stats.components.charts.expenses;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.dto.UserExpenseDocument;
import dk.trustworks.invoicewebui.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class AvgExpensesPerYearChart {

    private final StatisticsService statisticsService;

    @Autowired
    public AvgExpensesPerYearChart(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public Chart createChart() {
        LocalDate periodStart = LocalDate.of(2016, 7, 1);
        LocalDate periodEnd = LocalDate.now().withDayOfMonth(1).minusMonths(1);
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Average Expenses Per Year");
        chart.getConfiguration().setTitle("");
        chart.getConfiguration().getChart().setType(ChartType.AREASPLINE);
        chart.getConfiguration().getChart().setAnimation(true);
        chart.getConfiguration().getxAxis().getLabels().setEnabled(true);
        chart.getConfiguration().getxAxis().setTickWidth(0);
        chart.getConfiguration().getyAxis().setTitle("");
        chart.getConfiguration().getLegend().setEnabled(false);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ Highcharts.numberFormat(this.y/1000, 0) +' tkr'");
        chart.getConfiguration().setTooltip(tooltip);

        PlotOptionsArea plotOptionsArea1 = new PlotOptionsArea();
        plotOptionsArea1.setColor(new SolidColor(18, 51, 117));

        DataSeries salarySeries = new DataSeries("Average salaries");
        salarySeries.setPlotOptions(plotOptionsArea1);

        PlotOptionsArea plotOptionsArea2 = new PlotOptionsArea();

        DataSeries sharedExpensesSeries = new DataSeries("Average shared expenses");
        sharedExpensesSeries.setPlotOptions(plotOptionsArea2);

        PlotOptionsArea plotOptionsArea3 = new PlotOptionsArea();
        plotOptionsArea3.setColor(new SolidColor(253, 95, 91));

        DataSeries staffSalarySeries = new DataSeries("Average staff salaries");
        staffSalarySeries.setPlotOptions(plotOptionsArea3);

        int years = (int) ChronoUnit.YEARS.between(periodStart, periodEnd);

        String[] yearNames = new String[years];
        int monthCount = 0;
        for (int i = 0; i < years; i++) {
            List<UserExpenseDocument> expensesByMonth = new ArrayList<>();
            LocalDate fiscalDate = periodStart.plusMonths(monthCount);
            for (int j = 0; j < 12; j++) {
                LocalDate currentDate = periodStart.plusMonths(monthCount);
                if(currentDate.isAfter(periodEnd)) break;
                // TODO: expensesByMonth.addAll(statisticsService.getConsultantsExpensesByMonth(currentDate));
                monthCount++;
            }

            salarySeries.add(new DataSeriesItem(fiscalDate.format(DateTimeFormatter.ofPattern("yyyy")), Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getSalary).average().orElse(0.0))));
            sharedExpensesSeries.add(new DataSeriesItem(fiscalDate.format(DateTimeFormatter.ofPattern("yyyy")), Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getSharedExpense).average().orElse(0.0))));
            staffSalarySeries.add(new DataSeriesItem(fiscalDate.format(DateTimeFormatter.ofPattern("yyyy")), Math.round(expensesByMonth.stream().mapToDouble(UserExpenseDocument::getStaffSalaries).average().orElse(0.0))));

            yearNames[i] = fiscalDate.format(DateTimeFormatter.ofPattern("yyyy"));

        }

        chart.getConfiguration().getxAxis().setCategories(yearNames);
        chart.getConfiguration().addSeries(salarySeries);
        chart.getConfiguration().addSeries(staffSalarySeries);
        chart.getConfiguration().addSeries(sharedExpensesSeries);
        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }
}