package dk.trustworks.invoicewebui.web.contracts.layouts;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.contextmenu.GridContextMenu;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
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
import dk.trustworks.invoicewebui.web.contracts.model.ConsultantRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.button.MButton;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@SpringComponent
@SpringUI
public class ContractDetailLayout extends ResponsiveLayout {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private BudgetNewRepository budgetNewRepository;

    private ResponsiveRow contractRow;

    private ResponsiveColumn treeGridColumn;
    private Card consultantsCard;
    private Card projectsCard;
    private Grid<ConsultantRow> grid;
    private MainContract mainContract;

    @PostConstruct
    public void init() {
        contractRow = this.addRow();
    }

    public ResponsiveLayout loadContractDetails(MainContract mainContract) {
        this.mainContract = mainContract;
        contractRow.removeAllComponents();

        Binder<MainContract> mainContractBinder = new Binder<>();
        ContractFormDesign mainContractForm = new ContractFormDesign();
        mainContractForm.getChkProjects().setVisible(false);
        mainContractForm.getBtnCreate().setVisible(false);
        mainContractForm.getBtnUpdate().setVisible(true);
        mainContractForm.getBtnEdit().setVisible(false);
        mainContractForm.getTxtAmount().setVisible(mainContract.getContractType().equals(ContractType.AMOUNT));
        mainContractForm.getTxtAmount().setValue(NumberConverter.formatDouble(mainContract.getAmount()));
        mainContractForm.getDfFrom().setVisible(true);
        mainContractBinder.forField(mainContractForm.getDfFrom()).bind(MainContract::getActiveFrom, MainContract::setActiveFrom);
        //mainContractForm.getDfFrom().setValue(mainContract.getActiveFrom());
        mainContractForm.getDfTo().setVisible(true);
        mainContractBinder.forField(mainContractForm.getDfTo()).bind(Contract::getActiveTo, Contract::setActiveTo);
        //mainContractForm.getDfTo().setValue(mainContract.getActiveTo());
        mainContractForm.getCbType().setVisible(true);
        mainContractForm.getCbType().setEnabled(false);
        mainContractBinder.forField(mainContractForm.getCbType()).bind(Contract::getContractType, Contract::setContractType);
        //mainContractForm.getCbType().setValue(mainContract.getContractType());
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

        projectsCard = new Card();
        projectsCard.getLblTitle().setValue("Projects");

        createProjectList(mainContract);

        contractRow.addColumn()
                .withDisplayRules(12, 12, 6, 4)
                .withComponent(projectsCard);

        consultantsCard = new Card();
        consultantsCard.getLblTitle().setValue("Consultants");

        createConsultantList(mainContract);

        treeGridColumn = contractRow.addColumn()
                .withDisplayRules(12, 12, 4, 4)
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
        projectsCard.getContent().removeAllComponents();

        for (Project project : mainContract.getProjects()) {
            ProjectRowDesign projectRowDesign = new ProjectRowDesign();
            projectRowDesign.getLblName().setValue(project.getName());
            projectRowDesign.getBtnIcon().setIcon(MaterialIcons.DATE_RANGE);
            projectsCard.getContent().addComponent(projectRowDesign);
        }

        projectsCard.getContent().addComponent(new MButton(
                VaadinIcons.PLUS,
                event -> {
                    Window subWindow = new Window("");
                    VerticalLayout subContent = new VerticalLayout();
                    subWindow.setContent(subContent);

                    // Put some components in it
                    subContent.addComponent(new Label("Add project"));
                    ComboBox<Project> projectComboBox = new ComboBox<>();
                    projectComboBox.setItems(projectRepository.findByClientAndActiveTrueOrderByNameAsc(mainContract.getClient()));
                    projectComboBox.setItemCaptionGenerator(Project::getName);
                    subContent.addComponent(projectComboBox);
                    Button addButton = new Button("Add");
                    addButton.addClickListener(event1 -> {
                        Project project = projectComboBox.getSelectedItem().get();
                        mainContract.addProject(project);
                        project.addMainContract(mainContract);
                        projectRepository.save(project);
                        subWindow.close();
                        createProjectList(mainContract);
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

    private void createConsultantList(final MainContract mainContract) {
        consultantsCard.getContent().removeAllComponents();
        for (Consultant consultant : mainContract.getConsultants()) {
            ConsultantRowDesign consultantRowDesign = new ConsultantRowDesign();
            consultantRowDesign.getLblName().setValue(consultant.getUser().getFirstname() + " " + consultant.getUser().getLastname());
            consultantRowDesign.getTxtRate().setValue(NumberConverter.formatDouble(consultant.getRate()));
            consultantRowDesign.getTxtRate().addValueChangeListener(event -> {
                consultant.setRate(NumberConverter.parseDouble(event.getValue()));
                consultantRepository.save(consultant);
            });
            consultantRowDesign.getImgPhoto().addComponent(photoService.getRoundMemberImage(consultant.getUser(), false));
            consultantsCard.getContent().addComponent(consultantRowDesign);
        }

        consultantsCard.getContent().addComponent(new MButton(
                VaadinIcons.PLUS,
                event -> {
                    Window subWindow = new Window("");
                    VerticalLayout subContent = new VerticalLayout();
                    subWindow.setContent(subContent);

                    // Put some components in it
                    subContent.addComponent(new Label("Add consultant"));
                    ComboBox<User> userComboBox = new ComboBox<>();
                    userComboBox.setItems(userRepository.findAll());
                    userComboBox.setItemCaptionGenerator(User::getUsername);
                    subContent.addComponent(userComboBox);
                    Button addButton = new Button("Add");
                    addButton.addClickListener(event1 -> {
                        Consultant consultant = new Consultant(mainContract, userComboBox.getSelectedItem().get(), 0.0, 0.0, 0.0);
                        mainContract.addConsultant(consultant);
                        consultantRepository.save(consultant);
                        subWindow.close();
                        createConsultantList(mainContract);
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

    private Grid createTreeGrid() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        //LocalDate startDate = mainContract.getActiveFrom().withDayOfMonth(1);
        LocalDate endDate = LocalDate.of(mainContract.getActiveTo().getYear(),
                mainContract.getActiveTo().getMonthValue(),
                mainContract.getActiveTo().getDayOfMonth());
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
        System.out.println("period.getMonths() = " + monthsBetween);


        grid = new Grid<>();
        grid.addColumn(ConsultantRow::getUsername).setWidth(200).setCaption("Consultant").setId("name-column");
        grid.addColumn(ConsultantRow::getRate).setWidth(100).setCaption("Rate").setEditorComponent(new TextField(), ConsultantRow::setRate);
        //grid.addColumn(ConsultantRow::getAmount).setWidth(100).setCaption("Budget").setEditorComponent(new TextField(), ConsultantRow::setAmount);
        grid.setFrozenColumnCount(2);
        grid.setWidth("100%");
        grid.getEditor().setEnabled(true);

        GridContextMenu<ConsultantRow> gridMenu = new GridContextMenu<>(grid);
        gridMenu.addGridBodyContextMenuListener(this::updateGridBodyMenu);
        gridMenu.addGridHeaderContextMenuListener(this::updateGridHeaderMenu);

        List<ConsultantRow> consultantRows = new ArrayList<>();

        System.out.println("mainContract.getConsultants().size() = " + mainContract.getConsultants().size());
        for (Consultant consultant : mainContract.getConsultants()) {
            System.out.println("consultant = " + consultant);
            //LocalDate budgetDate = startDate;
            //System.out.println("budgetDate = " + budgetDate);

            ConsultantRow consultantRow = new ConsultantRow(consultant, 0);
            //ConsultantRow consultantRow = new ConsultantRow(consultant, (int)(monthsBetween+1));
            //System.out.println("monthsBetween = " + (monthsBetween+1));
/*
            int month = 0;
            while(budgetDate.isBefore(endDate)) {
                final LocalDate filterDate = budgetDate;
                System.out.println("filterDate = " + filterDate);

                List<BudgetNew> budgets = budgetNewRepository.findByMonthAndYearAndConsultant(filterDate.getMonthValue(), filterDate.getYear(), consultant);
                System.out.println("budgets.size() = " + budgets.size());

                if(budgets.size() > 0) {
                    BudgetNew budget = budgets.get(0);
                    if(consultant.getUser().getUsername().equals("hans.lassen")) System.out.println("budget.get() = " + budget);
                    consultantRow.setMonth(month, (budget.getBudget() / consultant.getRate())+"");
                } else {
                    System.out.println("0.0 = " + 0.0);
                    consultantRow.setMonth(month, "0.0");
                }
                month++;
                budgetDate = budgetDate.plusMonths(1);
            }
            */
            consultantRows.add(consultantRow);
        }
/*
        int month = 0;
        int year = startDate.getYear();
        List<String> yearColumns = new ArrayList<>();
        LocalDate budgetDate = startDate;
        while(budgetDate.isBefore(endDate)) {
            final LocalDate filterDate = budgetDate;
            final int actualMonth = month;
            Grid.Column<ConsultantRow, ?> budgetColumn = grid.addColumn(
                    consultantRow -> consultantRow.getMonth(actualMonth))
                    .setStyleGenerator(budgetHistory -> "align-right")
                    .setWidth(100)
                    .setId(Month.of(filterDate.getMonthValue()).name()+filterDate.getYear())
                    .setCaption(Month.of(filterDate.getMonthValue()).getDisplayName(TextStyle.SHORT, Locale.ENGLISH)+" "+ (filterDate.getYear()-2000))
                    .setEditorComponent(new TextField(), (Setter<ConsultantRow, String>) (consultantRow, budgetValue) -> consultantRow.setMonth(actualMonth, budgetValue));
            yearColumns.add(budgetColumn.getId());
            budgetDate = budgetDate.plusMonths(1);
            month++;
            if(year < budgetDate.getYear()) {
                //topHeader.join(yearColumns.toArray(new String[0])).setText(year + "");
                yearColumns = new ArrayList<>();
                year++;
            }
        }
*/
        grid.setItems(consultantRows);

        consultantsCard.getContent().addComponent(grid);
        return grid;
    }

    private void updateTreeGrid() {
        grid = createTreeGrid();
        //budgetCard.getContainer().removeAllComponents();
        //budgetCard.getContainer().addComponent(treeGrid);
    }

    private void updateGridBodyMenu(GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<ConsultantRow> event) {
        event.getContextMenu().removeItems();
        if (event.getItem() == null) {
            //if(event.getItem().getClass().equals(ConsultantRow.class)) {
                event.getContextMenu().addItem("Add Consultant", VaadinIcons.PLUS, selectedItem -> {
                    Window subWindow = new Window("");
                    VerticalLayout subContent = new VerticalLayout();
                    subWindow.setContent(subContent);

                    // Put some components in it
                    subContent.addComponent(new Label("Add consultant"));
                    ComboBox<User> userComboBox = new ComboBox<>();
                    userComboBox.setItems(userRepository.findAll());
                    userComboBox.setItemCaptionGenerator(User::getUsername);
                    subContent.addComponent(userComboBox);
                    Button addButton = new Button("Add");
                    addButton.addClickListener(event1 -> {
                        Consultant consultant = new Consultant(mainContract, userComboBox.getSelectedItem().get(), 0.0, 0.0, 0.0);
                        //Taskworkerconstraint taskworkerconstraint = new Taskworkerconstraint(0.0, userComboBox.getSelectedItem().get(), ((TaskRow) event.getItem()).getTask());
                        //taskworkerconstraintRepository.save(taskworkerconstraint);
                        consultantRepository.save(consultant);
                        subWindow.close();
                        updateTreeGrid();
                    });
                    subContent.addComponent(addButton);

                    // Center it in the browser window
                    subWindow.center();

                    // Open it in the UI
                    UI.getCurrent().addWindow(subWindow);
                });
            //} else {
                //event.getContextMenu().addItem("Remove "+((UserRow)event.getItem()).getUsername(), VaadinIcons.CLOSE, selectedItem -> Notification.show("Not possible at this time!"));
            //}
        }
    }

    private void updateGridHeaderMenu(GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<ConsultantRow> event) {
        event.getContextMenu().removeItems();
        if (event.getColumn() != null) {
            event.getContextMenu().addItem("Sort Ascending", selectedItem ->
                    grid.sort((Grid.Column<ConsultantRow, ?>) event.getColumn(), SortDirection.ASCENDING));
            event.getContextMenu().addItem("Sort Descending", selectedItem ->
                    grid.sort((Grid.Column<ConsultantRow, ?>) event.getColumn(), SortDirection.DESCENDING));
        } else {
            event.getContextMenu().addItem("menu is empty", null);
        }
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
