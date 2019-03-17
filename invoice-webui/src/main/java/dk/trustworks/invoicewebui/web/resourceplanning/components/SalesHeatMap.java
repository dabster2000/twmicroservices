package dk.trustworks.invoicewebui.web.resourceplanning.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.components.grid.HeaderRow;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.UserBooking;
import dk.trustworks.invoicewebui.model.dto.UserProjectBooking;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.repositories.ConsultantRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.services.WorkService;
import dk.trustworks.invoicewebui.utils.DateUtils;
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

    private final UserService userService;

    private final PhotoService photoService;

    double[] monthTotalAvailabilites;
    double[] monthAvailabilites;

    @Autowired
    public SalesHeatMap(BudgetNewRepository budgetNewRepository, ConsultantRepository consultantRepository, ClientRepository clientRepository, ContractService contractService, WorkService workService, UserService userService, PhotoService photoService) {
        this.budgetNewRepository = budgetNewRepository;
        this.consultantRepository = consultantRepository;
        this.clientRepository = clientRepository;
        this.contractService = contractService;
        this.workService = workService;
        this.userService = userService;
        this.photoService = photoService;
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
        config.getColorAxis().setMaxColor(new SolidColor("#3B8937"));


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
            for (BudgetNew budget : budgets) {
                ContractConsultant contractConsultant = budget.getContractConsultant();
                budgetRowList.putIfAbsent(contractConsultant.getUser().getUuid(), new double[12]);
                budgetRowList.get(contractConsultant.getUser().getUuid())[i] = (budget.getBudget() / budget.getContractConsultant().getRate()) + budgetRowList.get(contractConsultant.getUser().getUuid())[i];

                userAllocationPerAssignmentMap.putIfAbsent(contractConsultant.getUser().getUuid(), new HashMap<>());
                userAllocationPerAssignmentMap.get(contractConsultant.getUser().getUuid()).putIfAbsent(budget.getProject().getClient().getUuid(), new double[12]);
                userAllocationPerAssignmentMap.get(contractConsultant.getUser().getUuid()).get(budget.getProject().getClient().getUuid())[i] += (budget.getBudget() / budget.getContractConsultant().getRate());
            }
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
        hover.setFillColor(new SolidColor("#0A2A3C"));
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
        xAxis.setLineColor(new SolidColor("#3B8937"));
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

        List<UserBooking> userBookings = new ArrayList<>();

        LocalDate currentDate;

        Map<String, UserProjectBooking> userProjectBookingMap = new HashMap<>();

        for (User user : userService.findCurrentlyWorkingEmployees(ConsultantType.CONSULTANT)) {
            currentDate = LocalDate.now().withDayOfMonth(1).minusMonths(monthsInPast);
            UserBooking userBooking = new UserBooking(user.getUsername(),user.getUuid(), monthsInFuture, true);
            userBookings.add(userBooking);

            boolean debug = user.getUsername().equals("hans.lassen");

            for (int i = 0; i < monthsInFuture; i++) {
                List<Contract> contracts = contractService.findActiveContractsByDate(currentDate, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
                for (Contract contract : contracts) {
                    if(debug) System.out.println("contract = " + contract);
                    if(contract.getContractType().equals(ContractType.PERIOD)) {
                        for (ContractConsultant contractConsultant : contract.getContractConsultants().stream().filter(c -> c.getUser().getUsername().equals(user.getUsername())).collect(Collectors.toList())) {
                            if(debug) System.out.println("contractConsultant = " + contractConsultant);
                            String key = contractConsultant.getUser().getUuid()+contractConsultant.getContract().getClient().getUuid();
                            if(!userProjectBookingMap.containsKey(key)) {
                                UserProjectBooking newUserProjectBooking = new UserProjectBooking(contractConsultant.getContract().getClient().getName(), contractConsultant.getContract().getClient().getUuid(), monthsInFuture, false);
                                userProjectBookingMap.put(key, newUserProjectBooking);
                                userBooking.addSubProject(newUserProjectBooking);
                            }
                            UserProjectBooking userProjectBooking = userProjectBookingMap.get(key);

                            double workDaysInMonth = workService.getWorkDaysInMonth(contractConsultant.getUser().getUuid(), currentDate);
                            if(debug) System.out.println("workDaysInMonth = " + workDaysInMonth);
                            double weeks = (workDaysInMonth / 5.0);
                            if(debug) System.out.println("weeks = " + weeks);
                            double preBooking = 0.0;
                            double budget = 0.0;
                            double booking;
                            if(i < monthsInPast) {
                                if(debug) System.out.println("PAST");
                                budget = NumberUtils.round((contractConsultant.getHours() * weeks), 2);
                                if(debug) System.out.println("budget = " + budget);
                                //preBooking = Optional.ofNullable(workService.findHoursRegisteredOnContractByPeriod(contract.getUuid(), user.getUuid(), DateUtils.getFirstDayOfMonth(currentDate), DateUtils.getLastDayOfMonth(currentDate))).orElse(0.0);
                                Double preBookingObj = workService.findHoursRegisteredOnContractByPeriod(contract.getUuid(), user.getUuid(), DateUtils.getFirstDayOfMonth(currentDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), DateUtils.getLastDayOfMonth(currentDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                                if(debug) System.out.println("preBookingObj = " + preBookingObj);
                                if(preBookingObj != null) preBooking = preBookingObj;
                                if(debug) System.out.println("preBooking = " + preBooking);
                                if(debug) System.out.println("contract.getUuid() = " + contract.getUuid());
                                if(debug) System.out.println("user.getUuid() = " + user.getUuid());
                                if(debug) System.out.println("DateUtils.getFirstDayOfMonth(currentDate) = " + DateUtils.getFirstDayOfMonth(currentDate));
                                if(debug) System.out.println("DateUtils.getLastDayOfMonth(currentDate) = " + DateUtils.getLastDayOfMonth(currentDate));
                                if(debug) System.out.println("preBooking = " + preBooking);
                                booking = NumberUtils.round((preBooking / budget) * 100.0, 2);
                                if(debug) System.out.println("booking = " + booking);
                            } else {
                                if(debug) System.out.println("FUTURE");
                                if (contract.getStatus().equals(ContractStatus.BUDGET)) {
                                    if(debug) System.out.println("BUDGET");
                                    preBooking = NumberUtils.round((contractConsultant.getHours() * weeks), 2);
                                    if(debug) System.out.println("preBooking = " + preBooking);
                                } else {
                                    if(debug) System.out.println("TIME");
                                    budget = NumberUtils.round((contractConsultant.getHours() * weeks), 2);
                                    if(debug) System.out.println("budget = " + budget);
                                }
                                booking = NumberUtils.round((budget / (workDaysInMonth * 7)) * 100.0, 2);
                                if(debug) System.out.println("booking = " + booking);
                            }

                            userProjectBooking.setAmountItemsPerProjects(budget, i);
                            userProjectBooking.setAmountItemsPerPrebooking(preBooking, i);
                            userProjectBooking.setBookingPercentage(booking, i);
                            //userProjectBooking.setMonthNorm(NumberUtils.round(workDaysInMonth * 7, 2), i);
                            if(debug) System.out.println("(workDaysInMonth * 7) = " + (workDaysInMonth * 7));
                        }
                    }
                }

                List<BudgetNew> budgets = budgetNewRepository.findByMonthAndYear(currentDate.getMonthValue() - 1, currentDate.getYear());
                for (BudgetNew budget : budgets) {
                    if(!budget.getContractConsultant().getUser().getUsername().equals(user.getUsername())) continue;

                    String key = budget.getContractConsultant().getUser().getUuid()+budget.getProject().getUuid();
                    if(!userProjectBookingMap.containsKey(key)) {
                        UserProjectBooking newUserProjectBooking = new UserProjectBooking(budget.getProject().getName() + " / " + budget.getProject().getClient().getName(), budget.getProject().getClient().getUuid(), monthsInFuture, false);
                        userProjectBookingMap.put(key, newUserProjectBooking);
                        userBooking.addSubProject(newUserProjectBooking);
                    }
                    UserProjectBooking userProjectBooking = userProjectBookingMap.get(key);

                    double workDaysInMonth = workService.getWorkDaysInMonth(user.getUuid(), currentDate);
                    double preBooking = 0.0;
                    double hourBudget = 0.0;
                    double booking;

                    if(i < monthsInPast) {
                        hourBudget = NumberUtils.round(budget.getBudget() / budget.getContractConsultant().getRate(), 2);
                        preBooking = Optional.ofNullable(workService.findHoursRegisteredOnContractByPeriod(budget.getContractConsultant().getContract().getUuid(), budget.getContractConsultant().getUser().getUuid(), DateUtils.getFirstDayOfMonth(currentDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), DateUtils.getLastDayOfMonth(currentDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).orElse(0.0);
                        booking = NumberUtils.round((preBooking / hourBudget) * 100.0, 2);
                    } else {
                        if (budget.getContractConsultant().getContract().getStatus().equals(ContractStatus.BUDGET)) {
                            preBooking = NumberUtils.round(budget.getBudget() / budget.getContractConsultant().getRate(), 2);
                        } else {
                            hourBudget = NumberUtils.round(budget.getBudget() / budget.getContractConsultant().getRate(), 2);
                        }
                        booking = NumberUtils.round(((hourBudget) / (workDaysInMonth * 7)) * 100.0, 2);
                    }

                    userProjectBooking.setAmountItemsPerProjects(hourBudget, i);
                    userProjectBooking.setAmountItemsPerPrebooking(preBooking, i);
                    userProjectBooking.setBookingPercentage(booking, i);
                    //userProjectBooking.setMonthNorm(NumberUtils.round(workDaysInMonth * 7,2), i);
                }

                currentDate = currentDate.plusMonths(1);
            }
        }

        System.out.println("SUM PROJECTS");
        for(UserBooking userBooking : userBookings) {
            if(userBooking.getSubProjects().size() == 0) continue;
            boolean debug = (userBooking.getUsername().equals("hans.lassen"));
            for (UserBooking subProject : userBooking.getSubProjects()) {
                currentDate = LocalDate.now().withDayOfMonth(1).minusMonths(monthsInPast);
                for (int i = 0; i < monthsInFuture; i++) {
                    if(debug) System.out.println("i = " + i);
                    userBooking.addAmountItemsPerProjects(subProject.getAmountItemsPerProjects(i), i);
                    userBooking.addAmountItemsPerPrebooking(subProject.getAmountItemsPerPrebooking(i), i);
                    int workDaysInMonth = workService.getWorkDaysInMonth(userService.findByUsername(userBooking.getUsername()).getUuid(), currentDate);
                    userBooking.setMonthNorm(NumberUtils.round(workDaysInMonth * 7, 2), i);
                    subProject.setMonthNorm(NumberUtils.round(workDaysInMonth * 7, 2), i);
                    currentDate = currentDate.plusMonths(1);
                }
            }

            for (int i = 0; i < monthsInFuture; i++) {
                if(i < monthsInPast) {
                    userBooking.setBookingPercentage(NumberUtils.round((userBooking.getAmountItemsPerPrebooking(i) / userBooking.getAmountItemsPerProjects(i)) * 100.0, 2), i);
                } else {
                    if (userBooking.getMonthNorm(i) > 0.0)
                        userBooking.setBookingPercentage(NumberUtils.round((userBooking.getAmountItemsPerProjects(i) / (userBooking.getMonthNorm(i))) * 100.0, 2), i);
                }
            }
        }

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
