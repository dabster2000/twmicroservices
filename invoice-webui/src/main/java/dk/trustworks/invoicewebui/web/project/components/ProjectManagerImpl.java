package dk.trustworks.invoicewebui.web.project.components;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.Setter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.web.contracts.model.BudgetRow;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static java.util.Comparator.comparing;
import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * Created by hans on 21/08/2017.
 */

@SpringComponent
@SpringUI
public class ProjectManagerImpl extends ProjectManagerDesign {

    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    private final ClientRepository clientRepository;

    private final ClientdataRepository clientdataRepository;

    private final BudgetNewRepository budgetNewRepository;

    private ProjectMapLocationImpl projectMapLocation;

    private final PhotoRepository photoRepository;

    private final NewsRepository newsRepository;

    private ResponsiveLayout responsiveLayout;

    private Project currentProject;

    private BudgetCardDesign budgetCard;

    private Grid<BudgetRow> grid;

    @Autowired
    public ProjectManagerImpl(UserRepository userRepository, ProjectRepository projectRepository, ClientRepository clientRepository, ClientdataRepository clientdataRepository, BudgetNewRepository budgetNewRepository, PhotoRepository photoRepository, NewsRepository newsRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
        this.clientdataRepository = clientdataRepository;
        this.budgetNewRepository = budgetNewRepository;
        this.photoRepository = photoRepository;
        this.newsRepository = newsRepository;
    }


    @Transactional
    public ProjectManagerImpl init() {
        getOnOffSwitch().setValue(false);
        getOnOffSwitch().addValueChangeListener(event -> changeOptions());
        getSelProject().setItemCaptionGenerator(Project::getName);
        List<Project> projects = newArrayList(((getOnOffSwitch().getValue())?projectRepository.findAllByOrderByNameAsc():projectRepository.findAllByActiveTrueOrderByNameAsc()));

        getSelProject().setItems(projects);
        getSelProject().addValueChangeListener(event -> reloadGrid());

        getSelClient().setItems(clientRepository.findByActiveTrueOrderByName());
        getSelClient().setItemCaptionGenerator(Client::getName);
        getSelClient().addValueChangeListener(event -> changeOptions());

        getBtnAddNewProject().addClickListener((Button.ClickEvent event) -> {
            final Window window = new Window("Create Project");
            window.setWidth("330px");
            window.setHeight("300px");
            window.setModal(true);
            NewProjectDesign newProject = new NewProjectDesign();
            window.setContent(newProject);
            UI.getCurrent().addWindow(window);
            newProject.getCbClients().setItems(clientRepository.findByActiveTrue());
            newProject.getCbClients().addValueChangeListener(event1 -> {
                List<Clientdata> clientdataList = clientdataRepository.findByClient(event1.getValue());
                newProject.getCbClientdatas().setVisible(true);
                newProject.getCbClientdatas().setItems(clientdataList);
                newProject.getCbClientdatas().setItemCaptionGenerator(item -> item.getStreetnamenumber() + ", "
                        + item.getPostalcode() + " " + item.getCity() + ", "
                        + item.getContactperson());
                newProject.getCbClientdatas().setSelectedItem(clientdataList.get(0));
                newProject.getBtnCreate().setEnabled(true);
            });
            newProject.getCbClients().setItemCaptionGenerator(Client::getName);
            newProject.getBtnCreate().addClickListener(event1 -> {
                Clientdata clientdata = newProject.getCbClientdatas().getValue();
                Project project = projectRepository.save(new Project(newProject.getTxtProjectName().getValue(), newProject.getCbClients().getValue(), clientdata));
                List<Project> reloadedProjects = CollectionHelper.newArrayList(projectRepository.findAll());
                getSelProject().setItems(reloadedProjects);
                getSelProject().setSelectedItem(project);
                getSelProject().setValue(project);
                window.close();
                UI.getCurrent().removeWindow(window);
                reloadGrid();
            });
            newProject.getBtnCancel().addClickListener(event1 -> window.close());
        });
        return this;
    }

