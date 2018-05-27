package dk.trustworks.invoicewebui.web.contracts.layouts;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.exceptions.ContractValidationException;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.ConsultantRepository;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.contracts.components.*;
import dk.trustworks.invoicewebui.web.model.LocalDatePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.Collectors;

@SpringComponent
@SpringUI
public class ContractDetailLayout extends ResponsiveLayout {

    private final UserRepository userRepository;

    private final ContractService contractService;

    private final ProjectRepository projectRepository;

    private final ConsultantRepository consultantRepository;

    private final PhotoService photoService;

    private ResponsiveRow contractRow;

    private VerticalLayout consultantsLayout;
    private VerticalLayout projectsLayout;
    private VerticalLayout contractLayout;
    private Card usedBudgetChartCard;
    private Card burndownChartCard;
    private Card burnrateChartCard;

    private ContractFormDesign mainContractForm;

    private LocalDatePeriod proposedPeriod;

    @Autowired
    public ContractDetailLayout(UserRepository userRepository, ContractService contractService, ProjectRepository projectRepository, ConsultantRepository consultantRepository, PhotoService photoService) {
        this.userRepository = userRepository;
        this.contractService = contractService;
        this.projectRepository = projectRepository;
        this.consultantRepository = consultantRepository;
        this.photoService = photoService;
    }

    @PostConstruct
    public void init() {
        contractRow = this.addRow();
    }

    public ResponsiveLayout loadContractDetails(MainContract mainContract, NavigationBar navigationBar) {
        contractRow.removeAllComponents();

        contractRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(navigationBar);

        proposedPeriod = new LocalDatePeriod(mainContract.getActiveFrom(), mainContract.getActiveTo());

        contractLayout = new MVerticalLayout().withWidth(100, Unit.PERCENTAGE).withMargin(false).withSpacing(false).withFullWidth();
        createContractForm(mainContract);

        contractRow.addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(contractLayout);

        Card projectsCard = new Card();
        projectsCard.getLblTitle().setValue("Projects");
        projectsCard.getContent().setHeight(350, Unit.PIXELS);
        projectsCard.getContent().addStyleName("v-scrollable");
        projectsLayout = new MVerticalLayout().withWidth(100, Unit.PERCENTAGE);
        projectsCard.getContent().addComponent(projectsLayout);

        createProjectList(mainContract);

        contractRow.addColumn()
                .withDisplayRules(12, 12, 5, 5)
                .withComponent(projectsCard);

        if(mainContract.getContractType().equals(ContractType.AMOUNT) || mainContract.getContractType().equals(ContractType.SKI)) {
            usedBudgetChartCard = new Card();
            usedBudgetChartCard.getLblTitle().setValue("Used Budget");
            usedBudgetChartCard.getContent().setHeight(350, Unit.PIXELS);
            if(mainContract.getProjects().size()>0 && mainContract.getConsultants().size()>0) createUsedBudgetChartCard(mainContract);
            contractRow.addColumn()
                    .withDisplayRules(12, 12, 3, 3)
                    .withComponent(usedBudgetChartCard);
        }

        if(mainContract.getContractType().equals(ContractType.PERIOD)) {
            burnrateChartCard = new Card();
            burnrateChartCard.getLblTitle().setValue("Burn Rate");
            burnrateChartCard.getContent().setHeight(350, Unit.PIXELS);
            if(mainContract.getProjects().size()>0 && mainContract.getConsultants().size()>0) createBurnrateCard(mainContract);
            contractRow.addColumn()
                    .withDisplayRules(12, 12, 3, 3)
                    .withComponent(burnrateChartCard);
        }

        Card consultantsCard = new Card();
        consultantsCard.getLblTitle().setValue("Consultants");
        consultantsLayout = new MVerticalLayout().withWidth(100, Unit.PERCENTAGE);
        consultantsCard.getContent().addComponent(consultantsLayout);

        createConsultantList(mainContract);

        contractRow.addColumn()
                .withDisplayRules(12, 12, 9, 9)
                .withComponent(consultantsCard);

        if(mainContract.getContractType().equals(ContractType.AMOUNT) || mainContract.getContractType().equals(ContractType.SKI)) {
            burndownChartCard = new Card();
            burndownChartCard.getLblTitle().setValue("Burndown");
            burndownChartCard.getContent().setHeight(350, Unit.PIXELS);
            if(mainContract.getProjects().size()>0 && mainContract.getConsultants().size()>0) createBurndownCard(mainContract);
            contractRow.addColumn()
                    .withDisplayRules(12, 12, 3, 3)
                    .withComponent(burndownChartCard);
        }

        for (SubContract subContract : mainContract.getChildren()) {
            ContractFormDesign subContractComponent = getSubContractComponent(subContract, false);
            subContractComponent.getDfTo().setValue(subContract.getActiveTo());
            if(subContract.getContractType().equals(ContractType.AMOUNT) || mainContract.getContractType().equals(ContractType.SKI))
                subContractComponent.getTxtAmount().setValue(NumberConverter.formatDouble(subContract.getAmount()));
            contractRow.addColumn()
                    .withDisplayRules(12, 12, 3, 3)
                    .withComponent(subContractComponent);
        }

        ContractFormDesign contractFormDesign = getSubContractComponent(mainContract, true);
        ResponsiveColumn newSubContractFormColumn = contractRow.addColumn()
                .withDisplayRules(12, 12, 3, 3)
                .withComponent(contractFormDesign);
        contractFormDesign.getBtnCreate().addClickListener(event -> contractRow.removeComponent(newSubContractFormColumn));

        updateProposedPeriod(mainContract);

        return this;
    }

