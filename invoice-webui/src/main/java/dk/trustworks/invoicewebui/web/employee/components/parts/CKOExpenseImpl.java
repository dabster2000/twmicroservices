package dk.trustworks.invoicewebui.web.employee.components.parts;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.*;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.ThemeResource;
import dk.trustworks.invoicewebui.model.CKOExpense;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.CKOExpensePurpose;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseStatus;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.CKOExpenseRepository;
import dk.trustworks.invoicewebui.utils.DateUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by hans on 09/09/2017.
 */


public class CKOExpenseImpl extends CKOExpenseDesign {

    private final CKOExpenseRepository ckoExpenseRepository;

    private CKOExpense ckoExpense;
    private final Binder<CKOExpense> binder;

    public CKOExpenseImpl(CKOExpenseRepository ckoExpenseRepository, User user) {
        this.ckoExpenseRepository = ckoExpenseRepository;
        this.setVisible(false);

        getCbPurpose().setItems(CKOExpensePurpose.values());
        getCbStatus().setItems(CKOExpenseStatus.values());
        getCbType().setItems(CKOExpenseType.values());

        binder = new Binder<>();
        binder.forField(getDfDate()).bind(CKOExpense::getEventdate, CKOExpense::setEventdate);
        binder.forField(getTxtDescription()).withValidator(new StringLengthValidator(
                "Name must be between 0 and 250 characters long",
                0, 250)).bind(CKOExpense::getDescription, CKOExpense::setDescription);
        binder.forField(getTxtPrice()).withConverter(new StringToIntegerConverter("Must enter a number")).bind(CKOExpense::getPrice, CKOExpense::setPrice);
        binder.forField(getTxtComments()).bind(CKOExpense::getComment, CKOExpense::setComment);
        binder.forField(getTxtDays()).withConverter(new MyConverter()).bind(CKOExpense::getDays, CKOExpense::setDays);
        binder.forField(getCbPurpose()).bind(CKOExpense::getPurpose, CKOExpense::setPurpose);
        binder.forField(getCbStatus()).bind(CKOExpense::getStatus, CKOExpense::setStatus);
        binder.forField(getCbType()).bind(CKOExpense::getType, CKOExpense::setType);

        ckoExpense = new CKOExpense(user);
        binder.readBean(ckoExpense);

        getChartContainer().addComponent(getChart(user));

        //getExpenseCardContainer().removeAllComponents();
        ResponsiveLayout expenseBoard = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID).withFlexible();
        getExpenseCardContainer().addComponent(expenseBoard);

        refreshExpenseCards(user, expenseBoard);

        /*
        getGridCKOExpenses().addSelectionListener(event -> {
            if(event.getAllSelectedItems().size() > 1) {
                //getHlAddBar().setVisible(false);
                getBtnDelete().setVisible(true);
                getBtnEditItem().setVisible(false);
            } else if (event.getAllSelectedItems().size() == 1) {
                getBtnDelete().setVisible(true);
                getBtnEditItem().setVisible(true);
            } else {
                //getHlAddBar().setVisible(true);
                getBtnDelete().setVisible(false);
                getBtnEditItem().setVisible(false);
            }
        });
        */

        getBtnEdit().addClickListener(event -> {
            getDataContainer().setVisible(!getDataContainer().isVisible());
            getChartContainer().setVisible(!getChartContainer().isVisible());
            getChartContainer().removeAllComponents();
            getChartContainer().addComponent(getChart(user));
        });
/*
        getBtnDelete().addClickListener(event -> {
            //this.ckoExpenseRepository.delete(getGridCKOExpenses().getSelectedItems());
            //getGridCKOExpenses().setItems(this.ckoExpenseRepository.findCKOExpenseByUseruuid(user));
        });

        getBtnEditItem().addClickListener(event -> {
            getBtnAddSalary().setCaption("UPDATE");
            getBtnDelete().setVisible(true);
            getBtnEditItem().setVisible(false);
            //ckoExpense = getGridCKOExpenses().getSelectedItems().stream().findFirst().get();
            binder.readBean(ckoExpense);
            //getGridCKOExpenses().deselectAll();
        });
*/
        getBtnAddSalary().addClickListener(event -> {
            //this.ckoExpenseRepository.create(new CKOExpense(getDfDate().getValue(), user, getTxtDescription().getValue(), Integer.parseInt(getTxtPrice().getValue()), getTxtComments().getValue(), NumberConverter.parseDouble(getTxtDays().getValue()), CKOExpenseType.valueOf(getCbType().getValue()), CKOExpenseStatus.valueOf(getCbStatus().getValue()), CKOExpensePurpose.valueOf(getCbPurpose().getValue())));
            try {
                binder.writeBean(ckoExpense);
                ckoExpenseRepository.save(ckoExpense);
                binder.readBean(ckoExpense = new CKOExpense(user));
                getBtnAddSalary().setCaption("CREATE");
            } catch (ValidationException e) {
                e.printStackTrace();
            }
            //getGridCKOExpenses().setItems(this.ckoExpenseRepository.findCKOExpenseByUseruuid(user));
            refreshExpenseCards(user, expenseBoard);
        });

        //getGridCKOExpenses().sort("eventdate", SortDirection.ASCENDING);

