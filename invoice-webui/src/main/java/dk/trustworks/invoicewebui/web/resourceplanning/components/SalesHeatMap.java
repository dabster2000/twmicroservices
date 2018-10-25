package dk.trustworks.invoicewebui.web.resourceplanning.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.repositories.ConsultantRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.label.MLabel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    private final WorkRepository workRepository;

    double[] monthTotalAvailabilites;
    double[] monthAvailabilites;

    @Autowired
    public SalesHeatMap(BudgetNewRepository budgetNewRepository, ConsultantRepository consultantRepository, ClientRepository clientRepository, ContractService contractService, WorkRepository workRepository) {
        this.budgetNewRepository = budgetNewRepository;
        this.consultantRepository = consultantRepository;
        this.clientRepository = clientRepository;
        this.contractService = contractService;
        this.workRepository = workRepository;
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
            System.out.println("currentDate = " + currentDate);

            List<Contract> contracts = contractService.findActiveContractsByDate(currentDate, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            for (Contract contract : contracts) {
                if(contract.getContractType().equals(ContractType.PERIOD)) {
                    double weeks = currentDate.getMonth().length(true) / 7.0;
                    for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                        if(contractConsultant.getUser().getUsername().equals("elvi.nissen")) {
                            System.out.print("Client(" + contractConsultant.getContract().getClient().getName()+"): ");
                            System.out.println("hours = " + (contractConsultant.getHours() * weeks));
                        }
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
                if(contractConsultant.getUser().getUsername().equals("elvi.nissen")) {
                    System.out.print("Project (" + budget.getProject().getName()+"): ");
                    System.out.println("hours = " + (budget.getBudget() / budget.getContractConsultant().getRate()));
                }

                userAllocationPerAssignmentMap.putIfAbsent(contractConsultant.getUser().getUuid(), new HashMap<>());
                userAllocationPerAssignmentMap.get(contractConsultant.getUser().getUuid()).putIfAbsent(budget.getProject().getClient().getUuid(), new double[12]);
                userAllocationPerAssignmentMap.get(contractConsultant.getUser().getUuid()).get(budget.getProject().getClient().getUuid())[i] += (budget.getBudget() / budget.getContractConsultant().getRate());
            }
        }

        for (Consultant user : consultantList) {
            System.out.println("user.getUsername() = " + user.getUsername());
            budgetRowList.putIfAbsent(user.getUuid(), new double[12]);

            LocalDate localDate = localDateStart;
            int m = 0;
            while(localDate.isBefore(localDateEnd) || localDate.isEqual(localDateEnd)) {

                int weekDays = DateUtils.countWeekDays(localDate, localDate.plusMonths(1));
                List<Work> workList = workRepository.findByUserAndTasks(user.getUuid(), "");
                double vacationAndSickdays = workList.stream().mapToDouble(value -> value.getWorkduration()).sum() / 7.4;
                weekDays -= vacationAndSickdays;
                System.out.println("localDate = " + localDate);
                System.out.println("weekDays = " + weekDays);
                System.out.println("userStatus.getAllocation() = " + user.getAllocation());
                System.out.println("budgetRowList.get(user.getUuid())["+m+"] = " + budgetRowList.get(user.getUuid())[m]);
                double budget = Math.round((weekDays * (user.getAllocation()/5.0)) - budgetRowList.get(user.getUuid())[m]);
                System.out.println("budget = " + budget);
                if(budget < 0.0) budget = 0.0;
                budget = Math.round(budget / Math.round(weekDays * (user.getAllocation()/5.0)) * 100.0);

                System.out.println("budget = " + budget);

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


    private String[] getMonthNames(LocalDate localDateStart, LocalDate localDateEnd) {
        int monthPeriod = (int) ChronoUnit.MONTHS.between(localDateStart, localDateEnd)+1;
        String[] monthNames = new String[monthPeriod];
        for (int i = 0; i < monthNames.length; i++) {
            monthNames[i] = localDateStart.plusMonths(i).getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        }
        return monthNames;
    }
}
