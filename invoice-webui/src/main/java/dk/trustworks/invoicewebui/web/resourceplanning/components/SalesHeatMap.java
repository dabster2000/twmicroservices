package dk.trustworks.invoicewebui.web.resourceplanning.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.BudgetDocument;
import dk.trustworks.invoicewebui.model.dto.UserBooking;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.services.*;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hans on 19/12/2016.
 */

@SpringComponent
@SpringUI
public class SalesHeatMap {

    private final ClientRepository clientRepository;

    private final PhotoService photoService;

    private final StatisticsService statisticsService;

    private final UserService userService;

    double[] monthTotalAvailabilites;
    double[] monthAvailabilites;

    @Autowired
    public SalesHeatMap(ClientRepository clientRepository, PhotoService photoService, StatisticsService statisticsService, UserService userService1) {
        this.clientRepository = clientRepository;
        this.photoService = photoService;
        this.statisticsService = statisticsService;
        this.userService = userService1;
    }

    public Component getChart(LocalDate localDateStart, LocalDate localDateEnd) {
        int monthPeriod = (int) ChronoUnit.MONTHS.between(localDateStart, localDateEnd)+1;
        monthTotalAvailabilites = new double[monthPeriod];
        monthAvailabilites = new double[monthPeriod];

        String[] monthNames = getMonthNames(localDateStart, localDateEnd);

        Chart chart = new Chart();
        chart.setWidth("100%");

        Configuration config = chart.getConfiguration();
        config.getChart().setType(ChartType.HEATMAP);
        config.getChart().setMarginTop(40);
        config.getChart().setMarginBottom(40);

        config.getTitle().setText("Employee Availability Per Month");

        config.getColorAxis().setMin(0);
        config.getColorAxis().setMax(100);
        config.getColorAxis().setMinColor(SolidColor.WHITE);
        config.getColorAxis().setMaxColor(new SolidColor(163, 211, 209));


        config.getLegend().setLayout(LayoutDirection.VERTICAL);
        config.getLegend().setAlign(HorizontalAlign.RIGHT);
        config.getLegend().setMargin(0);
        config.getLegend().setVerticalAlign(VerticalAlign.TOP);
        config.getLegend().setY(25);
        config.getLegend().setSymbolHeight(320);

        HeatSeries rs = new HeatSeries("% availability");
        int userNumber = 0;


        Map<String, Map<String, double[]>> userAllocationPerAssignmentMap = new HashMap<>();
        List<User> users = userService.findCurrentlyEmployedUsers(ConsultantType.CONSULTANT).stream().sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toList());
        for (int i = 0; i < 12; i++) {
            LocalDate currentDate = localDateStart.withDayOfMonth(1).plusMonths(i);

            for (User user : users) {
                List<BudgetDocument> budgets = statisticsService.getBudgetData()
                        .stream()
                        .filter(budgetDocument -> budgetDocument.getUser().getUuid().equals(user.getUuid()) && budgetDocument.getMonth().isEqual(currentDate.withDayOfMonth(1)))
                        .collect(Collectors.toList());

                for (BudgetDocument budget : budgets) {
                    userAllocationPerAssignmentMap.putIfAbsent(user.getUuid(), new HashMap<>());
                    userAllocationPerAssignmentMap.get(user.getUuid()).putIfAbsent(budget.getClient().getUuid(), new double[12]);
                    userAllocationPerAssignmentMap.get(user.getUuid()).get(budget.getClient().getUuid())[i] +=  (budget.getBudgetHours());
                }
            }

        }
        for (User user : users) {
            LocalDate localDate = localDateStart;
            int m = 0;
            while(localDate.isBefore(localDateEnd) || localDate.isEqual(localDateEnd)) {
                double budget = statisticsService.getConsultantBudgetHoursByMonth(user, localDate.withDayOfMonth(1));
                monthAvailabilites[m] += budget;
                double availability = statisticsService.getConsultantAvailabilityByMonth(user, localDate.withDayOfMonth(1)).getNetAvailableHours();
                monthTotalAvailabilites[m] += availability;
                rs.addHeatPoint(m, userNumber, Math.round(100 - (budget / availability)*100.0));
                localDate = localDate.plusMonths(1);
                m++;
            }
            userNumber++;
        }

        config.getxAxis().setCategories(monthNames);

        String[] consultants = users.stream().map(User::getUsername).toArray(String[]::new);
        config.getyAxis().setCategories(consultants);