    private void createContractForm(MainContract mainContract) {
        contractLayout.removeAllComponents();
        mainContractForm = new ContractFormDesign();
        contractLayout.addComponent(mainContractForm);
        Binder<MainContract> mainContractBinder = new Binder<>();
        mainContractForm.getContainer().setHeight(350, Unit.PIXELS);
        mainContractForm.getContainer().addStyleName("v-scrollable");

        mainContractForm.getBtnCreate().setVisible(false);
        mainContractForm.getTxtAmount().setVisible(
                mainContract.getContractType().equals(ContractType.AMOUNT) ||
                mainContract.getContractType().equals(ContractType.SKI));
        mainContractForm.getTxtAmount().setValue(NumberConverter.formatDouble(mainContract.getAmount()));
        mainContractBinder.forField(mainContractForm.getDfFrom()).bind(MainContract::getActiveFrom, MainContract::setActiveFrom);
        mainContractBinder.forField(mainContractForm.getDfTo()).bind(Contract::getActiveTo, Contract::setActiveTo);
        mainContractBinder.forField(mainContractForm.getTxtNote()).bind(Contract::getNote, Contract::setNote);
        mainContractForm.getCbStatus().setItems(ContractStatus.values());
        mainContractBinder.forField(mainContractForm.getCbStatus()).bind(Contract::getStatus, Contract::setStatus);
        mainContractForm.getCbType().setEnabled(false);
        mainContractBinder.forField(mainContractForm.getCbType()).bind(Contract::getContractType, Contract::setContractType);
        mainContractForm.getLblTitle().setValue("Main Contract");
        mainContractBinder.readBean(mainContract);

        mainContractForm.getBtnUpdate().addClickListener(event -> {
            try {
                mainContractBinder.writeBean(mainContract);
                mainContract.setAmount(NumberConverter.parseDouble(mainContractForm.getTxtAmount().getValue()));
                contractService.updateContract(mainContract);
                updateData(mainContract);
            } catch (ValidationException e) {
                e.printStackTrace();
                Notification.show("Errors in form", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });
    }

    private void createUsedBudgetChartCard(MainContract mainContract) {
        Chart chart = new Chart(ChartType.COLUMN);
        usedBudgetChartCard.getContent().removeAllComponents();
        usedBudgetChartCard.getContent().addComponent(chart);
        chart.setSizeFull();

        Configuration conf = chart.getConfiguration();

        conf.setTitle("");
        conf.setSubTitle("");

        XAxis xAxis = new XAxis();
        xAxis.setVisible(false);
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setVisible(false);
        conf.addyAxis(yAxis);

        Legend legend = new Legend();
        legend.setEnabled(false);
        conf.setLegend(legend);

        PlotOptionsColumn plotOptions = new PlotOptionsColumn();
        plotOptions.setStacking(Stacking.NORMAL);
        DataLabels labels = new DataLabels(true);
        Style style=new Style();
        style.setTextShadow("0 0 3px black");
        labels.setStyle(style);
        labels.setColor(new SolidColor("white"));
        plotOptions.setDataLabels(labels);
        conf.setPlotOptions(plotOptions);

        double sum = 0.0;
        for (Work work : contractService.getWorkOnContractByUser(mainContract)) {
            Optional<Consultant> optionalConsultant = mainContract.getConsultants().stream().filter(consultant -> consultant.getUser().getUuid().equals(work.getUser().getUuid())).findFirst();
            if(!optionalConsultant.isPresent()) continue;
            sum += (work.getWorkduration() * optionalConsultant.get().getRate());
        }

        conf.addSeries(new ListSeries("Remaining", (mainContract.getAmount()-sum)));
        conf.addSeries(new ListSeries("Used", sum));

        chart.drawChart(conf);
    }

    private void createBurndownCard(MainContract mainContract) {
        Chart chart = new Chart(ChartType.AREA);
        burndownChartCard.getContent().removeAllComponents();
        burndownChartCard.getContent().addComponent(chart);
        chart.setSizeFull();

        Configuration conf = chart.getConfiguration();

        conf.setTitle("");
        conf.setSubTitle("");

        XAxis xAxis = new XAxis();
        xAxis.setType(AxisType.DATETIME);
        xAxis.setDateTimeLabelFormats(
                new DateTimeLabelFormats("%e. %b", "%b"));
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setVisible(false);
        conf.addyAxis(yAxis);

        Legend legend = new Legend();
        legend.setEnabled(false);
        conf.setLegend(legend);

        PlotOptionsArea plotOptions = new PlotOptionsArea();
        Marker marker = new Marker();
        marker.setEnabled(false);
        marker.setSymbol(MarkerSymbolEnum.CIRCLE);
        marker.setRadius(2);
        States states = new States();
        states.setHover(new Hover(true));
        marker.setStates(states);
        plotOptions.setMarker(marker);
        conf.setPlotOptions(plotOptions);

        DataSeries ls = new DataSeries();

        Map<LocalDate, Double> runningBudget = new TreeMap<>();
        double budget = mainContract.getAmount();
        for (Work work : contractService.getWorkOnContractByUser(mainContract).stream().sorted(Comparator.comparing(Work::getYear).thenComparing(Work::getMonth).thenComparing(Work::getDay)).collect(Collectors.toList())) {
            Optional<Consultant> optionalConsultant = mainContract.getConsultants().stream().filter(consultant -> consultant.getUser().getUuid().equals(work.getUser().getUuid())).findFirst();
            if(!optionalConsultant.isPresent()) continue;
            budget -= (work.getWorkduration() * optionalConsultant.get().getRate());
            LocalDate workDate = LocalDate.of(work.getYear(), work.getMonth()+1, work.getDay());
            runningBudget.put(workDate, budget);
        }

        for (LocalDate localDate : runningBudget.keySet()) {
            DataSeriesItem item = new DataSeriesItem(localDate.atStartOfDay().toInstant(ZoneOffset.UTC),runningBudget.get(localDate));
            ls.add(item);
        }

        conf.addSeries(ls);
        chart.drawChart(conf);
    }

    private void createBurnrateCard(MainContract mainContract) {
        Chart chart = new Chart(ChartType.SPLINE);
        burnrateChartCard.getContent().removeAllComponents();
        burnrateChartCard.getContent().addComponent(chart);
        chart.setSizeFull();

        Configuration conf = chart.getConfiguration();

        conf.setTitle("");
        conf.setSubTitle("");

        XAxis xAxis = new XAxis();
        xAxis.setType(AxisType.DATETIME);
        xAxis.setDateTimeLabelFormats(
                new DateTimeLabelFormats("%e. %b", "%b"));
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setVisible(true);
        conf.addyAxis(yAxis);

        Legend legend = new Legend();
        legend.setEnabled(false);
        conf.setLegend(legend);

        PlotOptionsArea plotOptions = new PlotOptionsArea();
        Marker marker = new Marker();
        marker.setEnabled(false);
        marker.setSymbol(MarkerSymbolEnum.CIRCLE);
        marker.setRadius(2);
        States states = new States();
        states.setHover(new Hover(true));
        marker.setStates(states);
        plotOptions.setMarker(marker);
        conf.setPlotOptions(plotOptions);

        Map<User, Map<LocalDate, Double>> userMapMap = new HashMap<>();
        //Map<LocalDate, Double> runningBudget = new TreeMap<>();
        //double budget = mainContract.getAmount();
        long between = ChronoUnit.WEEKS.between(mainContract.getActiveFrom(), mainContract.getActiveTo());
        String unit = "weeks";
        TemporalAdjuster temporalAdjuster = TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY);
        if(between > 26) {
            unit = "months";
            temporalAdjuster = TemporalAdjusters.firstDayOfMonth();
        }
        if(between > 104) {
            unit = "quarters";
            temporalAdjuster = new FirstDayOfQuarter();
        }
        for (Work work : contractService.getWorkOnContractByUser(mainContract).stream().sorted(Comparator.comparing(Work::getYear).thenComparing(Work::getMonth).thenComparing(Work::getDay)).collect(Collectors.toList())) {
            Optional<Consultant> optionalConsultant = mainContract.getConsultants().stream().filter(consultant -> consultant.getUser().getUuid().equals(work.getUser().getUuid())).findFirst();
            if(!optionalConsultant.isPresent()) continue;
            //budget -= (work.getWorkduration() * optionalConsultant.get().getRate());
            LocalDate workDate = LocalDate.of(work.getYear(), work.getMonth()+1, work.getDay()).with(temporalAdjuster);
            Map<LocalDate, Double> runningBudget;
            if(!userMapMap.containsKey(work.getUser())) {
                userMapMap.put(work.getUser(), new TreeMap<>());
            }
            runningBudget = userMapMap.get(work.getUser());
            double workDuration = 0.0;
            if(!runningBudget.containsKey(workDate)) {
                runningBudget.put(workDate, workDuration);
            }
            workDuration = runningBudget.get(workDate) + work.getWorkduration();
            runningBudget.put(workDate, workDuration);
        }
        if(unit.equals("months")) {
            for (User user : userMapMap.keySet()) {
                Map<LocalDate, Double> doubleMap = userMapMap.get(user);
                for (LocalDate localDate : doubleMap.keySet()) {
                    double aDouble = doubleMap.get(localDate);
                    double length = Month.from(localDate).length(true) / 7.0;
                    doubleMap.put(localDate, aDouble / length);
                }
            }
        }
        if(unit.equals("quarters")) {
            for (User user : userMapMap.keySet()) {
                Map<LocalDate, Double> doubleMap = userMapMap.get(user);
                for (LocalDate localDate : doubleMap.keySet()) {
                    double aDouble = doubleMap.get(localDate);
                    double length = (Month.from(localDate).length(true)
                            + Month.from(localDate.plusMonths(1)).length(true)
                            + Month.from(localDate.plusMonths(2)).length(true))/ 7.0;
                    doubleMap.put(localDate, aDouble / length);
                }
            }
        }
        for (User user : userMapMap.keySet()) {
            Map<LocalDate, Double> runningBudget = userMapMap.get(user);
            DataSeries ls = new DataSeries(user.getFirstname()+" "+user.getLastname());
            for (LocalDate localDate : runningBudget.keySet()) {
                DataSeriesItem item = new DataSeriesItem(localDate.atStartOfDay().toInstant(ZoneOffset.UTC),runningBudget.get(localDate));
                ls.add(item);
            }
            conf.addSeries(ls);
        }

        chart.drawChart(conf);
    }

    private void createProjectList(MainContract mainContract) {
        projectsLayout.removeAllComponents();

        for (Project project : mainContract.getProjects()) {
            ProjectRowDesign projectRowDesign = new ProjectRowDesign();
            projectRowDesign.getLblName().setValue(project.getName());
            projectRowDesign.getBtnIcon().setIcon(MaterialIcons.DATE_RANGE);
            projectRowDesign.getBtnDelete().setIcon(MaterialIcons.DELETE);
            projectRowDesign.getBtnDelete().addClickListener(event -> removeProject(mainContract, project));
            projectsLayout.addComponent(projectRowDesign);
        }

        createProposedProjects(mainContract);

        projectsLayout.addComponent(new MButton(
                VaadinIcons.PLUS,
                event -> {
                    Window subWindow = new Window("");
                    VerticalLayout subContent = new VerticalLayout();
                    subWindow.setContent(subContent);

                    // Put some components in it
                    subContent.addComponent(new Label("Add project"));
                    ComboBox<Project> projectComboBox = new ComboBox<>();
                    projectComboBox.setWidth(250, Unit.PIXELS);
                    projectComboBox.setItems(projectRepository.findByClientOrderByNameAsc(mainContract.getClient()));
                    projectComboBox.setItemCaptionGenerator(Project::getName);
                    subContent.addComponent(projectComboBox);
                    Button addButton = new Button("Add");
                    addButton.addClickListener(event1 -> projectComboBox.getOptionalValue().ifPresent(project -> {
                    //Project project = projectComboBox.getSelectedItem().get();
                    createProject(mainContract, project);
                    subWindow.close();
                    }));
                    subContent.addComponent(addButton);

                    // Center it in the browser window
                    subWindow.center();

                    // Open it in the UI
                    UI.getCurrent().addWindow(subWindow);
                })
                .withWidth(100, Unit.PERCENTAGE)
                .withStyleName("huge icon-only friendly")
        );
    }

    private void createProposedProjects(MainContract mainContract) {
        List<Project> deltaProjects = new ArrayList<>(mainContract.getClient().getProjects());
        //deltaProjects.removeAll(mainContract.getProjects());
        Map<String, Project> projectsWithUserWorkButNoContract = new HashMap<>();
        for (User user : mainContract.getConsultants().stream().map(Consultant::getUser).collect(Collectors.toList())) {
            for (Project project : contractService.getProjectsWithUserWorkButNoContract(deltaProjects, user)) {
                // is project already on contract
                if(mainContract.getProjects().stream().anyMatch(p -> p.getUuid().equals(project.getUuid()))) {
                    System.out.println("Project already exists. Trying to find work...");
                    LocalDatePeriod period = contractService.getUsersFirstAndLastWorkOnProject(project, user);
                    System.out.println("period = " + period);
                    if(period.getFrom().isBefore(proposedPeriod.getFrom())) proposedPeriod.setFrom(period.getFrom().minusMonths(1));
                    if(period.getTo().isAfter(proposedPeriod.getTo())) proposedPeriod.setTo(period.getTo().plusMonths(1));
                } else {
                    projectsWithUserWorkButNoContract.put(project.getUuid(), project);
                }
            }
        }
        if(projectsWithUserWorkButNoContract.size() > 0) {
            projectsLayout.addComponent(new MLabel("Proposed projects"));
            for (Project project : projectsWithUserWorkButNoContract.values()) {
                createProposedProjectRow(mainContract, project);
            }

        }

        Set<Project> projectsNotUnderContract = contractService.getClientProjectsNotUnderContract(mainContract.getClient());
        if(projectsNotUnderContract.size() > 0) {
            projectsLayout.addComponent(new MLabel("Projects with no contract"));
            for (Project project : projectsNotUnderContract) {
                createProposedProjectRow(mainContract, project);
            }
        }
    }

    private void createProposedProjectRow(MainContract mainContract, Project project) {
            ProjectRowDesign projectRowDesign = new ProjectRowDesign();
            projectRowDesign.getLblName().setValue(project.getName());
            projectRowDesign.getLblName().addStyleName("semi-transparent");
            projectRowDesign.getBtnIcon().setIcon(MaterialIcons.DATE_RANGE);
            projectRowDesign.getBtnIcon().addStyleName("semi-transparent");
            projectRowDesign.getBtnDelete().setIcon(MaterialIcons.ADD);
            projectRowDesign.getBtnDelete().addClickListener(event -> createProject(mainContract, project));
        projectsLayout.addComponent(projectRowDesign);
    }

    private void createConsultantList(final MainContract mainContract) {
        consultantsLayout.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ContainerType.FLUID);
        consultantsLayout.addComponent(responsiveLayout);
        ResponsiveRow responsiveRow = responsiveLayout.addRow();

        for (Consultant consultant : mainContract.getConsultants()) {
            ConsultantRowDesign consultantRowDesign = new ConsultantRowDesign();
            consultantRowDesign.getLblName().setValue(consultant.getUser().getFirstname() + " " + consultant.getUser().getLastname());
            consultantRowDesign.getTxtRate().setValue(Math.round(consultant.getRate())+"");
            consultantRowDesign.getTxtRate().addValueChangeListener(event -> {
                consultant.setRate(NumberConverter.parseDouble(event.getValue()));
                consultantRepository.save(consultant);
                updateData(mainContract);
            });
            consultantRowDesign.getTxtHours().setValue(Math.round(consultant.getHours())+"");
            consultantRowDesign.getTxtHours().addValueChangeListener(event -> {
                consultant.setHours(NumberConverter.parseDouble(event.getValue()));
                consultantRepository.save(consultant);
                updateData(mainContract);
            });
            consultantRowDesign.getVlHours().setVisible(consultant.getMainContract().getContractType().equals(ContractType.PERIOD));
            consultantRowDesign.getImgPhoto().addComponent(photoService.getRoundMemberImage(consultant.getUser(), false));

            consultantRowDesign.getBtnDelete().addClickListener(event -> removeConsultant(mainContract, consultant));

            responsiveRow.addColumn()
                    .withComponent(consultantRowDesign)
                    .withDisplayRules(12, 12, 6, 6);
        }

        createProposedConsultants(mainContract, responsiveLayout.addRow());

        consultantsLayout.addComponent(new MButton(
                VaadinIcons.PLUS,
                event -> {
                    Window subWindow = new Window("");
                    VerticalLayout subContent = new VerticalLayout();
                    subWindow.setContent(subContent);

                    // Put some components in it
                    subContent.addComponent(new Label("Add consultant"));
                    ComboBox<User> userComboBox = new ComboBox<>();
                    userComboBox.setItems(userRepository.findByOrderByUsername());
                    userComboBox.setItemCaptionGenerator(User::getUsername);
                    subContent.addComponent(userComboBox);
                    Button addButton = new Button("Add");
                    addButton.addClickListener(event1 -> userComboBox.getOptionalValue().ifPresent(user -> {
                        //User user = userComboBox.getSelectedItem().get();
                        createConsultant(mainContract, user, 0.0, 0.0);
                        subWindow.close();
                    }));
                    subContent.addComponent(addButton);

                    // Center it in the browser window
                    subWindow.center();

                    // Open it in the UI
                    UI.getCurrent().addWindow(subWindow);
                })
                .withWidth(100, Unit.PERCENTAGE)
                .withStyleName("huge icon-only friendly")
        );
    }

