package dk.trustworks.invoicewebui.web.project.views;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.contextmenu.GridContextMenu;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.components.grid.HeaderRow;
import dk.trustworks.invoicewebui.network.clients.*;
import dk.trustworks.invoicewebui.network.dto.*;
import dk.trustworks.invoicewebui.web.project.components.ProjectDetailCardDesign;
import dk.trustworks.invoicewebui.web.project.components.ProjectDetailCardImpl;
import dk.trustworks.invoicewebui.web.project.components.ProjectMapLocationImpl;
import dk.trustworks.invoicewebui.web.project.model.TaskRow;
import dk.trustworks.invoicewebui.web.project.model.UserRow;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private ResponsiveLayout responsiveLayout;

    private Project currentProject;

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
            currentProject = event.getValue();
            if(responsiveLayout!=null) removeComponent(responsiveLayout);
            createDetailLayout();
        });
    }

    private void createDetailLayout() {
        responsiveLayout = new ResponsiveLayout();
        addComponent(responsiveLayout);

        ResponsiveRow clientDetailsRow = responsiveLayout.addRow().withStyleName("dark-blue");
        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(new ProjectDetailCardImpl(currentProject, userClient.findAllUsers().getContent().stream().map(u -> u.getContent()).collect(Collectors.toList()), null));

        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(projectMapLocation);

        ResponsiveRow budgetRow = responsiveLayout.addRow();
        budgetRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(createTreeGrid());
    }

    private TreeGrid createTreeGrid() {
        LocalDate actualDate = new LocalDate(currentProject.getStartdate().getYear(),
                currentProject.getStartdate().getMonthValue(),
                currentProject.getStartdate().getDayOfMonth());
        LocalDate endDate = new LocalDate(currentProject.getEnddate().getYear(),
                currentProject.getEnddate().getMonthValue(),
                currentProject.getEnddate().getDayOfMonth());
        Months monthsBetween = Months.monthsBetween(actualDate, endDate);
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
            TaskRow taskRow = new TaskRow(task.getUuid(), task.getName(), monthsBetween.getMonths());
            taskRows.add(taskRow);
            System.out.println("task = " + task);
            for (User user : usersMap.values()) {
                if(user.getUsername().equals("hans.lassen")) System.out.println("user = " + user);
                LocalDate budgetDate = actualDate;
                Optional<Taskworkerconstraint> taskworkerconstraint = taskworkerconstraints.stream()
                        .filter(p ->
                                p.getTaskuuid()!=null &&
                                p.getUseruuid()!=null &&
                                p.getTaskuuid().equals(task.getUuid()) &&
                                p.getUseruuid().equals(user.getUuid()))
                        .findFirst();
                if(user.getUsername().equals("hans.lassen")) System.out.println("taskworkerconstraint = " + taskworkerconstraint);

                if(!taskworkerconstraint.isPresent()) continue;

                UserRow userRow = new UserRow(task.getUuid(), task.getName(), monthsBetween.getMonths(),
                        user.getUuid(), user.getUsername(), taskworkerconstraint.get().getPrice());

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
                        userRow.setMonth(month, budget.get().getBudget() / taskworkerconstraint.get().getPrice());
                    } else {
                        userRow.setMonth(month, 0.0);
                    }
                    month++;
                    budgetDate = budgetDate.plusMonths(1);
                }
                taskRow.addUserRow(userRow);
                System.out.println("userRow = " + userRow);
            }
            System.out.println("taskRow = " + taskRow);
        }


        TreeGrid<TaskRow> treeGrid = new TreeGrid<>();
        treeGrid.addColumn(TaskRow::getTaskName).setWidth(200).setCaption("Task Name").setId("name-column");
        treeGrid.addColumn(TaskRow::getUsername).setWidth(200).setCaption("Consultant");
        treeGrid.addColumn(TaskRow::getRate).setWidth(100).setCaption("Rate");
        treeGrid.setFrozenColumnCount(3);

        GridContextMenu<TaskRow> gridMenu = new GridContextMenu<>(treeGrid);
        gridMenu.addGridBodyContextMenuListener(this::updateGridBodyMenu);

        int month = 0;
        int year = actualDate.getYear();
        List<String> yearColumns = new ArrayList<>();
        LocalDate budgetDate = actualDate;
        while(budgetDate.isBefore(endDate)) {
            final LocalDate filterDate = budgetDate;
            final int actualMonth = month;
            Grid.Column<?, ?> budgetColumn = treeGrid.addColumn(
                    taskRow -> taskRow.getMonth(actualMonth))
                    .setStyleGenerator(budgetHistory -> "align-right")
                    .setWidth(100)
                    .setId(Month.of(filterDate.getMonthOfYear()).name()+filterDate.getYear())
                    .setCaption(Month.of(filterDate.getMonthOfYear()).getDisplayName(TextStyle.SHORT, Locale.ENGLISH)+" "+filterDate.year().getAsShortText());
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
        treeGrid.setItems(taskRows, TaskRow::getUserRows);
        return treeGrid;
    }

    private void updateGridBodyMenu(GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<TaskRow> event) {
        event.getContextMenu().removeItems();
        if (event.getItem() != null) {
            if(event.getItem().getClass().equals(TaskRow.class)) {
                event.getContextMenu().addItem("Add Consultant to "+((TaskRow)event.getItem()).getTaskName(), VaadinIcons.PLUS, selectedItem -> {
                    Notification.show("Add Consultant selected");
                });
            } else {
                event.getContextMenu().addItem("Remove "+((UserRow)event.getItem()).getUsername(), VaadinIcons.CLOSE, selectedItem -> {
                    Notification.show("Remove Consultant selected");
                });
            }
        } else {
            event.getContextMenu().addItem("Add Task", VaadinIcons.PLUS, selectedItem -> {
                Notification.show("Add Task selected");
            });
        }
    }
}
