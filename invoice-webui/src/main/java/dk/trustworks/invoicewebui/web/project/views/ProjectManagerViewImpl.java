package dk.trustworks.invoicewebui.web.project.views;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.contextmenu.GridContextMenu;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Setter;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.network.clients.*;
import dk.trustworks.invoicewebui.network.dto.*;
import dk.trustworks.invoicewebui.web.project.components.ProjectDetailCardImpl;
import dk.trustworks.invoicewebui.web.project.components.ProjectMapLocationImpl;
import dk.trustworks.invoicewebui.web.project.model.TaskRow;
import dk.trustworks.invoicewebui.web.project.model.UserRow;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import javax.annotation.PostConstruct;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hans on 21/08/2017.
 */

@SpringView(name = ProjectManagerViewImpl.VIEW_NAME)
public class ProjectManagerViewImpl extends ProjectManagerViewDesign implements View {

    public static final String VIEW_NAME = "project";

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProjectClient projectClient;

    @Autowired
    private ProjectMapLocationImpl projectMapLocation;

    @Autowired
    private TaskClient taskClient;

    @Autowired
    private TaskworkerconstraintClient taskworkerconstraintClient;

    @Autowired
    private BudgetClient budgetClient;

    @Autowired
    private LogoClient logoClient;

    private ResponsiveLayout responsiveLayout;

    private Project currentProject;

    private TreeGrid<TaskRow> treeGrid;

    @PostConstruct
    void init() {
        getSelProject().setItemCaptionGenerator(Project::getName);
        Resources<Resource<Project>> projectResources = projectClient.findAllProjects();
        List<Project> projects = new ArrayList<>();
        for (Resource<Project> projectResource : projectResources.getContent()) {
            projects.add(projectResource.getContent());
        }
        getSelProject().setItems(projects);
        getSelProject().addValueChangeListener(event -> {
            reloadGrid();
        });
    }

    private void createDetailLayout() {
        responsiveLayout = new ResponsiveLayout();
        addComponent(responsiveLayout);

        Resource<Logo> logoResource = logoClient.findByClientuuid(currentProject.getClientuuid());

        ResponsiveRow clientDetailsRow = responsiveLayout.addRow().withStyleName("dark-blue");
        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(new ProjectDetailCardImpl(currentProject, userClient.findAllUsers().getContent().stream().map(u -> u.getContent()).collect(Collectors.toList()), logoResource, projectClient));

        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(projectMapLocation);

        treeGrid = createTreeGrid();

        ResponsiveRow budgetRow = responsiveLayout.addRow();
        budgetRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(treeGrid);
    }

