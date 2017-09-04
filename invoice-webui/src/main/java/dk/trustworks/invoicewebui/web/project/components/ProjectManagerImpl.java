package dk.trustworks.invoicewebui.web.project.components;

import com.google.common.collect.Lists;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.contextmenu.GridContextMenu;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Setter;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.web.project.model.TaskRow;
import dk.trustworks.invoicewebui.web.project.model.UserRow;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hans on 21/08/2017.
 */

@SpringComponent
@SpringUI
public class ProjectManagerImpl extends ProjectManagerDesign {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskworkerconstraintRepository taskworkerconstraintRepository;

    @Autowired
    private ProjectMapLocationImpl projectMapLocation;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    private ResponsiveLayout responsiveLayout;

    private Project currentProject;

    private TreeGrid<TaskRow> treeGrid;

    @Transactional
    public ProjectManagerImpl init() {
        getSelProject().setItemCaptionGenerator(Project::getName);
        List<Project> projects = Lists.newArrayList(projectRepository.findAll());

        getSelProject().setItems(projects);
        getSelProject().addValueChangeListener(event -> {
            reloadGrid();
        });
        return this;
    }

    @Transactional
    private void createDetailLayout() {
        responsiveLayout = new ResponsiveLayout();
        addComponent(responsiveLayout);

        Logo logoResource = currentProject.getClient().getLogo();

        ResponsiveRow clientDetailsRow = responsiveLayout.addRow();
        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(new ProjectDetailCardImpl(currentProject, userRepository.findAll(), logoResource, projectRepository));

        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(projectMapLocation);

        BudgetCardDesign budgetCard = new BudgetCardDesign();
        treeGrid = createTreeGrid();
        budgetCard.getContainer().addComponent(treeGrid);

        ResponsiveRow budgetRow = responsiveLayout.addRow();
        budgetRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(budgetCard);
    }

    @Transactional
    private TreeGrid createTreeGrid() {
        LocalDate startDate = new LocalDate(currentProject.getStartdate().getYear(),
                currentProject.getStartdate().getMonthValue(),
                currentProject.getStartdate().getDayOfMonth());
        LocalDate endDate = new LocalDate(currentProject.getEnddate().getYear(),
                currentProject.getEnddate().getMonthValue(),
                currentProject.getEnddate().getDayOfMonth());
        Months monthsBetween = Months.monthsBetween(startDate, endDate);
        System.out.println("period.getMonths() = " + monthsBetween.getMonths());

        List<TaskRow> taskRows = new ArrayList<>();

        Map<String, User> usersMap = userRepository.findAll().stream().collect(Collectors.toMap(User::getUuid, user -> user));

        //ArrayList<Taskworkerconstraint> taskworkerconstraints = Lists.newArrayList(taskworkerconstraintRepository.findAll());

        //List<Budget> budgets = Lists.newArrayList(budgetRepository.findAll());

        for (Task task : currentProject.getTasks()) {
            List<Budget> budgets = budgetRepository.findByTaskuuid(task.getUuid());
            TaskRow taskRow = new TaskRow(task, monthsBetween.getMonths());
            taskRows.add(taskRow);
            System.out.println("task = " + task);
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
                if(user.getUsername().equals("hans.lassen")) System.out.println("taskworkerconstraint = " + taskworkerconstraint);

                if(!taskworkerconstraint.isPresent()) continue;

                UserRow userRow = new UserRow(task, taskworkerconstraint.get(), monthsBetween.getMonths(), user);

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
            } else {
                taskRow.getTask().setName(taskRow.getTaskName());
                taskRepository.save(taskRow.getTask());
            }
        });
        treeGrid.setItems(taskRows, TaskRow::getUserRows);
        return treeGrid;
    }

    @Transactional
    private void reloadGrid() {
        currentProject = getSelProject().getValue();
        if(responsiveLayout!=null) removeComponent(responsiveLayout);
        createDetailLayout();
    }

    @Transactional
    private void updateGridBodyMenu(GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<TaskRow> event) {
        event.getContextMenu().removeItems();
        if (event.getItem() != null) {
            if(event.getItem().getClass().equals(TaskRow.class)) {
                event.getContextMenu().addItem("Add Consultant to "+((TaskRow)event.getItem()).getTaskName(), VaadinIcons.PLUS, selectedItem -> {
                    Window subWindow = new Window("Sub-window");
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
                        reloadGrid();
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
                Task task = new Task("new task", currentProject.getUuid(), currentProject);
                taskRepository.save(task);
                reloadGrid();
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
}