    private void changeOptions() {
        if(!getSelClient().getSelectedItem().isPresent()) {
            List<Project> clientProjectList = newArrayList(((getOnOffSwitch().getValue())?projectRepository.findAllByOrderByNameAsc():projectRepository.findAllByActiveTrueOrderByNameAsc()));
            getSelProject().setItems(clientProjectList);
        } else {
            if(getOnOffSwitch().getValue()) {
                getSelProject().setItems(getSelClient().getSelectedItem().get().getProjects().stream().sorted(comparing(Project::getName)));
            } else {
                getSelProject().setItems(getSelClient().getSelectedItem().get().getProjects().stream().filter(Project::isActive).sorted(comparing(Project::getName)));
            }
        }
        reloadGrid();
    }

    public void setCurrentProject(String projectUUID) {
        currentProject = projectRepository.findOne(projectUUID);
        getSelProject().setSelectedItem(currentProject);
        getSelProject().setValue(currentProject);
        reloadGrid();
    }

    private void createDetailLayout() {
        projectMapLocation = new ProjectMapLocationImpl(projectRepository);
        responsiveLayout = new ResponsiveLayout();
        addComponent(responsiveLayout);

        Photo photoResource = photoRepository.findByRelateduuid(currentProject.getClient().getUuid());
        ProjectDetailCardImpl projectDetailCard = new ProjectDetailCardImpl(currentProject, userRepository.findAll(), photoResource, projectRepository, newsRepository, userRepository);
        projectDetailCard.getBtnUpdate().addClickListener(event -> {
            projectDetailCard.save();
            updateTreeGrid();
        });

        ResponsiveRow clientDetailsRow = responsiveLayout.addRow();
        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(projectDetailCard);

        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(projectMapLocation.init(currentProject));

        budgetCard = new BudgetCardDesign();
        updateTreeGrid();

        ResponsiveRow budgetRow = responsiveLayout.addRow();
        budgetRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(budgetCard);
    }

    private void updateTreeGrid() {
        grid = createGrid();
        budgetCard.getContainer().removeAllComponents();
        budgetCard.getContainer().addComponent(grid);
    }

    private Grid createGrid() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        if(currentProject.getMainContracts().size()==0) return new Grid();
        LocalDate endDate = currentProject.getMainContracts().stream().max(Comparator.comparing(MainContract::getEndDate)).get().getEndDate();
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
        System.out.println("period.getMonths() = " + monthsBetween);

        grid = new Grid<>();

        grid.addColumn(BudgetRow::getUsername).setWidth(200).setCaption("Consultant").setId("name-column");
        //grid.addColumn(ConsultantRow::getRate).setWidth(100).setCaption("Rate").setEditorComponent(new TextField(), ConsultantRow::setRate);
        //grid.addColumn(ConsultantRow::getAmount).setWidth(100).setCaption("Budget").setEditorComponent(new TextField(), ConsultantRow::setAmount);
        grid.setFrozenColumnCount(1);
        grid.setWidth("100%");
        grid.getEditor().setEnabled(true);

        List<BudgetRow> budgetRows = new ArrayList<>();

