package dk.trustworks.invoicewebui.web.contracts.layouts;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.exceptions.ContractValidationException;
import dk.trustworks.invoicewebui.model.*;
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

    private ContractFormDesign mainContractForm;

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

        Binder<MainContract> mainContractBinder = new Binder<>();
        mainContractForm = new ContractFormDesign();
        mainContractForm.getContainer().setHeight(300, Unit.PIXELS);
        mainContractForm.getContainer().addStyleName("v-scrollable");

        mainContractForm.getChkProjects().setVisible(false);
        mainContractForm.getBtnCreate().setVisible(false);
        mainContractForm.getBtnUpdate().setVisible(true);
        mainContractForm.getBtnEdit().setVisible(false);
        mainContractForm.getTxtAmount().setVisible(mainContract.getContractType().equals(ContractType.AMOUNT));
        mainContractForm.getTxtAmount().setValue(NumberConverter.formatDouble(mainContract.getAmount()));
        mainContractForm.getDfFrom().setVisible(true);
        mainContractBinder.forField(mainContractForm.getDfFrom()).bind(MainContract::getActiveFrom, MainContract::setActiveFrom);
        mainContractForm.getDfTo().setVisible(true);
        mainContractBinder.forField(mainContractForm.getDfTo()).bind(Contract::getActiveTo, Contract::setActiveTo);
        mainContractForm.getCbType().setVisible(true);
        mainContractForm.getCbType().setEnabled(false);
        mainContractBinder.forField(mainContractForm.getCbType()).bind(Contract::getContractType, Contract::setContractType);
        mainContractForm.getLblTitle().setValue("Main Contract");
        mainContractBinder.readBean(mainContract);

        mainContractForm.getBtnUpdate().addClickListener(event -> {
            try {
                mainContractBinder.writeBean(mainContract);
                mainContract.setAmount(NumberConverter.parseDouble(mainContractForm.getTxtAmount().getValue()));
                contractService.updateContract(mainContract);
            } catch (ValidationException e) {
                e.printStackTrace();
                Notification.show("Errors in form", e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });

        contractRow.addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(mainContractForm);

        Card projectsCard = new Card();
        projectsCard.getLblTitle().setValue("Projects");
        projectsCard.getContent().setHeight(300, Unit.PIXELS);
        projectsCard.getContent().addStyleName("v-scrollable");
        projectsLayout = new MVerticalLayout().withWidth(100, Unit.PERCENTAGE);
        projectsCard.getContent().addComponent(projectsLayout);

        createProjectList(mainContract);

        contractRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(projectsCard);

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
            if(subContract.getContractType().equals(ContractType.AMOUNT))
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

        return this;
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
                    String period = contractService.getUsersFirstAndLastWorkOnProject(project, user);
                    System.out.println("period = " + period);
                    mainContractForm.getDfTo().setComponentError(new UserError(period));
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
                proposedUsers.put(employee.getUuid(), employee);
            }
        }

        if(proposedUsers.size() > 0)
            responsiveRow.addColumn()
                    .withDisplayRules(12, 12, 12, 12)
                    .withComponent(new MLabel("Proposed consultants:"));

        for (User user : proposedUsers.values()) {
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
    }

    private void createProject(MainContract mainContract, Project project) {
        try {
            contractService.addProject(mainContract, project);
        } catch (ContractValidationException e) {
            e.printStackTrace();
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
        createConsultantList(mainContract);
        createProjectList(mainContract);
    }

    private void removeConsultant(MainContract mainContract, Consultant consultant) {
        mainContract.getConsultants().remove(consultant);
        consultantRepository.delete(consultant);
        createConsultantList(mainContract);
        createProjectList(mainContract);
    }

    private void removeProject(MainContract mainContract, Project project) {
        mainContract = contractService.removeProject(mainContract, project);
        createProjectList(mainContract);
        createConsultantList(mainContract);
    }

    private void createConsultant(MainContract mainContract, User user) {
        Consultant consultant = new Consultant(mainContract, user, 0.0, 0.0, 0.0);
        mainContract.addConsultant(consultant);
        consultantRepository.save(consultant);
        createConsultantList(mainContract);
        createProjectList(mainContract);
    }

    private ContractFormDesign getSubContractComponent(Contract mainContract, boolean newContract) {
        ContractFormDesign contractFormDesign = new ContractFormDesign();
        contractFormDesign.getChkProjects().setVisible(false);
        contractFormDesign.getBtnCreate().setVisible(newContract);
        contractFormDesign.getBtnUpdate().setVisible(!newContract);
        contractFormDesign.getBtnEdit().setVisible(false);
        contractFormDesign.getTxtAmount().setVisible(mainContract.getContractType().equals(ContractType.AMOUNT));
        contractFormDesign.getDfFrom().setVisible(false);
        contractFormDesign.getDfTo().setVisible(true);
        contractFormDesign.getCbType().setVisible(false);
        contractFormDesign.getLblTitle().setValue(newContract?"Add sub contract":"Sub Contract");
        return contractFormDesign;
    }
}