        chart.addPointClickListener(event -> {
            int intValue = new Double(Math.floor(event.getPointIndex() / 12.0)).intValue();
            User user = users.get(intValue);


            final Window window = new Window("Window");
            //window.setWidth(300.0f, Sizeable.Unit.PIXELS);
            window.setModal(true);
            window.setCaption("Detailed allocation");
            final GridLayout grid = new GridLayout();

            window.setContent(grid);

            Map<String, double[]> assignmentsMap = userAllocationPerAssignmentMap.get(user.getUuid());
            grid.setRows(assignmentsMap.size()+1);
            grid.setColumns(13);
            grid.setMargin(true);
            grid.setSpacing(true);
            grid.addStyleName("outlined");

            LocalDate localDate = localDateStart;
            grid.addComponent(new Label());
            for (int i = 1; i < 13; i++) {
                grid.addComponent(new MLabel(localDate.format(DateTimeFormatter.ofPattern("MMM"))));
                localDate = localDate.plusMonths(1);
            }

            for (String s : assignmentsMap.keySet()) {
                grid.addComponent(new MLabel(clientRepository.findOne(s).getName()).withStyleName("bold"));
                for (double v : assignmentsMap.get(s)) {
                    grid.addComponent(new MLabel(NumberConverter.convertDoubleToInt(v)+""));
                }
            }

            UI.getCurrent().addWindow(window);
        });


        PlotOptionsHeatmap plotOptionsHeatmap = new PlotOptionsHeatmap();
        plotOptionsHeatmap.setDataLabels(new DataLabels());
        plotOptionsHeatmap.getDataLabels().setEnabled(true);
        plotOptionsHeatmap.getStates().getHover().setFillColor(SolidColor.BLACK);

        SeriesTooltip tooltip = new SeriesTooltip();
        tooltip.setHeaderFormat("{series.name}<br/>");
        tooltip.setPointFormat("Amount: <b>{point.value}</b> ");
        plotOptionsHeatmap.setTooltip(tooltip);

        Hover hover = new Hover();
        hover.setFillColor(new SolidColor(18, 51, 117));
        States states = new States();
        states.setHover(hover);
        plotOptionsHeatmap.setStates(states);

        config.setPlotOptions(plotOptionsHeatmap);

        config.setSeries(rs);

        chart.drawChart(config);
        chart.setHeight("700px");