    private void createProposedConsultants(MainContract mainContract, ResponsiveRow responsiveRow) {
        HashMap<String, User> proposedUsers = new HashMap<>();
        for (Project project : mainContract.getProjects()) {
            Set<User> employees = contractService.getEmployeesWorkingOnProjectWithNoContract(project);
            for (User employee : employees) {
                if(mainContract.getConsultants().stream().noneMatch(consultant -> consultant.getUser().getUuid().equals(employee.getUuid())))
                    proposedUsers.put(employee.getUuid(), employee);
            }
        }

        if(proposedUsers.size() > 0) {
            responsiveRow.addColumn()
                    .withDisplayRules(12, 12, 12, 12)
                    .withComponent(new MLabel("Proposed consultants:"));
            for (User user : proposedUsers.values()) {
                createConsultantRow(mainContract, responsiveRow, user);
            }
        } else if(mainContract.getProjects().size() == 0) {
            for (Project project : mainContract.getClient().getProjects()) {
                for (User user : contractService.getEmployeesWorkingOnProjectWithNoContract(project)) {
                    if(mainContract.getConsultants().stream().noneMatch(consultant -> consultant.getUser().getUuid().equals(user.getUuid())))
                    proposedUsers.put(user.getUuid(), user);
                }
            }
            if(proposedUsers.size() > 0) {
                responsiveRow.addColumn()
                        .withDisplayRules(12, 12, 12, 12)
                        .withComponent(new MLabel("Proposed consultants:"));
                for (User user : proposedUsers.values()) {
                    createConsultantRow(mainContract, responsiveRow, user);
                }
            }
        }
    }

