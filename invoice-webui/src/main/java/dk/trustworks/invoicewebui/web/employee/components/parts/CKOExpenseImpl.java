package dk.trustworks.invoicewebui.web.employee.components.parts;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.*;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.CKOCertification;
import dk.trustworks.invoicewebui.model.CKOExpense;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.*;
import dk.trustworks.invoicewebui.repositories.CKOCertificationsRepository;
import dk.trustworks.invoicewebui.repositories.CKOExpenseRepository;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import org.vaadin.addons.autocomplete.AutocompleteExtension;
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
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static dk.trustworks.invoicewebui.model.enums.CKOExpenseStatus.*;
import static dk.trustworks.invoicewebui.model.enums.CKOExpenseType.*;

/**
 * Created by hans on 09/09/2017.
 */


public class CKOExpenseImpl extends CKOExpenseDesign {

    private final CKOExpenseRepository ckoExpenseRepository;

    private final CKOCertificationsRepository ckoCertificationsRepository;

    private CKOExpense ckoExpense;
    private final Binder<CKOExpense> binder;

    public CKOExpenseImpl(CKOExpenseRepository ckoExpenseRepository, CKOCertificationsRepository ckoCertificationsRepository, User user) {
        this.ckoExpenseRepository = ckoExpenseRepository;
        this.ckoCertificationsRepository = ckoCertificationsRepository;
        this.setVisible(false);

        getCbPurpose().setItems(CKOExpensePurpose.values());
        getCbStatus().setItems(CKOExpenseStatus.values());
        getCbType().setItems(CKOExpenseType.values());

        binder = new Binder<>();
        binder.forField(getDfDate()).bind(CKOExpense::getEventdate, CKOExpense::setEventdate);
        binder.forField(getTxtDescription()).withValidator(new StringLengthValidator(
                "Name must be between 0 and 250 characters long",
                0, 250)).bind(CKOExpense::getDescription, CKOExpense::setDescription);
        AutocompleteExtension<String> descriptionExtension = new AutocompleteExtension<>(getTxtDescription());
        descriptionExtension.setSuggestionGenerator(this::suggestDescription);

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

    private List<String> suggestDescription(String query, int cap) {
        List<String> strings = ckoCertificationsRepository.findAll().stream().map(CKOCertification::getName).collect(Collectors.toList());
        strings.addAll(ckoExpenseRepository.findAll().stream().map(CKOExpense::getDescription).collect(Collectors.toList()));
        return strings.stream()
                .distinct().filter(p -> p.contains(query))
                .limit(cap).collect(Collectors.toList());
    }

    private void refreshExpenseCards(User user, ResponsiveLayout expenseBoard) {
        expenseBoard.removeAllComponents();

        ResponsiveRow filterRow = expenseBoard.addRow();
        Map<CKOExpenseType, List<BoxImpl>> items = new HashMap<>();

        final boolean[] filterDisabled = {true, true, true, true, true};

        MButton btnFilterConferences = new MButton("Conferences");
        btnFilterConferences.withWidth(100, PERCENTAGE).withStyleName("border","tiny").withListener(event -> {
            filterDisabled[0] = !filterDisabled[0];
            if (!filterDisabled[0]) {
                btnFilterConferences.addStyleName("danger");
            } else {
                btnFilterConferences.removeStyleName("danger");
            }
            items.get(CONFERENCE).forEach(c -> c.setVisible(filterDisabled[0]));
        });

        MButton btnFilterCourses = new MButton("Courses");
        btnFilterCourses.withWidth(100, PERCENTAGE).withStyleName("border","tiny").withListener(event -> {
            filterDisabled[1] = !filterDisabled[1];
            if (!filterDisabled[1]) {
                btnFilterCourses.addStyleName("danger");
            } else {
                btnFilterCourses.removeStyleName("danger");
            }
            items.get(COURSE).forEach(c -> c.setVisible(filterDisabled[1]));
        });

        MButton btnFilterSubscriptions = new MButton("Subscriptions");
        btnFilterSubscriptions.withWidth(100, PERCENTAGE).withStyleName("border","tiny").withListener(event -> {
            filterDisabled[2] = !filterDisabled[2];
            if (!filterDisabled[2]) {
                btnFilterSubscriptions.addStyleName("danger");
            } else {
                btnFilterSubscriptions.removeStyleName("danger");
            }
            items.get(SUBSCRIPTION).forEach(c -> c.setVisible(filterDisabled[2]));
        });

        MButton btnFilterMemberships = new MButton("Memberships");
        btnFilterMemberships.withWidth(100, PERCENTAGE).withStyleName("border","tiny").withListener(event -> {
            filterDisabled[3] = !filterDisabled[3];
            if (!filterDisabled[3]) {
                btnFilterMemberships.addStyleName("danger");
            } else {
                btnFilterMemberships.removeStyleName("danger");
            }
            items.get(MEMBERSHIP).forEach(c -> c.setVisible(filterDisabled[3]));
        });

        MButton btnFilterBooks = new MButton("Books");
        btnFilterBooks.withWidth(100, PERCENTAGE).withStyleName("border","tiny").withListener(event -> {
            filterDisabled[4] = !filterDisabled[4];
            if (!filterDisabled[4]) {
                btnFilterBooks.addStyleName("danger");
            } else {
                btnFilterBooks.removeStyleName("danger");
            }
            items.get(BOOKS).forEach(c -> c.setVisible(filterDisabled[4]));
        });

        MButton btnFilterCompleted = new MButton("");
        btnFilterCompleted.withWidth(100, PERCENTAGE).withStyleName("border","tiny").withListener(event -> {
            filterDisabled[5] = !filterDisabled[5];
            if (!filterDisabled[5]) {
                btnFilterBooks.addStyleName("danger");
            } else {
                btnFilterBooks.removeStyleName("danger");
            }
            //items.get(null).forEach(c -> c.setVisible(filterDisabled[5]));
        });

        filterRow.setHorizontalSpacing(ResponsiveRow.SpacingSize.SMALL, true);
        filterRow.addColumn().withComponent(new MLabel("Filter:")).withDisplayRules(12,12,12,12);
        filterRow.addColumn().withComponent(btnFilterConferences).withDisplayRules(4,4,4,4);
        filterRow.addColumn().withComponent(btnFilterCourses).withDisplayRules(4,4,4,4);
        filterRow.addColumn().withComponent(btnFilterSubscriptions).withDisplayRules(4,4,4,4);
        filterRow.addColumn().withComponent(btnFilterMemberships).withDisplayRules(4,4,4,4);
        filterRow.addColumn().withComponent(btnFilterBooks).withDisplayRules(4,4,4,4);
        filterRow.addColumn().withComponent(btnFilterCompleted).withDisplayRules(4,4,4,4);

        ResponsiveRow expenseBoardRow = expenseBoard.addRow();
        for (CKOExpense expense : ckoExpenseRepository.findCKOExpenseByUseruuid(user.getUuid()).stream().sorted(Comparator.comparing(CKOExpense::getEventdate).reversed()).collect(Collectors.toList())) {

            ResponsiveLayout expenseItemLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
            BoxImpl expenseItemBox = new BoxImpl().instance(expenseItemLayout);

            // ROW 1
            ResponsiveRow expenseItemRow = expenseItemLayout.addRow();

            // COLUMN 1.1
            ResponsiveColumn leftColumn = expenseItemRow.addColumn().withDisplayRules(12, 12, 2, 2);
            ResponsiveLayout leftContentLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
            leftColumn.withComponent(leftContentLayout);

            // COLUMN 2.1
            Image icon = new Image("", new ThemeResource("images/icons/" + expense.getType().name().toLowerCase() + "-icon.png"));
            icon.setWidth(100, PERCENTAGE);
            icon.setHeight(100, PERCENTAGE);
            leftContentLayout.addRow().addColumn().withComponent(icon).setAlignment(ResponsiveColumn.ColumnComponentAlignment.CENTER);


            // COLUMN 1.2
            ResponsiveColumn rightContentColumn = expenseItemRow.addColumn().withDisplayRules(12, 12, 10, 10);
            ResponsiveLayout rightContentLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
            rightContentColumn.withComponent(rightContentLayout);

            // ROW 2
            ResponsiveRow titleRow = rightContentLayout.addRow();

            // COLUMN 2.1
            titleRow.addColumn().withComponent(new MLabel(expense.getDescription()).withStyleName("large bold")).withDisplayRules(12, 12, 12, 12);

            // ROW 3
            ResponsiveRow detailRow = rightContentLayout.addRow();

            // COLUMN 3.1
            ResponsiveColumn dateColumn = detailRow.addColumn().withDisplayRules(12, 12, 4, 4);

            CheckBox its_a_certification = new CheckBox("Its a certification", expense.getCertification() == 1);
            CheckBox i_passed = new CheckBox("I passed", expense.getCertified() == 1);

            its_a_certification.addValueChangeListener(event -> {
                expense.setCertification(its_a_certification.getValue()?1:0);
                i_passed.setVisible(its_a_certification.getValue() && expense.getStatus().equals(COMPLETED));
                ckoExpenseRepository.save(expense);
            });
            i_passed.setVisible(expense.getCertified()==1);
            i_passed.addValueChangeListener(event -> {
                expense.setCertified(i_passed.getValue()?1:0);
                ckoExpenseRepository.save(expense);
            });

            MVerticalLayout certificateContent = new MVerticalLayout(
                    its_a_certification,
                    i_passed
            );
            certificateContent.setVisible(expense.getType().equals(COURSE));

            dateColumn.withComponent(new MVerticalLayout(
                    new MLabel(expense.getEventdate().format(DateTimeFormatter.ofPattern("dd. MMMM yyyy")) + " / " + expense.getDays() + " days"),
                    new MHorizontalLayout(
                            new MLabel("Purpose: ").withStyleName("dark-grey-font"),
                            new MLabel(expense.getPurpose().getCaption()).withStyleName("bold dark-grey-font")
                    ),
                    new MHorizontalLayout(
                            new MLabel("Amount (w/o tax): ").withStyleName("dark-grey-font"),
                            new MLabel(expense.getPrice() + "").withStyleName("bold dark-grey-font")
                    ),
                    certificateContent

            ));

            // COLUMN 3.2
            ResponsiveColumn ratingColumn = detailRow.addColumn().withDisplayRules(12, 12, 5, 5);

            ratingColumn.withComponent(new MVerticalLayout(
                        new MLabel(expense.getComment()).withFullWidth()
                    ).withFullWidth()
            );

            // COLUMN 3.3
            ResponsiveColumn buttonColumn = detailRow.addColumn().withDisplayRules(12, 12, 3, 3);

            MButton btnEdit = new MButton("Edit").withStyleName("border").withListener(event -> {
                getBtnAddSalary().setCaption("UPDATE");
                ckoExpense = expense;
                binder.readBean(ckoExpense);
            }).withWidth(100, PERCENTAGE);

            MButton btnDelete = new MButton("Delete").withStyleName("border danger").withListener(event -> {
                ckoExpenseRepository.delete(expense);
                refreshExpenseCards(user, expenseBoard);
            }).withWidth(100, PERCENTAGE);

            MButton btnRate = new MButton("Review").withStyleName("border").withListener(event -> {
                RatingStars ratingStars = createRatingStars(user, expense);
                TextArea reviewTextarea = createReviewTextarea(expense);
                MVerticalLayout ratingContent = new MVerticalLayout(
                        new MLabel("Add a rating from 1 to 5, write a short review and let your colleagues learn from your experiences with this " + expense.getType().getCaption().toLowerCase()).withWidth(300, PIXELS),
                        new MLabel("Rating: ").withStyleName("dark-grey-font"),
                        ratingStars,
                        new MLabel("Review: ").withStyleName("dark-grey-font"),
                        reviewTextarea
                );
                ratingContent.setWidth(300, PIXELS);
                ratingContent.setVisible(expense.getStatus()== COMPLETED);

                Window window = new Window("Rate the " + expense.getType().getCaption(), ratingContent);
                window.setWidth(320, Unit.PIXELS);
                window.setHeight(460, PIXELS);
                window.setModal(true);
                window.setContent(ratingContent);
                window.isClosable();

                ratingContent.add(new MButton("Save").withListener(c -> {
                    expense.setRating(ratingStars.getValue());
                    expense.setRating_comment(reviewTextarea.getValue());
                    ckoExpenseRepository.save(expense);
                    window.close();
                }).withFullWidth());

                UI.getCurrent().addWindow(window);
            }).withVisible(expense.getStatus()== COMPLETED).withWidth(100, PERCENTAGE);

            MButton statusButton = new MButton(expense.getStatus().getCaption()).withStyleName("friendly").withWidth(100, PERCENTAGE);
            statusButton.withListener(event -> {
                if (expense.getStatus().equals(WISHLIST))
                    expense.setStatus(BOOKED);
                else if (expense.getStatus().equals(BOOKED))
                    expense.setStatus(COMPLETED);
                else if (expense.getStatus().equals(COMPLETED))
                    expense.setStatus(WISHLIST);
                if(!expense.getStatus().equals(COMPLETED)) {
                    expense.setCertified(0);
                    i_passed.setValue(expense.getCertified()==1);
                }
                ckoExpenseRepository.save(expense);
                btnRate.setVisible(expense.getStatus()== COMPLETED);
                //ratingContent.setVisible(expense.getStatus()==CKOExpenseStatus.COMPLETED);
                i_passed.setVisible(expense.getStatus()== COMPLETED);
                binder.readBean(ckoExpense = new CKOExpense(user));
                getBtnAddSalary().setCaption("CREATE");
                statusButton.setCaption(expense.getStatus().getCaption());
            });

            leftContentLayout.addRow().addColumn().withComponent(new MVerticalLayout(statusButton).withWidth(100, PERCENTAGE)).setAlignment(ResponsiveColumn.ColumnComponentAlignment.CENTER);

            buttonColumn.withComponent(new MVerticalLayout(
                    btnEdit, btnDelete, new Label(""), btnRate
            ));

            items.putIfAbsent(expense.getType(), new ArrayList<>());
            items.get(expense.getType()).add(expenseItemBox);
            expenseBoardRow.addColumn()
                    .withDisplayRules(12, 12, 12, 12)
                    .withComponent(expenseItemBox);
        }
    }

    private TextArea createReviewTextarea(CKOExpense expense) {
        TextArea reviewTextarea = new TextArea(null, Optional.ofNullable(expense.getRating_comment()).orElse(""));
        reviewTextarea.addFocusListener(event -> {
            //expense.setRating_comment(event.getSource().getValue());
            //ckoExpenseRepository.save(expense);
        });
        reviewTextarea.setWidth(100, PERCENTAGE);
        return reviewTextarea;
    }

    private RatingStars createRatingStars(User user, CKOExpense expense) {
        RatingStars ratingStars = new RatingStars();
        ratingStars.setMaxValue(5);
        ratingStars.setValue(expense.getRating());
        ratingStars.addValueChangeListener(event -> {
            expense.setRating(event.getValue());
            ckoExpenseRepository.save(expense);
            binder.readBean(ckoExpense = new CKOExpense(user));
            getBtnAddSalary().setCaption("CREATE");
        });
        return ratingStars;
    }

    private Chart getChart(User user) {
        Chart chart = new Chart(ChartType.COLUMN);
        chart.setWidth(100, PERCENTAGE);

        Configuration conf = chart.getConfiguration();

        conf.setTitle("Knowledge Budget");

        SortedMap<String, Integer> expenses = new TreeMap<>();
        for (CKOExpense ckoExpense : ckoExpenseRepository.findCKOExpenseByUseruuid(user.getUuid())) {
            if(ckoExpense.getStatus()!=null && ckoExpense.getStatus().equals(WISHLIST)) continue;
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

    public static class MyConverter implements Converter<String, Double> {
        @Override
        public Result<Double> convertToModel(String fieldValue, ValueContext context) {
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
            NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            return formatter.format(aDouble);
        }
    }
}