        for (MainContract mainContract : currentProject.getMainContracts()) {
            if(!mainContract.getContractType().equals(ContractType.AMOUNT)) continue;
            for (Consultant consultant : mainContract.getConsultants()) {
                BudgetRow budgetRow = new BudgetRow(consultant, (int)(monthsBetween+1));
                System.out.println("consultant = " + consultant);
                LocalDate budgetDate = startDate;

                int month = 0;
                while(budgetDate.isBefore(endDate)) {
                    /*
                    ptional<Budget> budget = budgets.stream()
                            .filter(p -> p.getYear()==filterDate.getYear() &&
                                    p.getMonth()==filterDate.getMonthOfYear()-1 &&
                                    p.getTask()!=null &&
                                    p.getUser()!=null &&
                                    p.getTask().getUuid().equals(task.getUuid()) &&
                                    p.getUser().getUuid().equals(user.getUuid()))
                            .findFirst();

                    if(budget.isPresent()) {
                        if(user.getUsername().equals("hans.lassen")) System.out.println("budget.get() = " + budget);
                        userRow.setMonth(month, (budget.get().getBudget() / taskworkerconstraint.get().getPrice())+"");
                    } else {
                        userRow.setMonth(month, "0.0");
                    }
                    month++;
                    budgetDate = budgetDate.plusMonths(1);
                     */

                    final LocalDate filterDate = budgetDate;
                    System.out.println("filterDate = " + filterDate);

                    BudgetNew budget = budgetNewRepository.findByMonthAndYearAndConsultant(filterDate.getMonthValue()-1, filterDate.getYear(), consultant);
                    //System.out.println("budgets.size() = " + budgets.size());

                    if(budget != null) {
                        //BudgetNew budget = budgets.get(0);
                        System.out.println("budget = " + budget);
                        if(consultant.getUser().getUsername().equals("hans.lassen")) System.out.println("budget.get() = " + budget);
                        budgetRow.setMonth(month, (budget.getBudget() / consultant.getRate())+"");
                    } else {
                        System.out.println("0.0 = " + 0.0);
                        budgetNewRepository.save(new BudgetNew(filterDate.getMonthValue()-1, filterDate.getYear(), 0.0, consultant));
                        budgetRow.setMonth(month, "0.0");
                    }
                    month++;
                    budgetDate = budgetDate.plusMonths(1);
                }

                budgetRows.add(budgetRow);
            }
            int month = 0;
            int year = startDate.getYear();
            List<String> yearColumns = new ArrayList<>();
            LocalDate budgetDate = startDate;
            while(budgetDate.isBefore(endDate)) {
                final LocalDate filterDate = budgetDate;
                final int actualMonth = month;
                Grid.Column<BudgetRow, ?> budgetColumn = grid.addColumn(
                        budgetRow -> budgetRow.getMonth(actualMonth))
                        .setStyleGenerator(budgetHistory -> "align-right")
                        .setWidth(100)
                        .setId(Month.of(filterDate.getMonthValue()).name()+filterDate.getYear())
                        .setCaption(Month.of(filterDate.getMonthValue()).getDisplayName(TextStyle.SHORT, Locale.ENGLISH)+" "+ (filterDate.getYear()-2000))
                        .setEditorComponent(new TextField(), (Setter<BudgetRow, String>) (budgetRow, budgetValue) -> budgetRow.setMonth(actualMonth, budgetValue));
                yearColumns.add(budgetColumn.getId());
                budgetDate = budgetDate.plusMonths(1);
                month++;
                if(year < budgetDate.getYear()) {
                    //topHeader.join(yearColumns.toArray(new String[0])).setText(year + "");
                    yearColumns = new ArrayList<>();
                    year++;
                }
            }
        }
        grid.getEditor().setEnabled(true);
        grid.getEditor().addSaveListener(event -> {
            System.out.println("grid.getEditor().addSaveListener");
            BudgetRow budgetRow = event.getBean();
            System.out.println("consultantRow = " + budgetRow);
            LocalDate budgetCountDate = startDate;
            List<BudgetNew> budgetList = new ArrayList<>();
            for (String budgetString : budgetRow.getBudget()) {
                if(budgetString==null) budgetString = "0.0";
                BudgetNew budget = budgetNewRepository.findByMonthAndYearAndConsultant(
                        budgetCountDate.getMonthValue() - 1,
                        budgetCountDate.getYear(),
                        budgetRow.getConsultant());
                budget.setBudget(Double.parseDouble(budgetString) * NumberConverter.parseDouble(budgetRow.getRate()));
                budgetNewRepository.save(budget);
                budgetCountDate = budgetCountDate.plusMonths(1);
            }

            updateTreeGrid();

        });
        grid.setItems(budgetRows);