    private void createConsultantRow(MainContract mainContract, ResponsiveRow responsiveRow, User user) {
        ConsultantRowDesign consultantRowDesign = new ConsultantRowDesign();
        consultantRowDesign.getHlBackground().setStyleName("bg-grey");
        consultantRowDesign.getHlNameBackground().setStyleName("dark-grey");
        consultantRowDesign.getLblName().setValue(user.getFirstname() + " " + user.getLastname());
        consultantRowDesign.getTxtRate().setValue("0");
        consultantRowDesign.getTxtHours().setValue("0");
        consultantRowDesign.getVlHours().setVisible(mainContract.getContractType().equals(ContractType.PERIOD));
        consultantRowDesign.getImgPhoto().addComponent(photoService.getRoundMemberImage(user, false));
        consultantRowDesign.getImgPhoto().setEnabled(false);
        consultantRowDesign.getBtnDelete().setIcon(MaterialIcons.ADD);
        consultantRowDesign.getBtnDelete().addClickListener(event -> createConsultant(
                mainContract,
                user,
                NumberConverter.parseDouble(consultantRowDesign.getTxtHours().getValue()),
                NumberConverter.parseDouble(consultantRowDesign.getTxtRate().getValue())));

        responsiveRow.addColumn()
                .withComponent(consultantRowDesign)
                .withDisplayRules(12, 12, 6, 6);
    }