        this.setVisible(true);
        //getGridCKOExpenses().setItems(ckoExpenseRepository.findCKOExpenseByUseruuid(user));
    }

    private void refreshExpenseCards(User user, ResponsiveLayout expenseBoard) {
        expenseBoard.removeAllComponents();
        ResponsiveRow row = expenseBoard.addRow();

        for (CKOExpense expense : ckoExpenseRepository.findCKOExpenseByUseruuid(user.getUuid())) {
            CKOExpenseItem expenseItem = new CKOExpenseItem();
            expenseItem.getImgIcon().setSource(new ThemeResource("images/icons/"+expense.getType().name().toLowerCase()+"-icon.png"));
            expenseItem.getLblDate().setValue(expense.getEventdate().format(DateTimeFormatter.ofPattern("dd. MMMM yyyy")));
            expenseItem.getLblDays().setValue(expense.getDays()+"");
            expenseItem.getLblDescription().setValue(expense.getDescription());
            expenseItem.getLblAmount().setValue(expense.getPrice()+"");
            expenseItem.getLblStatus().setValue(expense.getStatus().getCaption());
            expenseItem.getLblPurpose().setValue(expense.getPurpose().getCaption());
            expenseItem.getVlExtra().setVisible(false);
            expenseItem.getLblComments().setValue(expense.getComment());
            expenseItem.getBtnEdit().addClickListener(event -> {
                getBtnAddSalary().setCaption("UPDATE");
                ckoExpense = expense;
                binder.readBean(ckoExpense);
            });
            expenseItem.getBtnDelete().addClickListener(event -> {
                ckoExpenseRepository.delete(expense);
                refreshExpenseCards(user, expenseBoard);
            });
            expenseItem.getBtnMore().addClickListener(event -> {
                expenseItem.getVlExtra().setVisible(true);
                expenseItem.getBtnMore().setVisible(false);
            });
            row.addColumn()
                    .withDisplayRules(12, 12, 4, 4)
                    .withComponent(expenseItem);
        }
    }

    private Chart getChart(User user) {
        Chart chart = new Chart(ChartType.COLUMN);
        chart.setWidth(100, Unit.PERCENTAGE);

        Configuration conf = chart.getConfiguration();

        conf.setTitle("Knowledge Budget");

        SortedMap<String, Integer> expenses = new TreeMap<>();
        for (CKOExpense ckoExpense : ckoExpenseRepository.findCKOExpenseByUseruuid(user.getUuid())) {
            if(ckoExpense.getStatus()!=null && ckoExpense.getStatus().equals(CKOExpenseStatus.WISHLIST)) continue;
            expenses.putIfAbsent(ckoExpense.getEventdate().getYear()+"", 0);
            Integer integer = expenses.get(ckoExpense.getEventdate().getYear() + "");
            expenses.replace(ckoExpense.getEventdate().getYear()+"", (integer+ckoExpense.getPrice()));
        }

        XAxis x = new XAxis();
        x.setTitle("year");

        ListSeries expenseSeries = new ListSeries("used");
        ListSeries availableSeries = new ListSeries("available");

        int maxBudgetFullYear = 24000;
        int maxBudgetFirstYear = maxBudgetFullYear;
        Optional<UserStatus> firstStatus = user.getStatuses().stream().filter(userStatus -> userStatus.getStatus().equals(StatusType.ACTIVE)).min(Comparator.comparing(UserStatus::getStatusdate));
        if(!firstStatus.isPresent()) return chart;

        if(DateUtils.countMonthsBetween(firstStatus.get().getStatusdate(), LocalDate.now()) < 12)
            maxBudgetFirstYear = DateUtils.countMonthsBetween(firstStatus.get().getStatusdate(), LocalDate.now()) * 2000;

        if(expenses.keySet().size() == 0) {
            x.addCategory(LocalDate.now().getYear()+"");
            expenseSeries.addData(0);
            availableSeries.addData(maxBudgetFirstYear );
        } else {
            for (String year : expenses.keySet()) {
                x.addCategory(year);
                expenseSeries.addData(expenses.get(year));
                if(Integer.parseInt(year) == firstStatus.get().getStatusdate().getYear())
                    availableSeries.addData(maxBudgetFirstYear - expenses.get(year));
                else
                    availableSeries.addData(maxBudgetFullYear - expenses.get(year));
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

    public class MyConverter implements Converter<String, Double> {
        @Override
        public Result<Double> convertToModel(String fieldValue, ValueContext context) {
            System.out.println("MyConverter.convertToModel");
            System.out.println("fieldValue = [" + fieldValue + "], context = [" + context + "]");
            // Produces a converted value or an error
            try {
                // ok is a static helper method that creates a Result
                NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
                return Result.ok(formatter.parse(fieldValue).doubleValue());
            } catch (NumberFormatException | ParseException e) {
                e.printStackTrace();
                // error is a static helper method that creates a Result
                return Result.error("Please enter a number");
            }
        }

        @Override
        public String convertToPresentation(Double aDouble, ValueContext context) {
            System.out.println("MyConverter.convertToPresentation");
            System.out.println("aDouble = [" + aDouble + "], context = [" + context + "]");
            // Converting to the field type should always succeed,
            // so there is no support for returning an error Result.
            NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            return formatter.format(aDouble);
        }
    }
}