        System.out.println("----------------------------------------------------");
        return chart;
    }

    public Component getAvailabilityChart(LocalDate localDateStart, LocalDate localDateEnd) {
        int monthPeriod = (int) ChronoUnit.MONTHS.between(localDateStart, localDateEnd)+1;
        String[] monthNames = getMonthNames(localDateStart, localDateEnd);
        Chart chart = new Chart(ChartType.AREASPLINE);
        chart.setHeight("450px");

        Configuration conf = chart.getConfiguration();

        conf.setTitle(new Title("Total % availability"));

        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setAlign(HorizontalAlign.LEFT);
        legend.setFloating(true);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(150);
        legend.setY(100);
        conf.setLegend(legend);

        XAxis xAxis = new XAxis();
        xAxis.setCategories(monthNames);
        xAxis.setLineColor(new SolidColor(163, 211, 209));
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle(new AxisTitle("Total Availability"));
        conf.addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        // Customize tooltip formatting
        tooltip.setHeaderFormat("");
        tooltip.setPointFormat("{series.name}: {point.y} %");
        conf.setTooltip(tooltip);

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setColor(SolidColor.GREEN);
        plotOptions.setFillOpacity(0.5);
        conf.setPlotOptions(plotOptions);

        ListSeries listSeries = new ListSeries();
        for (int j = 0; j < monthPeriod; j++) {
            listSeries.addData(Math.round(100 - ((monthAvailabilites[j] / monthTotalAvailabilites[j]) * 100.0)));
        }

        conf.addSeries(listSeries);

        chart.drawChart(conf);
        chart.setHeight("700px");

        return chart;
    }

    public Component getSalesOverview() {
        TreeGrid<UserBooking> treeGrid = new TreeGrid<>();
        treeGrid.setWidth(100, Sizeable.Unit.PERCENTAGE);
        treeGrid.setStyleGenerator(t -> {
            if (t.getBookingPercentage(1)>100.0 || t.getBookingPercentage(2) > 100.0 || t.getBookingPercentage(3) > 100.0) {
                return "error_row";
            } else {
                return null;
            }
        });

        int monthsInFuture = 7;
        int monthsInPast = 3;

        LocalDate currentDate;

        List<UserBooking> userBookings = statisticsService.getUserBooking(monthsInPast, monthsInFuture);

        currentDate = LocalDate.now().withDayOfMonth(1).minusMonths(monthsInPast);

        treeGrid.setItems(userBookings, UserBooking::getSubProjects);

        HeaderRow topHeader = treeGrid.prependHeaderRow();

        treeGrid.addComponentColumn(userBooking -> new MHorizontalLayout(
                photoService.getRoundImage(
                        userBooking.getUuid(),
                        false,
                        30,
                        Sizeable.Unit.PIXELS),
                new Label(userBooking.getUsername()))).setCaption("Name").setId("name-column");
        //treeGrid.addColumn(UserBooking::getUsername).setCaption("Name").setId("name-column");
        treeGrid.setFrozenColumnCount(1);

        int key = 0;
        for (int i = 0; i < monthsInFuture; i++) {
            Grid.Column<?, ?>[] headerCells = new Grid.Column<?, ?>[4];
            if(i<monthsInPast) {
                key = createPastColumns(treeGrid, currentDate, key, headerCells, i);
            } else {
                key = createFutureColumns(treeGrid, currentDate, key, headerCells, i);
            }
            topHeader.join(headerCells).setText(currentDate.format(DateTimeFormatter.ofPattern("MMMM")));
            currentDate = currentDate.plusMonths(1);
        }

        treeGrid.addCollapseListener(event -> {
            Notification.show(
                    "Project '" + event.getCollapsedItem().getUsername() + "' collapsed.",
                    Notification.Type.TRAY_NOTIFICATION);
        });
        treeGrid.addExpandListener(event -> {
            Notification.show(
                    "Project '" + event.getExpandedItem().getUsername() + "' expanded.",
                    Notification.Type.TRAY_NOTIFICATION);
        });

        return treeGrid;
    }



    private int createFutureColumns(TreeGrid<UserBooking> treeGrid, LocalDate currentDate, int key, Grid.Column<?, ?>[] headerCells, int colNumber) {
        headerCells[0] = treeGrid.addColumn(userBooking -> NumberUtils.round(userBooking.getAmountItemsPerProjects(colNumber),2))
                .setStyleGenerator(budgetHistory -> "align-right")
                .setId(key++ + currentDate.format(DateTimeFormatter.ofPattern("MMM")))
                .setCaption("Budget");
        headerCells[1] = treeGrid.addColumn(userBooking -> NumberUtils.round(userBooking.getAmountItemsPerPrebooking(colNumber),2))
                .setStyleGenerator(budgetHistory -> "align-right")
                .setId(key++ + currentDate.format(DateTimeFormatter.ofPattern("MMM")))
                .setCaption("Prebooking");
        headerCells[2] = treeGrid.addColumn(userBooking -> NumberUtils.round(userBooking.getBookingPercentage(colNumber),2))
                .setStyleGenerator(budgetHistory -> "align-right")
                .setId(key++ + currentDate.format(DateTimeFormatter.ofPattern("MMM")))
                .setCaption("Booking (%)");
        headerCells[3] = treeGrid.addColumn(userBooking -> NumberUtils.round(userBooking.getMonthNorm(colNumber),2))
                .setStyleGenerator(budgetHistory -> "align-right")
                .setId(key++ + currentDate.format(DateTimeFormatter.ofPattern("MMM")))
                .setCaption("Month norm");
        return key;
    }

    private int createPastColumns(TreeGrid<UserBooking> treeGrid, LocalDate currentDate, int key, Grid.Column<?, ?>[] headerCells, int colNumber) {
        headerCells[0] = treeGrid.addColumn(userBooking -> NumberUtils.round(userBooking.getAmountItemsPerProjects(colNumber),2))
                .setStyleGenerator(budgetHistory -> "align-right")
                .setId(key++ + currentDate.format(DateTimeFormatter.ofPattern("MMM")))
                .setCaption("Budget");
        headerCells[1] = treeGrid.addColumn(userBooking -> NumberUtils.round(userBooking.getAmountItemsPerPrebooking(colNumber),2))
                .setStyleGenerator(budgetHistory -> "align-right")
                .setId(key++ + currentDate.format(DateTimeFormatter.ofPattern("MMM")))
                .setCaption("Actual");
        headerCells[2] = treeGrid.addColumn(userBooking -> NumberUtils.round(userBooking.getBookingPercentage(colNumber),2))
                .setStyleGenerator(budgetHistory -> "align-right")
                .setId(key++ + currentDate.format(DateTimeFormatter.ofPattern("MMM")))
                .setCaption("Performance (%)");
        headerCells[3] = treeGrid.addColumn(userBooking -> NumberUtils.round(userBooking.getMonthNorm(colNumber),2))
                .setStyleGenerator(budgetHistory -> "align-right")
                .setId(key++ + currentDate.format(DateTimeFormatter.ofPattern("MMM")))
                .setCaption("Month norm");
        return key;
    }


    private String[] getMonthNames(LocalDate localDateStart, LocalDate localDateEnd) {
        int monthPeriod = (int) ChronoUnit.MONTHS.between(localDateStart, localDateEnd)+1;
        String[] monthNames = new String[monthPeriod];
        for (int i = 0; i < monthNames.length; i++) {
            monthNames[i] = localDateStart.plusMonths(i).getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        }
        return monthNames;
    }
}