        return grid;
    }


    /*
    private TreeGrid createTreeGrid() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = new LocalDate(currentProject.getEnddate().getYear(),
                currentProject.getEnddate().getMonthValue(),
                currentProject.getEnddate().getDayOfMonth());
        Months monthsBetween = Months.monthsBetween(startDate, endDate);
        System.out.println("period.getMonths() = " + monthsBetween.getMonths());

        List<TaskRow> taskRows = new ArrayList<>();

        Map<String, User> usersMap = userRepository.findAll().stream().collect(Collectors.toMap(User::getUuid, user -> user));

        for (Task task : currentProject.getTasks()) {
            List<Budget> budgets = budgetRepository.findByTaskuuid(task.getUuid());
            TaskRow taskRow = new TaskRow(task, monthsBetween.getMonths()+1);
            taskRows.add(taskRow);
            for (User user : usersMap.values()) {
                LocalDate budgetDate = startDate;
                List<Taskworkerconstraint> taskworkerconstraints = taskworkerconstraintRepository.findByTask(task);
                Optional<Taskworkerconstraint> taskworkerconstraint = taskworkerconstraints.stream()
                        .filter(p ->
                                p.getTask()!=null &&
                                p.getUser()!=null &&
                                p.getTask().getUuid().equals(task.getUuid()) &&
                                p.getUser().getUuid().equals(user.getUuid()))
                        .findFirst();
                //if(user.getUsername().equals("hans.lassen")) System.out.println("taskworkerconstraint = " + taskworkerconstraint);

                if(!taskworkerconstraint.isPresent()) continue;

                UserRow userRow = new UserRow(task, taskworkerconstraint.get(), monthsBetween.getMonths()+1, user);

                //if(user.getUsername().equals("hans.lassen")) System.out.println("budgets = " + budgets.size());

                int month = 0;
                while(budgetDate.isBefore(endDate)) {
                    final LocalDate filterDate = budgetDate;

                    Optional<Budget> budget = budgets.stream()
                            .filter(p -> p.getYear()==filterDate.getYear() &&
                                    p.getMonth()==filterDate.getMonthOfYear()-1 &&
                                    p.getTask()!=null &&
                                    p.getUser()!=null &&
                                    p.getTask().getUuid().equals(task.getUuid()) &&
                                    p.getUser().getUuid().equals(user.getUuid()))
                            .findFirst();

                    if(budget.isPresent()) {
                        if(user.getUsername().equals("hans.lassen")) System.out.println("budget.get() = " + budget);
                        userRow.setMonth(month, (budget.get().getBudget() / taskworkerconstraint.get().getPrice())+"");
                    } else {
                        userRow.setMonth(month, "0.0");
                    }
                    month++;
                    budgetDate = budgetDate.plusMonths(1);
                }
                taskRow.addUserRow(userRow);
                System.out.println("userRow = " + userRow);
            }
            System.out.println("taskRow = " + taskRow);
        }


        treeGrid = new TreeGrid<>();
        treeGrid.addColumn(TaskRow::getTaskName).setWidth(200).setCaption("Task Name").setId("name-column").setEditorComponent(new TextField(), TaskRow::setTaskName);
        treeGrid.addColumn(TaskRow::getUsername).setWidth(200).setCaption("Consultant");
        treeGrid.addColumn(TaskRow::getRate).setWidth(100).setCaption("Rate").setEditorComponent(new TextField(), TaskRow::setRate);
        treeGrid.setFrozenColumnCount(3);

        GridContextMenu<TaskRow> gridMenu = new GridContextMenu<>(treeGrid);
        gridMenu.addGridBodyContextMenuListener(this::updateGridBodyMenu);
        gridMenu.addGridHeaderContextMenuListener(this::updateGridHeaderMenu);

        int month = 0;
        int year = startDate.getYear();
        List<String> yearColumns = new ArrayList<>();
        LocalDate budgetDate = startDate;
        while(budgetDate.isBefore(endDate)) {
            final LocalDate filterDate = budgetDate;
            final int actualMonth = month;
            Grid.Column<TaskRow, ?> budgetColumn = treeGrid.addColumn(
                    taskRow -> taskRow.getMonth(actualMonth))
                    .setStyleGenerator(budgetHistory -> "align-right")
                    .setWidth(100)
                    .setId(Month.of(filterDate.getMonthOfYear()).name()+filterDate.getYear())
                    .setCaption(Month.of(filterDate.getMonthOfYear()).getDisplayName(TextStyle.SHORT, Locale.ENGLISH)+" "+filterDate.year().getAsShortText())
                    .setEditorComponent(new TextField(), (Setter<TaskRow, String>) (taskRow, budgetValue) -> taskRow.setMonth(actualMonth, budgetValue));
            yearColumns.add(budgetColumn.getId());
            budgetDate = budgetDate.plusMonths(1);
            month++;
            if(year < budgetDate.getYear()) {
                //topHeader.join(yearColumns.toArray(new String[0])).setText(year + "");
                yearColumns = new ArrayList<>();
                year++;
            }
        }

        treeGrid.setWidth("100%");
        treeGrid.getEditor().setEnabled(true);
        treeGrid.getEditor().addSaveListener(event -> {
            TaskRow taskRow = event.getBean();
            if(taskRow.getClass().equals(UserRow.class)) {
                UserRow userRow = (UserRow) taskRow;
                Taskworkerconstraint taskworkerconstraint = userRow.getTaskworkerconstraint();
                System.out.println("taskworkerconstraint = " + taskworkerconstraint);
                taskworkerconstraint.setPrice(Double.parseDouble(userRow.getRate()));
                taskworkerconstraintRepository.save(taskworkerconstraint);
                LocalDate budgetCountDate = startDate;
                List<Budget> budgetList = new ArrayList<>();
                for (String budgetString : userRow.getBudget()) {
                    if(budgetString==null) budgetString = "0.0";
                    Budget budget = new Budget(
                            budgetCountDate.getMonthOfYear()-1,
                            budgetCountDate.getYear(),
                            Double.parseDouble(budgetString) * taskworkerconstraint.getPrice(),
                            userRow.getUser(),
                            userRow.getTask()
                    );
                    budgetList.add(budget);
                    budgetCountDate = budgetCountDate.plusMonths(1);
                }

                budgetRepository.save(budgetList);
                //updateTreeGrid();
            } else {
                taskRow.getTask().setName(taskRow.getTaskName());
                taskRepository.save(taskRow.getTask());
                //updateTreeGrid();
            }
        });
        treeGrid.setItems(taskRows, TaskRow::getUserRows);
        return treeGrid;
    }
    */

    private void reloadGrid() {
        if(responsiveLayout!=null) removeComponent(responsiveLayout);
        currentProject = getSelProject().getValue();
        if(getSelProject().getSelectedItem().isPresent()) createDetailLayout();
    }
