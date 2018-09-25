package dk.trustworks.invoicewebui.web.profile.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.shared.data.sort.SortDirection;
import dk.trustworks.invoicewebui.model.CKOExpense;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseType;
import dk.trustworks.invoicewebui.repositories.CKOExpenseRepository;
import dk.trustworks.invoicewebui.utils.NumberConverter;

import java.time.LocalDate;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by hans on 09/09/2017.
 */


public class CKOExpenseImpl extends CKOExpenseDesign {

    private final CKOExpenseRepository ckoExpenseRepository;

    public CKOExpenseImpl(CKOExpenseRepository ckoExpenseRepository, User user) {
        this.ckoExpenseRepository = ckoExpenseRepository;
        this.setVisible(false);

        getChartContainer().addComponent(getChart(user));

        getGridCKOExpenses().addSelectionListener(event -> {
            if(event.getAllSelectedItems().size() > 0) {
                //getHlAddBar().setVisible(false);
                getBtnDelete().setVisible(true);
            } else {
                //getHlAddBar().setVisible(true);
                getBtnDelete().setVisible(false);
            }
        });

        getBtnEdit().addClickListener(event -> {
            getDataContainer().setVisible(!getDataContainer().isVisible());
            getChartContainer().setVisible(!getChartContainer().isVisible());
        });

        getBtnDelete().addClickListener(event -> {
            this.ckoExpenseRepository.delete(getGridCKOExpenses().getSelectedItems());
            getGridCKOExpenses().setItems(this.ckoExpenseRepository.findCKOExpenseByUser(user));
        });
        getBtnAddSalary().addClickListener(event -> {
            this.ckoExpenseRepository.save(new CKOExpense(getDfDate().getValue(), user, getTxtDescription().getValue(), Integer.parseInt(getTxtPrice().getValue()), getTxtComments().getValue(), NumberConverter.parseDouble(getTxtDays().getValue()), CKOExpenseType.valueOf(getCbType().getValue())));
            getGridCKOExpenses().setItems(this.ckoExpenseRepository.findCKOExpenseByUser(user));
        });

        getGridCKOExpenses().sort("eventdate", SortDirection.ASCENDING);

        this.setVisible(true);
        getGridCKOExpenses().setItems(ckoExpenseRepository.findCKOExpenseByUser(user));
    }

    private Chart getChart(User user) {
        Chart chart = new Chart(ChartType.COLUMN);
        chart.setWidth(100, Unit.PERCENTAGE);

        Configuration conf = chart.getConfiguration();

        conf.setTitle("Knowledge Budget");

        SortedMap<String, Integer> expenses = new TreeMap<>();
        for (CKOExpense ckoExpense : ckoExpenseRepository.findCKOExpenseByUser(user)) {
            expenses.putIfAbsent(ckoExpense.getEventdate().getYear()+"", 0);
            Integer integer = expenses.get(ckoExpense.getEventdate().getYear() + "");
            expenses.replace(ckoExpense.getEventdate().getYear()+"", (integer+ckoExpense.getPrice()));
        }

        XAxis x = new XAxis();
        x.setTitle("year");

        ListSeries expenseSeries = new ListSeries("expenses");
        ListSeries availableSeries = new ListSeries("available");

        if(expenses.keySet().size() == 0) {
            x.addCategory(LocalDate.now().getYear()+"");
            expenseSeries.addData(0);
            availableSeries.addData(30000 );
        } else {
            for (String year : expenses.keySet()) {
                x.addCategory(year);
                expenseSeries.addData(expenses.get(year));
                availableSeries.addData(30000 - expenses.get(year));
            }
        }

        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Amount (kr)");
        StackLabels sLabels = new StackLabels(true);
        y.setStackLabels(sLabels);
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
        //conf.setLegend(legend);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.x +': '+ this.y +' kr'");
        conf.setTooltip(tooltip);

        PlotOptionsColumn plot = new PlotOptionsColumn();
        plot.setStacking(Stacking.NORMAL);
        plot.setPointPadding(0.2);
        plot.setBorderWidth(0);
        conf.setPlotOptions(plot);

        conf.addSeries(expenseSeries);
        conf.addSeries(availableSeries);
        chart.drawChart(conf);
        return chart;
    }
}