    private TreeGrid createTreeGrid() {
        LocalDate startDate = new LocalDate(currentProject.getStartdate().getYear(),
                currentProject.getStartdate().getMonthValue(),
                currentProject.getStartdate().getDayOfMonth());
        LocalDate endDate = new LocalDate(currentProject.getEnddate().getYear(),
                currentProject.getEnddate().getMonthValue(),
                currentProject.getEnddate().getDayOfMonth());
        Months monthsBetween = Months.monthsBetween(startDate, endDate);
        System.out.println("period.getMonths() = " + monthsBetween.getMonths());

        List<Task> tasks = new ArrayList<>();
        List<TaskRow> taskRows = new ArrayList<>();
        for (Resource<Task> taskResource : taskClient.findByProjectuuid(currentProject.getUuid()).getContent()) {
            tasks.add(taskResource.getContent());
        }

        Map<String, User> usersMap = userClient.findAllUsers().getContent().stream().map(u -> u.getContent()).collect(Collectors.toMap(User::getUuid, user -> user));
        /*
        for (Resource<User> userResource : userClient.findAllUsers().getContent()) {
            usersMap.put(userResource.getContent().getUuid(), userResource.getContent());
            System.out.println("userResource.getContent() = " + userResource.getContent());
        }*/

        List<Budget> budgets = new ArrayList<>();
        for (Resource<Budget> budgetResource : budgetClient.findAllBudgets().getContent()) {
            budgets.add(budgetResource.getContent());
        }

        List<Taskworkerconstraint> taskworkerconstraints = new ArrayList<>();
        for (Resource<Taskworkerconstraint> taskResource : taskworkerconstraintClient.findAllTaskworkerconstraints().getContent()) {
            taskworkerconstraints.add(taskResource.getContent());
        }
        for (Task task : tasks) {
            TaskRow taskRow = new TaskRow(task, monthsBetween.getMonths());
            taskRows.add(taskRow);
            System.out.println("task = " + task);
            for (User user : usersMap.values()) {
                if(user.getUsername().equals("hans.lassen")) System.out.println("user = " + user);
                LocalDate budgetDate = startDate;
                Optional<Taskworkerconstraint> taskworkerconstraint = taskworkerconstraints.stream()
                        .filter(p ->
                                p.getTaskuuid()!=null &&
                                p.getUseruuid()!=null &&
                                p.getTaskuuid().equals(task.getUuid()) &&
                                p.getUseruuid().equals(user.getUuid()))
                        .findFirst();
                if(user.getUsername().equals("hans.lassen")) System.out.println("taskworkerconstraint = " + taskworkerconstraint);

                if(!taskworkerconstraint.isPresent()) continue;

                UserRow userRow = new UserRow(task, taskworkerconstraint.get(), monthsBetween.getMonths(),
                        user.getUuid(), user.getUsername());

                if(user.getUsername().equals("hans.lassen")) System.out.println("budgets = " + budgets.size());

                int month = 0;
                while(budgetDate.isBefore(endDate)) {
                    final LocalDate filterDate = budgetDate;

                    Optional<Budget> budget = budgets.stream()
                            .filter(p -> p.getYear()==filterDate.getYear() &&
                                    p.getMonth()==filterDate.getMonthOfYear()-1 &&
                                    p.getTaskuuid()!=null &&
                                    p.getUseruuid()!=null &&
                                    p.getTaskuuid().equals(task.getUuid()) &&
                                    p.getUseruuid().equals(user.getUuid()))
                            .findFirst();

                    if(budget.isPresent()) {
                        if(user.getUsername().equals("hans.lassen")) System.out.println("budget.get() = " + budget.get());
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
                taskworkerconstraintClient.save(taskworkerconstraint.getUuid(), taskworkerconstraint);
                LocalDate budgetCountDate = startDate;
                List<Budget> budgetList = new ArrayList<>();
                for (String budgetString : userRow.getBudget()) {
                    Budget budget = new Budget(
                            budgetCountDate.getMonthOfYear()-1,
                            budgetCountDate.getYear(),
                            Double.parseDouble(budgetString) * taskworkerconstraint.getPrice(),
                            userRow.getUserUUID(),
                            userRow.getTask().getUuid()
                    );
                    budgetList.add(budget);
                    budgetCountDate = budgetCountDate.plusMonths(1);
                }
                budgetClient.save(budgetList);
            } else {
                taskClient.save(taskRow.getTask().getUuid(), new Task(taskRow.getTaskName()));
            }
        });
        treeGrid.setItems(taskRows, TaskRow::getUserRows);
        return treeGrid;
    }

    private void reloadGrid() {
        currentProject = getSelProject().getValue();
        if(responsiveLayout!=null) removeComponent(responsiveLayout);
        createDetailLayout();
    }

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
                    userComboBox.setItems(userClient.findAllActiveUsers().getContent()
                            .stream().map(userResource -> userResource.getContent()));
                    userComboBox.setItemCaptionGenerator(User::getUsername);
                    subContent.addComponent(userComboBox);
                    Button addButton = new Button("Add");
                    addButton.addClickListener(event1 -> {
                        TaskworkerconstraintCreate taskworkerconstraint = new TaskworkerconstraintCreate(0.0, ((TaskRow) event.getItem()).getTask().getUuid(), userComboBox.getSelectedItem().get().getUuid());
                        taskworkerconstraintClient.create(taskworkerconstraint);
                        reloadGrid();
                    });
                    subContent.addComponent(addButton);

                    // Center it in the browser window
                    subWindow.center();

                    // Open it in the UI
                    UI.getCurrent().addWindow(subWindow);
                });
            } else {
                event.getContextMenu().addItem("Remove "+((UserRow)event.getItem()).getUsername(), VaadinIcons.CLOSE, selectedItem -> {
                    Notification.show("Not possible at this time!");
                });
            }
        } else {
            event.getContextMenu().addItem("Add Task", VaadinIcons.PLUS, selectedItem -> {
                Task task = new Task("new task", currentProject.getUuid());
                taskClient.create(task);
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
