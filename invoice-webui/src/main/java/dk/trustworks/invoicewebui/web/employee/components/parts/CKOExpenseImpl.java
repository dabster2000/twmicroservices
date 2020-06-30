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
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.CKOExpense;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.CKOExpensePurpose;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseStatus;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.CKOExpenseRepository;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.teemu.ratingstars.RatingStars;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

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

        ResponsiveLayout expenseBoard = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID).withFlexible();
        getExpenseCardContainer().addComponent(expenseBoard);

        refreshExpenseCards(user, expenseBoard);

        getBtnEdit().setCaption("Register your continued education: ");
        getBtnEdit().setDescription("Gather your wishes for training here, register them when they are booked and evaluate them when you have been away");
        getBtnEdit().addClickListener(event -> {
            getDataContainer().setVisible(!getDataContainer().isVisible());
            getChartContainer().setVisible(!getChartContainer().isVisible());
            getChartContainer().removeAllComponents();
            getChartContainer().addComponent(getChart(user));
        });

        getBtnAddSalary().addClickListener(event -> {
            try {
                binder.writeBean(ckoExpense);
                ckoExpenseRepository.save(ckoExpense);
                binder.readBean(ckoExpense = new CKOExpense(user));
                getBtnAddSalary().setCaption("CREATE");
            } catch (ValidationException e) {
                e.printStackTrace();
            }
            refreshExpenseCards(user, expenseBoard);
        });

        this.setVisible(true);
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
            expenseItem.getLblStatus().setVisible(false);

            MButton button = new MButton(expense.getStatus().getCaption()).withStyleName("friendly").withFullWidth();

            RatingStars ratingStars = new RatingStars();
            ratingStars.setMaxValue(5);
            ratingStars.setValue(expense.getRating());
            ratingStars.addValueChangeListener(event -> {
                expense.setRating(event.getValue());
                ckoExpenseRepository.save(expense);
                binder.readBean(ckoExpense = new CKOExpense(user));
                getBtnAddSalary().setCaption("CREATE");
                button.setCaption(expense.getStatus().getCaption());
            });

            TextArea textField = new TextArea(expense.getRating_comment());
            PopupView popupView = new PopupView(null, new MHorizontalLayout(textField));
            popupView.addPopupVisibilityListener(event1 -> {
                expense.setRating_comment(textField.getValue());
                ckoExpenseRepository.save(expense);
            });

            MHorizontalLayout ratingContent = new MHorizontalLayout(
                    new MLabel("Rating: ").withStyleName("dark-grey-font").withHeight(24, Unit.PIXELS),
                    ratingStars,
                    new MButton(MaterialIcons.FEEDBACK).withStyleName("icon-only flat").withDescription("Click to elaborate on your rating").withListener(event -> {
                        popupView.setPopupVisible(true);
                    }),
                    popupView
            );
            ratingContent.setWidth(100, Unit.PERCENTAGE);
            ratingContent.setVisible(expense.getStatus()==CKOExpenseStatus.COMPLETED);

            CheckBox its_a_certification = new CheckBox("Its a certification", expense.getCertification() == 1);
            CheckBox i_passed = new CheckBox("I passed", expense.getCertified() == 1);

            button.addClickListener(event -> {
                if (expense.getStatus().equals(CKOExpenseStatus.WISHLIST))
                    expense.setStatus(CKOExpenseStatus.BOOKED);
                else if (expense.getStatus().equals(CKOExpenseStatus.BOOKED))
                    expense.setStatus(CKOExpenseStatus.COMPLETED);
                else if (expense.getStatus().equals(CKOExpenseStatus.COMPLETED))
                    expense.setStatus(CKOExpenseStatus.WISHLIST);
                ckoExpenseRepository.save(expense);
                ratingContent.setVisible(expense.getStatus()==CKOExpenseStatus.COMPLETED);
                i_passed.setVisible(expense.getStatus()==CKOExpenseStatus.COMPLETED);
                binder.readBean(ckoExpense = new CKOExpense(user));
                getBtnAddSalary().setCaption("CREATE");
                button.setCaption(expense.getStatus().getCaption());
                expenseItem.getVLStatus().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
            });

            its_a_certification.addValueChangeListener(event -> {
                expense.setCertification(its_a_certification.getValue()?1:0);
                i_passed.setVisible(its_a_certification.getValue() && expense.getStatus().equals(CKOExpenseStatus.COMPLETED));
                ckoExpenseRepository.save(expense);
            });
            i_passed.setVisible(false);
            i_passed.addValueChangeListener(event -> {
                expense.setCertified(i_passed.getValue()?1:0);
                ckoExpenseRepository.save(expense);
            });

            MVerticalLayout certificateContent = new MVerticalLayout(
                    its_a_certification,
                    i_passed
            );
            certificateContent.setVisible(expense.getType().equals(CKOExpenseType.COURSE));

            expenseItem.getVLStatus().addComponents(
                    new MHorizontalLayout(button).withFullWidth().alignAll(Alignment.MIDDLE_CENTER),
                    ratingContent.alignAll(Alignment.MIDDLE_CENTER),
                    certificateContent
                    );
            expenseItem.getVLStatus().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
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
                    .withDisplayRules(12, 12, 6, 4)
                    .withComponent(expenseItem);
        }
    }

    private static class PopupTextFieldContent implements PopupView.Content {
        private final HorizontalLayout layout;
        private final TextArea textField = new TextArea("Minimized HTML content", "Click to edit");

        private PopupTextFieldContent(String text) {
            textField.setValue(new String(Base64.getDecoder().decode(text)));
            layout = new HorizontalLayout(textField);
        }

        @Override
        public final Component getPopupComponent() {
            return layout;
        }

        @Override
        public final String getMinimizedValueAsHTML() {
            return textField.getValue();
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