/*
    private void updateGridBodyMenu(GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<TaskRow> event) {
        event.getContextMenu().removeItems();
        if (event.getItem() != null) {
            if(event.getItem().getClass().equals(TaskRow.class)) {
                event.getContextMenu().addItem("Add Consultant to "+((TaskRow)event.getItem()).getTaskName(), VaadinIcons.PLUS, selectedItem -> {
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
                        Taskworkerconstraint taskworkerconstraint = new Taskworkerconstraint(0.0, userComboBox.getSelectedItem().get(), ((TaskRow) event.getItem()).getTask());
                        taskworkerconstraintRepository.save(taskworkerconstraint);
                        subWindow.close();
                        updateTreeGrid();
                    });
                    subContent.addComponent(addButton);

                    // Center it in the browser window
                    subWindow.center();

                    // Open it in the UI
                    UI.getCurrent().addWindow(subWindow);
                });
            } else {
                event.getContextMenu().addItem("Remove "+((UserRow)event.getItem()).getUsername(), VaadinIcons.CLOSE,
                        selectedItem -> Notification.show("Not possible at this time!"));
            }
        } else {
            event.getContextMenu().addItem("Add Task", VaadinIcons.PLUS, selectedItem -> {
                Task task = new Task("new task", currentProject);
                currentProject.getTasks().add(task);
                taskRepository.save(task);
                updateTreeGrid();
            });
        }
    }

    private void updateGridHeaderMenu(GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<TaskRow> event) {
        event.getContextMenu().removeItems();
        if (event.getColumn() != null) {
            event.getContextMenu().addItem("Sort Ascending", selectedItem ->
                    treeGrid.sort((Grid.Column<TaskRow, ?>) event.getColumn(), SortDirection.ASCENDING));
            event.getContextMenu().addItem("Sort Descending", selectedItem ->
                    treeGrid.sort((Grid.Column<TaskRow, ?>) event.getColumn(), SortDirection.DESCENDING));
        } else {
            event.getContextMenu().addItem("menu is empty", null);
        }
    }
    */
}
