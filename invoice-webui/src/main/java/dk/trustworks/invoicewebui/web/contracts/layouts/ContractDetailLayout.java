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
import dk.trustworks.invoicewebui.web.contracts.components.Card;
import dk.trustworks.invoicewebui.web.contracts.components.ConsultantRowDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ContractFormDesign;
import dk.trustworks.invoicewebui.web.contracts.components.ProjectRowDesign;
import dk.trustworks.invoicewebui.web.model.LocalDatePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.MLabel;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
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
    private Card chartCard;

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

    public ResponsiveLayout loadContractDetails(MainContract mainContract) {
        contractRow.removeAllComponents();
        proposedPeriod = new LocalDatePeriod(mainContract.getActiveFrom(), mainContract.getActiveTo());

        Binder<MainContract> mainContractBinder = new Binder<>();
        mainContractForm = new ContractFormDesign();
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

        contractRow.addColumn()
                .withDisplayRules(12, 12, 5, 4)
                .withComponent(mainContractForm);

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
            chartCard = new Card();
            chartCard.getLblTitle().setValue("Used Budget");
            chartCard.getContent().setHeight(350, Unit.PIXELS);
            if(mainContract.getProjects().size()>0 && mainContract.getConsultants().size()>0) createUsedBudgetChartCard(mainContract);
            contractRow.addColumn()
                    .withDisplayRules(12, 12, 2, 3)
                    .withComponent(chartCard);
        }

        Card consultantsCard = new Card();
        consultantsCard.getLblTitle().setValue("Consultants");
        consultantsLayout = new MVerticalLayout().withWidth(100, Unit.PERCENTAGE);
        consultantsCard.getContent().addComponent(consultantsLayout);

        createConsultantList(mainContract);

        contractRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(consultantsCard);

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
        contractFormDesign.getBtnCreate().addClickListener(event -> {
            contractRow.removeComponent(newSubContractFormColumn);
        });

        updateProposedPeriod(mainContract);

        return this;
    }

    private void createUsedBudgetChartCard(MainContract mainContract) {
        Chart chart = new Chart(ChartType.COLUMN);
        chartCard.getContent().removeAllComponents();
        chartCard.getContent().addComponent(chart);
        chart.setSizeFull();

        Configuration conf = chart.getConfiguration();

        conf.setTitle("");
        conf.setSubTitle("");

        XAxis xAxis = new XAxis();
        xAxis.setCategories(new String[] { "Main" });
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setVisible(false);
        yAxis.setMin(0);
        yAxis.setTitle(new AxisTitle(null));
        //StackLabels sLabels = new StackLabels(true);
        //yAxis.setStackLabels(sLabels);
        conf.addyAxis(yAxis);

        Legend legend = new Legend();
        legend.setAlign(HorizontalAlign.RIGHT);
        legend.setFloating(true);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(-100);
        legend.setY(20);
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
            System.out.println("work = " + work);
            Optional<Consultant> optionalConsultant = mainContract.getConsultants().stream().filter(consultant -> consultant.getUser().getUuid().equals(work.getUser().getUuid())).findFirst();
            if(!optionalConsultant.isPresent()) continue;
            sum += (work.getWorkduration() * optionalConsultant.get().getRate());
        }

        conf.addSeries(new ListSeries("Budget", new Number[] { (mainContract.getAmount()-sum) }));
        conf.addSeries(new ListSeries("Used", new Number[] { sum}));

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
                    addButton.addClickListener(event1 -> {
                        Project project = projectComboBox.getSelectedItem().get();
                        createProject(mainContract, project);
                        subWindow.close();
                    });
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
        List<Project> deltaProjects = new ArrayList<>();
        deltaProjects.addAll(mainContract.getClient().getProjects());
        //deltaProjects.removeAll(mainContract.getProjects());
        Map<String, Project> projectsWithUserWorkButNoContract = new HashMap<>();
        for (User user : mainContract.getConsultants().stream().map(Consultant::getUser).collect(Collectors.toList())) {
            for (Project project : contractService.getProjectsWithUserWorkButNoContract(deltaProjects, user)) {
                // is project already on contract
                if(mainContract.getProjects().stream().filter(p -> p.getUuid().equals(project.getUuid())).findFirst().isPresent()) {
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
            });
            consultantRowDesign.getTxtHours().setValue(Math.round(consultant.getHours())+"");
            consultantRowDesign.getTxtHours().addValueChangeListener(event -> {
                consultant.setHours(NumberConverter.parseDouble(event.getValue()));
                consultantRepository.save(consultant);
            });
            consultantRowDesign.getVlHours().setVisible(consultant.getMainContract().getContractType().equals(ContractType.PERIOD));
            consultantRowDesign.getImgPhoto().addComponent(photoService.getRoundMemberImage(consultant.getUser(), false));

            consultantRowDesign.getBtnDelete().addClickListener(event -> removeConsultant(mainContract, consultant));

            responsiveRow.addColumn()
                    .withComponent(consultantRowDesign)
                    .withDisplayRules(12, 12, 6, 4);
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
                    addButton.addClickListener(event1 -> {
                        User user = userComboBox.getSelectedItem().get();
                        createConsultant(mainContract, user);
                        subWindow.close();
                    });
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
                if(!mainContract.getConsultants().stream().filter(consultant -> consultant.getUser().getUuid().equals(employee.getUuid())).findFirst().isPresent())
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
                    if(!mainContract.getConsultants().stream().filter(consultant -> consultant.getUser().getUuid().equals(user.getUuid())).findFirst().isPresent())
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
        consultantRowDesign.getBtnDelete().addClickListener(event -> createConsultant(mainContract, user));

        responsiveRow.addColumn()
                .withComponent(consultantRowDesign)
                .withDisplayRules(12, 12, 6, 4);
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
        createConsultantList(mainContract);
        createProjectList(mainContract);
        updateProposedPeriod(mainContract);
        if(mainContract.getContractType().equals(ContractType.AMOUNT) || mainContract.getContractType().equals(ContractType.SKI))
            if(mainContract.getProjects().size()>0 && mainContract.getConsultants().size()>0)
                createUsedBudgetChartCard(mainContract);
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

    private void createConsultant(MainContract mainContract, User user) {
        Consultant consultant = new Consultant(mainContract, user, 0.0, 0.0, 0.0);
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
