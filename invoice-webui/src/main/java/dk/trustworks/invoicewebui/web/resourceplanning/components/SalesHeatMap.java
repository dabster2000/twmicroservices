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
import dk.trustworks.invoicewebui.model.BudgetNew;
import dk.trustworks.invoicewebui.model.Consultant;
import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.ContractConsultant;
import dk.trustworks.invoicewebui.model.dto.UserBooking;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.repositories.ConsultantRepository;
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

    private final BudgetNewRepository budgetNewRepository;

    private final ConsultantRepository consultantRepository;

    private final ClientRepository clientRepository;

    private final ContractService contractService;

    private final WorkService workService;

    private final PhotoService photoService;

    private final StatisticsService statisticsService;

    double[] monthTotalAvailabilites;
    double[] monthAvailabilites;

    @Autowired
    public SalesHeatMap(BudgetNewRepository budgetNewRepository, ConsultantRepository consultantRepository, ClientRepository clientRepository, ContractService contractService, WorkService workService, UserService userService, PhotoService photoService, StatisticsService statisticsService) {
        this.budgetNewRepository = budgetNewRepository;
        this.consultantRepository = consultantRepository;
        this.clientRepository = clientRepository;
        this.contractService = contractService;
        this.workService = workService;
        this.photoService = photoService;
        this.statisticsService = statisticsService;
    }

    public Component getChart(LocalDate localDateStart, LocalDate localDateEnd) {
        int monthPeriod = (int) ChronoUnit.MONTHS.between(localDateStart, localDateEnd)+1;
        monthTotalAvailabilites = new double[monthPeriod];
        monthAvailabilites = new double[monthPeriod];

        List<Consultant> consultantList = consultantRepository.findByStatus(StatusType.ACTIVE).stream().filter(consultant -> consultant.getType().equals(ConsultantType.CONSULTANT)).collect(Collectors.toList());


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

        Map<String, double[]> budgetRowList = new HashMap<>();
        Map<String, Map<String, double[]>> userAllocationPerAssignmentMap = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            LocalDate currentDate = localDateStart.plusMonths(i);

            List<Contract> contracts = contractService.findActiveContractsByDate(currentDate, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            for (Contract contract : contracts) {
                if(contract.getContractType().equals(ContractType.PERIOD)) {
                    for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                        double weeks = (workService.getWorkDaysInMonth(contractConsultant.getUser().getUuid(), currentDate) / 5.0);
                        budgetRowList.putIfAbsent(contractConsultant.getUser().getUuid(), new double[12]);
                        budgetRowList.get(contractConsultant.getUser().getUuid())[i] = (contractConsultant.getHours() * weeks) + budgetRowList.get(contractConsultant.getUser().getUuid())[i];

                        userAllocationPerAssignmentMap.putIfAbsent(contractConsultant.getUser().getUuid(), new HashMap<>());
                        userAllocationPerAssignmentMap.get(contractConsultant.getUser().getUuid()).putIfAbsent(contract.getClient().getUuid(), new double[12]);
                        userAllocationPerAssignmentMap.get(contractConsultant.getUser().getUuid()).get(contract.getClient().getUuid())[i] +=  (contractConsultant.getHours() * weeks);
                    }
                }
            }
            List<BudgetNew> budgets = budgetNewRepository.findByMonthAndYear(currentDate.getMonthValue() - 1, currentDate.getYear());
            List<BudgetNew> budgetsWithNoValidContract = new ArrayList<>();
            for (BudgetNew budget : budgets) {
                ContractConsultant contractConsultant = budget.getContractConsultant();
                LocalDate localDate = LocalDate.of(budget.getYear(), budget.getMonth()+1, 15);
                if(localDate.isAfter(contractConsultant.getContract().getActiveFrom()) && localDate.isBefore(contractConsultant.getContract().getActiveTo())) {
                    budgetRowList.putIfAbsent(contractConsultant.getUser().getUuid(), new double[12]);
                    budgetRowList.get(contractConsultant.getUser().getUuid())[i] = (budget.getBudget() / budget.getContractConsultant().getRate()) + budgetRowList.get(contractConsultant.getUser().getUuid())[i];

                    userAllocationPerAssignmentMap.putIfAbsent(contractConsultant.getUser().getUuid(), new HashMap<>());
                    userAllocationPerAssignmentMap.get(contractConsultant.getUser().getUuid()).putIfAbsent(budget.getProject().getClient().getUuid(), new double[12]);
                    userAllocationPerAssignmentMap.get(contractConsultant.getUser().getUuid()).get(budget.getProject().getClient().getUuid())[i] += (budget.getBudget() / budget.getContractConsultant().getRate());
                } else {
                    budgetsWithNoValidContract.add(budget);
                }
            }
            budgetNewRepository.delete(budgetsWithNoValidContract);
        }

        for (Consultant user : consultantList) {
            budgetRowList.putIfAbsent(user.getUuid(), new double[12]);

            LocalDate localDate = localDateStart;
            int m = 0;
            while(localDate.isBefore(localDateEnd) || localDate.isEqual(localDateEnd)) {

                int workDaysInMonth = workService.getWorkDaysInMonth(user.getUuid(), localDate);
                double budget = Math.round((workDaysInMonth * (user.getAllocation()/5.0)) - budgetRowList.get(user.getUuid())[m]);
                if(budget < 0.0) budget = 0.0;
                budget = Math.round(budget / Math.round(workDaysInMonth * (user.getAllocation()/5.0)) * 100.0);

                monthAvailabilites[m] += Math.round(budget);
                monthTotalAvailabilites[m] += 100;

                rs.addHeatPoint(m, userNumber, Math.round(budget));

                localDate = localDate.plusMonths(1);
                m++;
            }
            userNumber++;
        }

        config.getxAxis().setCategories(monthNames);

        String[] consultants = consultantList.stream().map(Consultant::getUsername).toArray(String[]::new);
        config.getyAxis().setCategories(consultants);

        chart.addPointClickListener(event -> {
            int intValue = new Double(Math.floor(event.getPointIndex() / 12.0)).intValue();
            Consultant user = consultantList.get(intValue);


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
            listSeries.addData(Math.round(monthAvailabilites[j] / monthTotalAvailabilites[j] * 100.0));
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

        List<UserBooking> userBookings = statisticsService.getUserBooking(monthsInFuture, monthsInPast);

        currentDate = LocalDate.now().withDayOfMonth(1).minusMonths(monthsInPast);

        treeGrid.setItems(userBookings, UserBooking::getSubProjects);

        HeaderRow topHeader = treeGrid.prependHeaderRow();

        treeGrid.addComponentColumn(userBooking -> {
                return new MHorizontalLayout(
                        photoService.getRoundImage(
                                userBooking.getUuid(),
                                false,
                                30,
                                Sizeable.Unit.PIXELS),
                        new Label(userBooking.getUsername()));
                }).setCaption("Name").setId("name-column");
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