    private void createProject(MainContract mainContract, Project project) {
        System.out.println("ContractDetailLayout.createProject");
        System.out.println("mainContract = [" + mainContract + "], project = [" + project + "]");
        try {
            mainContract = contractService.addProject(mainContract, project);
        } catch (ContractValidationException e) {
            e.printStackTrace();
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
        updateData(mainContract);
    }

    private void removeConsultant(MainContract mainContract, Consultant consultant) {
        mainContract.getConsultants().remove(consultant);
        consultantRepository.delete(consultant);
        updateData(mainContract);
    }

    private void updateData(MainContract mainContract) {
        mainContract = contractService.getUpdatedContract(mainContract);
        createContractForm(mainContract);
        createConsultantList(mainContract);
        createProjectList(mainContract);
        updateProposedPeriod(mainContract);
        if (mainContract.getProjects().size() > 0 && mainContract.getConsultants().size() > 0) {
            if(mainContract.getContractType().equals(ContractType.AMOUNT) || mainContract.getContractType().equals(ContractType.SKI)) {
                createUsedBudgetChartCard(mainContract);
                createBurndownCard(mainContract);
            } else {
                createBurnrateCard(mainContract);
            }
        }
    }

    private void updateProposedPeriod(MainContract mainContract) {
        mainContractForm.getCssNotification().setVisible(false);
        if(proposedPeriod.getFrom().isBefore(mainContract.getActiveFrom()) || proposedPeriod.getTo().isAfter(mainContract.getActiveTo())) {
            mainContractForm.getLblNotification().setValue("Consider this date range: "+proposedPeriod);
            mainContractForm.getCssNotification().setVisible(true);
        }
    }

    private void removeProject(MainContract mainContract, Project project) {
        mainContract = contractService.removeProject(mainContract, project);
        updateData(mainContract);
    }

    private void createConsultant(MainContract mainContract, User user, double hours, double rate) {
        Consultant consultant = new Consultant(mainContract, user, rate, 0.0, hours);
        mainContract.addConsultant(consultant);
        consultantRepository.save(consultant);
        updateData(mainContract);
    }

    private ContractFormDesign getSubContractComponent(Contract mainContract, boolean newContract) {
        ContractFormDesign contractFormDesign = new ContractFormDesign();
        contractFormDesign.getBtnCreate().setVisible(newContract);
        contractFormDesign.getBtnUpdate().setVisible(!newContract);
        contractFormDesign.getTxtAmount().setVisible(mainContract.getContractType().equals(ContractType.AMOUNT) || mainContract.getContractType().equals(ContractType.SKI));
        contractFormDesign.getDfFrom().setVisible(false);
        contractFormDesign.getCbType().setVisible(false);
        contractFormDesign.getLblTitle().setValue(newContract?"Add sub contract":"Sub Contract");
        return contractFormDesign;
    }
}

class FirstDayOfQuarter implements TemporalAdjuster {

    @Override
    public Temporal adjustInto(Temporal temporal) {

        int currentQuarter = YearMonth.from(temporal).get(
                IsoFields.QUARTER_OF_YEAR);

        if (currentQuarter == 1) {

            return LocalDate.from(temporal).with(
                    TemporalAdjusters.firstDayOfYear());

        } else if (currentQuarter == 2) {

            return LocalDate.from(temporal).withMonth(Month.APRIL.getValue())
                    .with(TemporalAdjusters.firstDayOfMonth());

        } else if (currentQuarter == 3) {

            return LocalDate.from(temporal).withMonth(Month.JULY.getValue())
                    .with(TemporalAdjusters.firstDayOfMonth());

        } else {

            return LocalDate.from(temporal).withMonth(Month.OCTOBER.getValue())
                    .with(TemporalAdjusters.firstDayOfMonth());

        }
    }
}