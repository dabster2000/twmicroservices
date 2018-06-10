package dk.trustworks.invoicewebui.web.contracts.layouts;

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
import dk.trustworks.invoicewebui.jobs.ChartCacheJob;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.model.enums.TaskType;
import dk.trustworks.invoicewebui.repositories.ClientdataRepository;
import dk.trustworks.invoicewebui.repositories.ConsultantRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.services.ProjectService;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.common.Card;
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

    private final ProjectService projectService;

    private final ConsultantRepository consultantRepository;

    private final PhotoService photoService;

    private final ChartCacheJob chartCache;

    private ResponsiveRow contractRow;
    private ResponsiveRow subContractRow;

    private VerticalLayout consultantsLayout;
    private VerticalLayout projectsLayout;
    private VerticalLayout contractLayout;
    private MVerticalLayout contactInformationLayout;
    private Card usedBudgetChartCard;
    private Card burndownChartCard;
    private Card burnrateChartCard;

    private ContractFormDesign contractForm;

    private LocalDatePeriod proposedPeriod;

    @Autowired
    public ContractDetailLayout(UserRepository userRepository, ContractService contractService, ProjectService projectService, ConsultantRepository consultantRepository, PhotoService photoService, ClientdataRepository clientdataRepository, ChartCacheJob chartCache) {
        this.userRepository = userRepository;
        this.contractService = contractService;
        this.projectService = projectService;
        this.consultantRepository = consultantRepository;
        this.photoService = photoService;
        this.chartCache = chartCache;
    }

    @PostConstruct
    public void init() {
        contractRow = this.addRow();
        subContractRow = this.addRow();
    }

    public ResponsiveLayout loadContractDetails(Contract contract, NavigationBar navigationBar) {
        contract = contractService.reloadContract(contract);
        contractRow.removeAllComponents();
        proposedPeriod = new LocalDatePeriod(contract.getActiveFrom(), contract.getActiveTo());

        createNavigationBarCard(navigationBar, 12);
        createUsedBudgetCard(contract, 6);
        createBurndownCard(contract, 6);
        createBurnrateCard(contract, 12);
        createContractCard(contract, 4);
        createProjectsCard(contract, 4);
        createContactInformationCard(contract, 4);
        createConsultantsCard(contract, 12);
        updateProposedPeriod(contract);

        return this;
    }

    private void createBurndownCard(Contract contract, int width) {
        if(contract.getContractType().equals(ContractType.AMOUNT) || contract.getContractType().equals(ContractType.SKI)) {
            burndownChartCard = new Card();
            burndownChartCard.getLblTitle().setValue("Burndown");
            burndownChartCard.getContent().setHeight(350, Unit.PIXELS);
            burndownChartCard.getHlTitleBar().addComponent(
                    new MButton("refresh", event -> {
                        chartCache.refreshBurndownRateForSingleContract(contract);
                        createBurndownChart(contract);

                    })
                            .withStyleName("flat", "borderless")
                            .withFullHeight()
            );
            if(contract.getProjects().size()>0 && contract.getConsultants().size()>0) createBurndownChart(contract);
            contractRow.addColumn()
                    .withDisplayRules(12, 12, width, width)
                    .withComponent(burndownChartCard);
        }
    }

    private void createBurnrateCard(Contract contract, int width) {
        if(contract.getContractType().equals(ContractType.PERIOD)) {
            burnrateChartCard = new Card();
            burnrateChartCard.getLblTitle().setValue("Burn Rate");
            burnrateChartCard.getContent().setHeight(350, Unit.PIXELS);
            if(contract.getProjects().size()>0 && contract.getConsultants().size()>0) createBurnrateChart(contract);
            contractRow.addColumn()
                    .withDisplayRules(12, 12, width, width)
                    .withComponent(burnrateChartCard);
        }
    }

    private void createConsultantsCard(Contract contract, int width) {
        Card consultantsCard = new Card();
        consultantsCard.getLblTitle().setValue("Consultants");
        consultantsLayout = new MVerticalLayout().withWidth(100, Unit.PERCENTAGE);
        consultantsCard.getContent().addComponent(consultantsLayout);
        createConsultantList(contract);
        contractRow.addColumn()
                .withDisplayRules(12, 12, width, width)
                .withComponent(consultantsCard);
    }

    private void createContactInformationCard(Contract contract, int width) {
        Card contactInformationCard = new Card();
        contactInformationCard.getLblTitle().setValue("Contact Information");
        contactInformationCard.getContent().setHeight(350, Unit.PIXELS);
        contactInformationCard.getContent().addStyleName("v-scrollable");
        contactInformationLayout = new MVerticalLayout().withWidth(100, Unit.PERCENTAGE);
        contactInformationCard.getContent().addComponent(contactInformationLayout);
        createContactInformation(contract);
        contractRow.addColumn()
                .withDisplayRules(12, 12, width, width)
                .withComponent(contactInformationCard);
    }

    private void createUsedBudgetCard(Contract contract, int width) {
        if(contract.getContractType().equals(ContractType.AMOUNT) || contract.getContractType().equals(ContractType.SKI)) {
            usedBudgetChartCard = new Card();
            usedBudgetChartCard.getLblTitle().setValue("Used Budget");
            usedBudgetChartCard.getContent().setHeight(350, Unit.PIXELS);
            if(contract.getProjects().size()>0 && contract.getConsultants().size()>0) createUsedBudgetChartCard(contract);
            contractRow.addColumn()
                    .withDisplayRules(12, 12, width, width)
                    .withComponent(usedBudgetChartCard);
        }
    }

    private void createSubContractCard(Contract contract, int width) {
        /*
        subContractRow.removeAllComponents();
        for (Contract subContract : contract.getChildren()) {
            ContractFormDesign subContractComponent = getSubContractComponent(subContract, false);
            subContractComponent.getDfTo().setValue(subContract.getActiveTo());
            if(subContract.getContractType().equals(ContractType.AMOUNT) || contract.getContractType().equals(ContractType.SKI))
                subContractComponent.getTxtAmount().setValue(NumberConverter.formatDouble(subContract.getAmount()));
            subContractRow.addColumn()
                    .withDisplayRules(12, 12, width, width)
                    .withComponent(subContractComponent);
        }

        ContractFormDesign contractFormDesign = getSubContractComponent(contract, true);
        ResponsiveColumn newSubContractFormColumn = subContractRow.addColumn()
                .withDisplayRules(12, 12, width, width)
                .withComponent(contractFormDesign);
        contractFormDesign.getBtnCreate().addClickListener(event -> contractRow.removeComponent(newSubContractFormColumn));
        */
    }

    private void createProjectsCard(Contract contract, int width) {
        Card projectsCard = new Card();
        projectsCard.getLblTitle().setValue("Projects");
        projectsCard.getContent().setHeight(350, Unit.PIXELS);
        projectsCard.getContent().addStyleName("v-scrollable");
        projectsLayout = new MVerticalLayout().withWidth(100, Unit.PERCENTAGE);
        projectsCard.getContent().addComponent(projectsLayout);
        createProjectList(contract);
        contractRow.addColumn()
                .withDisplayRules(12, 12, width, width)
                .withComponent(projectsCard);
    }

    private void createContractCard(Contract Contract, int width) {
        contractLayout = new MVerticalLayout().withWidth(100, Unit.PERCENTAGE).withMargin(false).withSpacing(false).withFullWidth();
        createContractForm(Contract);
        contractRow.addColumn()
                .withDisplayRules(12, 12, width, width)
                .withComponent(contractLayout);
    }

    private void createNavigationBarCard(NavigationBar navigationBar, int width) {
        contractRow.addColumn().withDisplayRules(12, 12, width, width).withComponent(navigationBar);
    }

    private void createContactInformation(Contract Contract) {
        contactInformationLayout.removeAllComponents();
        Clientdata currentClientdata = Contract.getClientdata();
        if(currentClientdata != null) {
            ContactInformationRowDesign contactInformationRow = new ContactInformationRowDesign();
            contactInformationRow.getLblName().setValue(currentClientdata.getClientname());
            contactInformationRow.getLblContact().setValue(currentClientdata.getContactperson());
            contactInformationRow.getLblCVR().setValue(currentClientdata.getCvr());
            contactInformationRow.getLblEAN().setValue(currentClientdata.getEan());
            contactInformationRow.getLblStreet().setValue(currentClientdata.getStreetnamenumber());
            contactInformationRow.getLblPostalCode().setValue(currentClientdata.getPostalcode()+"");
            contactInformationRow.getLblCity().setValue(currentClientdata.getCity());
            contactInformationRow.getLblOther().setValue(currentClientdata.getOtheraddressinfo());
            contactInformationRow.getBtnChange().addClickListener(event1 -> {
                createContactInformationSelector(Contract);
            });
            contactInformationLayout.add(contactInformationRow);
        } else {
            contactInformationLayout.add(new MButton(MaterialIcons.ADD, event -> {
                createContactInformationSelector(Contract);
            }).withStyleName("friendly").withWidth(100, Unit.PERCENTAGE));
        }
    }

    private void createContactInformationSelector(Contract contract) {
        Window subWindow = new Window("Select contract Recipient");
        VerticalLayout subContent = new VerticalLayout();
        subWindow.setContent(subContent);

        for (Clientdata clientdata : contract.getClient().getClientdata()) {
            CompactContactInformationRowDesign contactInformationRow = new CompactContactInformationRowDesign();
            contactInformationRow.setStyleName("bg-grey");
            contactInformationRow.getLblName().setValue(clientdata.getClientname());
            contactInformationRow.getLblContact().setValue(clientdata.getContactperson());
            contactInformationRow.getLblCVR().setValue(clientdata.getCvr());
            contactInformationRow.getLblEAN().setValue(clientdata.getEan());
            contactInformationRow.getLblStreet().setValue(clientdata.getStreetnamenumber());
            contactInformationRow.getLblPostalCode().setValue(clientdata.getPostalcode()+"");
            contactInformationRow.getLblCity().setValue(clientdata.getCity());
            contactInformationRow.getLblOther().setValue(clientdata.getOtheraddressinfo());
            contactInformationRow.getBtnAdd().addClickListener(event1 -> {
                contract.setClientdata(clientdata);
                Contract newContract = contractService.updateContract(contract);
                subWindow.close();
                updateData(newContract);
            });
            subContent.addComponent(contactInformationRow);
        }

        subWindow.center();
        UI.getCurrent().addWindow(subWindow);
    }

    private void createContractForm(Contract contract) {
        contractLayout.removeAllComponents();
        contractForm = new ContractFormDesign();
        contractLayout.addComponent(contractForm);
        Binder<Contract> contractBinder = new Binder<>();
        contractForm.getContainer().setHeight(350, Unit.PIXELS);
        contractForm.getContainer().addStyleName("v-scrollable");

        contractForm.getBtnCreate().setVisible(false);
        contractForm.getTxtAmount().setVisible(
                contract.getContractType().equals(ContractType.AMOUNT) ||
                contract.getContractType().equals(ContractType.SKI));
        contractForm.getTxtAmount().setValue(NumberConverter.formatDouble(contract.getAmount()));
        contractBinder.forField(contractForm.getDfFrom()).bind(Contract::getActiveFrom, Contract::setActiveFrom);
        contractBinder.forField(contractForm.getDfTo()).bind(Contract::getActiveTo, Contract::setActiveTo);
        contractBinder.forField(contractForm.getTxtRefid()).bind(Contract::getRefid, Contract::setRefid);
        contractBinder.forField(contractForm.getTxtNote()).bind(Contract::getNote, Contract::setNote);
        contractForm.getCbStatus().setItems(ContractStatus.values());
        contractBinder.forField(contractForm.getCbStatus()).bind(Contract::getStatus, Contract::setStatus);
        contractForm.getCbType().setEnabled(false);
        contractBinder.forField(contractForm.getCbType()).bind(Contract::getContractType, Contract::setContractType);
        contractForm.getLblTitle().setValue("Main contract");
        contractBinder.readBean(contract);

        contractForm.getBtnUpdate().addClickListener(event -> {
            try {
                contractBinder.writeBean(contract);
                contract.setAmount(NumberConverter.parseDouble(contractForm.getTxtAmount().getValue()));
                contractService.updateContract(contract);
                updateData(contract);
            } catch (ValidationException e) {
                e.printStackTrace();
                Notification.show("Errors in form", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });
    }

    private void createUsedBudgetChartCard(Contract contract) {
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

        double sum = contractService.findAmountUsedOnContract(contract);

        conf.addSeries(new ListSeries("Remaining", (contract.getAmount()-sum)));
        conf.addSeries(new ListSeries("Used", sum));

        chart.drawChart(conf);
    }

    private void createBurndownChart(Contract contract) {
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

        Map<LocalDate, Double> runningBudget = chartCache.getBurndownDateForContract(contract);

        for (LocalDate localDate : runningBudget.keySet()) {
            DataSeriesItem item = new DataSeriesItem(localDate.atStartOfDay().toInstant(ZoneOffset.UTC),runningBudget.get(localDate));
            ls.add(item);
        }

        conf.addSeries(ls);
        chart.drawChart(conf);
    }



    private void createBurnrateChart(Contract contract) {
        //contract.getChildren().sort(Comparator.comparing(subContract -> subContract.getActiveTo()));
        LocalDate activeFrom = contract.getActiveFrom();
        LocalDate activeTo = contract.getActiveTo();
        //LocalDate activeTo = (contract.getChildren().size()>0)?contract.getChildren().get(contract.getChildren().size() - 1).getActiveTo():contract.getActiveTo();
        long between = ChronoUnit.WEEKS.between(activeFrom, activeTo);

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
        List<Work> workList = contractService.getWorkOnContractByUser(contract).stream().sorted(Comparator.comparing(Work::getYear).thenComparing(Work::getMonth).thenComparing(Work::getDay)).collect(Collectors.toList());
        for (Work work : workList) {
            if(work.getTask().getType().equals(TaskType.SO)) continue;
            Optional<Consultant> optionalConsultant = contract.getConsultants().stream().filter(consultant -> consultant.getUser().getUuid().equals(work.getUser().getUuid())).findFirst();
            if(!optionalConsultant.isPresent()) continue;
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

    private void createProjectList(Contract Contract) {
        projectsLayout.removeAllComponents();

        for (Project project : Contract.getProjects()) {
            ProjectRowDesign projectRowDesign = new ProjectRowDesign();
            projectRowDesign.getLblName().setValue(project.getName());
            projectRowDesign.getBtnIcon().setIcon(MaterialIcons.DATE_RANGE);
            projectRowDesign.getBtnDelete().setIcon(MaterialIcons.DELETE);
            projectRowDesign.getBtnDelete().addClickListener(event -> removeProject(Contract, project));
            projectsLayout.addComponent(projectRowDesign);
        }

        createProposedProjects(Contract);

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
                    projectComboBox.setPopupWidth("");
                    projectComboBox.setItems(projectService.findByClientOrderByNameAsc(Contract.getClient()));
                    projectComboBox.setItemCaptionGenerator(Project::getName);
                    subContent.addComponent(projectComboBox);
                    Button addButton = new Button("Add");
                    addButton.addClickListener(event1 -> projectComboBox.getOptionalValue().ifPresent(project -> {
                    //Project project = projectComboBox.getSelectedItem().get();
                    createProject(Contract, project);
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

    private void createProposedProjects(Contract Contract) {
        List<Project> deltaProjects = new ArrayList<>(Contract.getClient().getProjects());
        Map<String, Project> projectsWithUserWorkButNoContract = new HashMap<>();
        for (User user : Contract.getConsultants().stream().map(Consultant::getUser).collect(Collectors.toList())) {
            for (Project project : contractService.getProjectsWithUserWorkButNoContract(deltaProjects, user)) {
                // is project already on contract
                if(Contract.getProjects().stream().anyMatch(p -> p.getUuid().equals(project.getUuid()))) {
                    System.out.println("Project already exists. Trying to find work...");
                    LocalDatePeriod period = contractService.getUsersFirstAndLastWorkOnProject(project, user);
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
                createProposedProjectRow(Contract, project);
            }

        }

        Set<Project> projectsNotUnderContract = contractService.getClientProjectsNotUnderContract(Contract.getClient());
        if(projectsNotUnderContract.size() > 0) {
            projectsLayout.addComponent(new MLabel("Projects with no contract"));
            for (Project project : projectsNotUnderContract) {
                createProposedProjectRow(Contract, project);
            }
        }
    }

    private void createProposedProjectRow(Contract Contract, Project project) {
            ProjectRowDesign projectRowDesign = new ProjectRowDesign();
            projectRowDesign.getLblName().setValue(project.getName());
            projectRowDesign.getLblName().addStyleName("semi-transparent");
            projectRowDesign.getBtnIcon().setIcon(MaterialIcons.DATE_RANGE);
            projectRowDesign.getBtnIcon().addStyleName("semi-transparent");
            projectRowDesign.getBtnDelete().setIcon(MaterialIcons.ADD);
            projectRowDesign.getBtnDelete().addClickListener(event -> createProject(Contract, project));
        projectsLayout.addComponent(projectRowDesign);
    }

    private void createConsultantList(final Contract contract) {
        consultantsLayout.removeAllComponents();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ContainerType.FLUID);
        consultantsLayout.addComponent(responsiveLayout);
        ResponsiveRow responsiveRow = responsiveLayout.addRow();

        for (Consultant consultant : contract.getConsultants()) {
            ConsultantRowDesign consultantRowDesign = new ConsultantRowDesign();
            consultantRowDesign.getLblName().setValue(consultant.getUser().getFirstname() + " " + consultant.getUser().getLastname());
            consultantRowDesign.getTxtRate().setValue(Math.round(consultant.getRate())+"");
            consultantRowDesign.getTxtRate().addValueChangeListener(event -> {
                consultant.setRate(NumberConverter.parseDouble(event.getValue()));
                consultantRepository.save(consultant);
                updateData(contract);
            });
            consultantRowDesign.getTxtHours().setValue(Math.round(consultant.getHours())+"");
            consultantRowDesign.getTxtHours().addValueChangeListener(event -> {
                consultant.setHours(NumberConverter.parseDouble(event.getValue()));
                consultantRepository.save(consultant);
                updateData(contract);
            });
            consultantRowDesign.getVlHours().setVisible(consultant.getContract().getContractType().equals(ContractType.PERIOD));
            consultantRowDesign.getImgPhoto().addComponent(photoService.getRoundMemberImage(consultant.getUser(), false));

            consultantRowDesign.getBtnDelete().addClickListener(event -> removeConsultant(contract, consultant));

            responsiveRow.addColumn()
                    .withComponent(consultantRowDesign)
                    .withDisplayRules(12, 12, 6, 6);
        }

        createProposedConsultants(contract, responsiveLayout.addRow());

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
                        createConsultant(contract, user, 0.0, 0.0);
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

    private void createProposedConsultants(Contract Contract, ResponsiveRow responsiveRow) {
        HashMap<String, User> proposedUsers = new HashMap<>();
        for (Project project : Contract.getProjects()) {
            Set<User> employees = contractService.getEmployeesWorkingOnProjectWithNoContract(project);
            for (User employee : employees) {
                if(Contract.getConsultants().stream().noneMatch(consultant -> consultant.getUser().getUuid().equals(employee.getUuid())))
                    proposedUsers.put(employee.getUuid(), employee);
            }
        }

        if(proposedUsers.size() > 0) {
            responsiveRow.addColumn()
                    .withDisplayRules(12, 12, 12, 12)
                    .withComponent(new MLabel("Proposed consultants:"));
            for (User user : proposedUsers.values()) {
                createConsultantRow(Contract, responsiveRow, user);
            }
        } else if(Contract.getProjects().size() == 0) {
            for (Project project : Contract.getClient().getProjects()) {
                for (User user : contractService.getEmployeesWorkingOnProjectWithNoContract(project)) {
                    if(Contract.getConsultants().stream().noneMatch(consultant -> consultant.getUser().getUuid().equals(user.getUuid())))
                    proposedUsers.put(user.getUuid(), user);
                }
            }
            if(proposedUsers.size() > 0) {
                responsiveRow.addColumn()
                        .withDisplayRules(12, 12, 12, 12)
                        .withComponent(new MLabel("Proposed consultants:"));
                for (User user : proposedUsers.values()) {
                    createConsultantRow(Contract, responsiveRow, user);
                }
            }
        }
    }

    private void createConsultantRow(Contract Contract, ResponsiveRow responsiveRow, User user) {
        ConsultantRowDesign consultantRowDesign = new ConsultantRowDesign();
        consultantRowDesign.getHlBackground().setStyleName("bg-grey");
        consultantRowDesign.getHlNameBackground().setStyleName("dark-grey");
        consultantRowDesign.getLblName().setValue(user.getFirstname() + " " + user.getLastname());
        consultantRowDesign.getTxtRate().setValue("0");
        consultantRowDesign.getTxtHours().setValue("0");
        consultantRowDesign.getVlHours().setVisible(Contract.getContractType().equals(ContractType.PERIOD));
        consultantRowDesign.getImgPhoto().addComponent(photoService.getRoundMemberImage(user, false));
        consultantRowDesign.getImgPhoto().setEnabled(false);
        consultantRowDesign.getBtnDelete().setIcon(MaterialIcons.ADD);
        consultantRowDesign.getBtnDelete().addClickListener(event -> createConsultant(
                Contract,
                user,
                NumberConverter.parseDouble(consultantRowDesign.getTxtHours().getValue()),
                NumberConverter.parseDouble(consultantRowDesign.getTxtRate().getValue())));

        responsiveRow.addColumn()
                .withComponent(consultantRowDesign)
                .withDisplayRules(12, 12, 6, 6);
    }

    private void createProject(Contract Contract, Project project) {
        System.out.println("ContractDetailLayout.createProject");
        System.out.println("Contract = [" + Contract + "], project = [" + project + "]");
        try {
            Contract = contractService.addProject(Contract, project);
        } catch (ContractValidationException e) {
            e.printStackTrace();
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
        updateData(Contract);
    }

    private void removeConsultant(Contract Contract, Consultant consultant) {
        Contract.getConsultants().remove(consultant);
        consultantRepository.delete(consultant);
        updateData(Contract);
    }

    private void updateData(Contract contract) {
        contract = contractService.reloadContract(contract);
        createContractForm(contract);
        createConsultantList(contract);
        createProjectList(contract);
        createContactInformation(contract);
        updateProposedPeriod(contract);
        createSubContractCard(contract, 4);
        if (contract.getProjects().size() > 0 && contract.getConsultants().size() > 0) {
            if(contract.getContractType().equals(ContractType.AMOUNT) || contract.getContractType().equals(ContractType.SKI)) {
                createUsedBudgetChartCard(contract);
                createBurndownChart(contract);
            } else {
                createBurnrateChart(contract);
            }
        }
    }

    private void updateProposedPeriod(Contract contract) {
        contractForm.getCssNotification().setVisible(false);
        if(proposedPeriod.getFrom().isBefore(contract.getActiveFrom()) || proposedPeriod.getTo().isAfter(contract.getActiveTo())) {
            contractForm.getLblNotification().setValue("Consider this date range: "+proposedPeriod);
            contractForm.getCssNotification().setVisible(true);
        }
    }

    private void removeProject(Contract contract, Project project) {
        contract = contractService.removeProject(contract, project);
        updateData(contract);
    }

    private void createConsultant(Contract contract, User user, double hours, double rate) {
        Consultant consultant = new Consultant(contract, user, rate, 0.0, hours);
        contract.addConsultant(consultant);
        consultantRepository.save(consultant);
        updateData(contract);
    }

    private ContractFormDesign getSubContractComponent(Contract contract, boolean newContract) {
        /*
        Contract parentContract = (newContract)?contract:contract.getParent();
        Contract subContract = (newContract)?new Contract(ContractStatus.INACTIVE, "", 0.0, contract.getActiveTo().plusMonths(2), parentContract):contract;
        Binder<Contract> contractBinder = new Binder<>();
        ContractFormDesign contractFormDesign = new ContractFormDesign();
        contractFormDesign.getTxtAmount().setVisible(contract.getContractType().equals(ContractType.AMOUNT) || contract.getContractType().equals(ContractType.SKI));
        contractFormDesign.getDfFrom().setVisible(false);
        contractFormDesign.getCbType().setVisible(false);
        contractFormDesign.getLblTitle().setValue(newContract?"Add sub contract":"Sub Contract");
        contractFormDesign.getCbStatus().setItems(ContractStatus.values());
        contractBinder.forField(contractFormDesign.getCbStatus()).bind(Contract::getStatus, Contract::setStatus);
        contractBinder.forField(contractFormDesign.getDfTo()).bind(Contract::getActiveTo, Contract::setActiveTo);
        contractBinder.forField(contractFormDesign.getTxtNote()).bind(Contract::getNote, Contract::setNote);
        contractBinder.readBean(subContract);

        if(newContract) {
            contractFormDesign.getBtnUpdate().setVisible(false);
            contractFormDesign.getBtnCreate().setVisible(true);
            contractFormDesign.getBtnCreate().addClickListener(event -> {
                try {
                    contractBinder.writeBean(subContract);
                    contract.getChildren().add(subContract);
                    contract.setAmount(NumberConverter.parseDouble(contractFormDesign.getTxtAmount().getValue()));
                    contractService.createContract(subContract);
                    updateData(contractService.updateContract(contract));
                } catch (ValidationException e) {
                    e.printStackTrace();
                    Notification.show("Errors in form", e.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            });
        } else {
            contractFormDesign.getBtnUpdate().setVisible(true);
            contractFormDesign.getBtnCreate().setVisible(true);
            contractFormDesign.getBtnCreate().setCaption("Delete");
            contractFormDesign.getBtnCreate().addStyleName("danger");

            contractFormDesign.getBtnUpdate().addClickListener(event -> {
                try {
                    contractBinder.writeBean(subContract);
                    contract.setAmount(NumberConverter.parseDouble(contractFormDesign.getTxtAmount().getValue()));
                    contractService.updateContract(subContract);
                    updateData(contractService.reloadContract(contract));
                } catch (ValidationException e) {
                    e.printStackTrace();
                }
            });

            contractFormDesign.getBtnCreate().addClickListener(event -> {
                contractService.deleteContract(subContract);
                updateData(contractService.reloadContract(contract));
            });
        }
        return contractFormDesign;
        */
        return null;
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