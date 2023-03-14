package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.model.dto.CompanyAggregateData;
import dk.trustworks.invoicewebui.model.enums.InvoiceStatus;
import dk.trustworks.invoicewebui.services.*;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class RevenuePerMonthChart {

    private final InvoiceService invoiceService;

    private final RevenueService revenueService;
    private final BudgetService budgetService;

    private final BiService biService;

    @Autowired
    public RevenuePerMonthChart(InvoiceService invoiceService, RevenueService revenueService, BudgetService budgetService, BiService biService) {
        this.invoiceService = invoiceService;
        this.revenueService = revenueService;
        this.budgetService = budgetService;
        this.biService = biService;
    }

    public Chart createRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd) {
        return createRevenuePerMonthChart(periodStart, periodEnd, true);
    }

    public Chart createRevenuePerMonthChart(LocalDate periodStart, LocalDate periodEnd, boolean showEarnings) {
        Chart chart = new Chart();
        chart.setWidth(100, Sizeable.Unit.PERCENTAGE);

        chart.setCaption("Revenue and Budget");
        if(showEarnings) chart.setCaption("Revenue, Budget, and Gross Profit");
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

        List<CompanyAggregateData> data = biService.getCompanyAggregateDataByPeriod(periodStart, periodEnd);
        //List<GraphKeyValue> revenue = revenueService.getInvoicedOrRegisteredRevenueByPeriod(periodStart, periodEnd);
        //List<GraphKeyValue> budgets = budgetService.getBudgetsByPeriod(periodStart, periodEnd);

        DataSeries budgetSeries = new DataSeries("Budget Revenue");
        PlotOptionsAreaspline plotOptionsArea = new PlotOptionsAreaspline();
        plotOptionsArea.setColor(new SolidColor("#123375"));
        budgetSeries.setPlotOptions(plotOptionsArea);
        chart.getConfiguration().addSeries(budgetSeries);

        DataSeries revenueSeries = new DataSeries("Registered Hours Revenue");
        PlotOptionsAreaspline plotOptionsArea2 = new PlotOptionsAreaspline();
        plotOptionsArea2.setColor(new SolidColor("#7084AC"));
        revenueSeries.setPlotOptions(plotOptionsArea2);
        chart.getConfiguration().addSeries(revenueSeries);

        DataSeries revenueOrInvoiceSeries = new DataSeries("Invoiced Revenue");
        PlotOptionsSpline plotOptionsArea4 = new PlotOptionsSpline();
        plotOptionsArea4.setColor(new SolidColor("#CFD6E3"));
        revenueOrInvoiceSeries.setPlotOptions(plotOptionsArea4);

        DataSeries earningsSeries = new DataSeries("Gross Profit");
        PlotOptionsSpline plotOptionsArea3 = new PlotOptionsSpline();
        plotOptionsArea3.setColor(new SolidColor("#54D69E"));
        plotOptionsArea3.setNegativeColor(new SolidColor("#FD5F5B"));
        earningsSeries.setPlotOptions(plotOptionsArea3);

        /*
        for (int m = 0; m < 12; m++) {
            LocalDate date = periodStart.plusMonths(m);
            String name = DateUtils.stringIt(date, "MMM-yyyy");
            budgetSeries.add(new DataSeriesItem(name, budgets.get(m).getValue()));
            revenueOrInvoiceSeries.add(new DataSeriesItem(name, revenue.get(m).getValue()));
        }
         */

        data.forEach(m -> {
            String name = DateUtils.stringIt(m.getMonth(), "MMM-yyyy");
            budgetSeries.add(new DataSeriesItem(name, m.getBudgetAmount()));
            revenueSeries.add(new DataSeriesItem(name, m.getRegisteredAmount()));
            revenueOrInvoiceSeries.add(new DataSeriesItem(name, m.getInvoicedAmount()>0?m.getInvoicedAmount():m.getRegisteredAmount()));
            earningsSeries.add(new DataSeriesItem(name, m.getInvoicedAmount()>0?m.getInvoicedAmount()-m.calcExpensesSum():0.0));
        });

        if(showEarnings) {
            chart.getConfiguration().addSeries(revenueOrInvoiceSeries);
            chart.getConfiguration().addSeries(earningsSeries);
        }

        chart.getConfiguration().getxAxis().setCategories(StatisticsService.getMonthCategories(periodStart, periodEnd));

        chart.addPointClickListener(event -> {
            if(!event.getSeries().getName().equals("Invoiced Revenue")) return;
            LocalDate currentDate = periodStart.plusMonths(event.getPointIndex());

            List<Invoice> invoiceList = invoiceService.getInvoicesForSingleMonthUsingBookingDate(currentDate);
            invoiceList = invoiceList.stream().filter(invoice -> invoice.getStatus().equals(InvoiceStatus.CREATED) || invoice.getStatus().equals(InvoiceStatus.CREDIT_NOTE)).filter(invoice -> {
                if (invoice.bookingdate.withDayOfMonth(1).isEqual(currentDate.withDayOfMonth(1))) return true;
                else return invoice.bookingdate.isEqual(LocalDate.of(1900, 1, 1)) && invoice.invoicedate.withDayOfMonth(1).isEqual(currentDate.withDayOfMonth(1));
            }).collect(Collectors.toList());

            Grid<Invoice> invoiceDetailGrid = createExpenseDetailGrid(invoiceList);
            UI.getCurrent().addWindow(new Window("Invoice details for " + event.getCategory(), invoiceDetailGrid));
        });

        Credits c = new Credits("");
        chart.getConfiguration().setCredits(c);
        return chart;
    }

    public Grid<Invoice> createExpenseDetailGrid(List<Invoice> invoiceList) {
        Grid<Invoice> treeGrid = new Grid<>();
        treeGrid.setWidth(100, Sizeable.Unit.PERCENTAGE);
        treeGrid.addColumn(Invoice::getInvoicenumber).setCaption("#").setId("invoicenumber-column");
        treeGrid.addColumn(Invoice::getInvoicedate).setCaption("Date").setId("date-column");
        treeGrid.addColumn(Invoice::getClientname).setCaption("Client").setId("client-column");
        treeGrid.addColumn(Invoice::getProjectname).setCaption("Project").setId("project-column");
        treeGrid.addColumn(Invoice::getType).setCaption("Type").setId("type-column");
        treeGrid.addColumn(Invoice::getSumNoTax).setCaption("Amount").setId("sum-column");
        treeGrid.sort("client-column", SortDirection.ASCENDING);
        treeGrid.setItems(invoiceList);
        return treeGrid;
    }
}