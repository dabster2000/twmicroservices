package dk.trustworks.invoicewebui.web.project.views;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TreeGrid;
import dk.trustworks.invoicewebui.network.clients.*;
import dk.trustworks.invoicewebui.network.dto.*;
import dk.trustworks.invoicewebui.web.project.components.ProjectDetailCardDesign;
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
import java.util.*;
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
                .withComponent(new ProjectDetailCardDesign());

        clientDetailsRow.addColumn()
                .withDisplayRules(12, 12, 6, 6)
                .withComponent(projectMapLocation);

        ResponsiveRow budgetRow = responsiveLayout.addRow();
        budgetRow.addColumn()
                .withDisplayRules(12, 12, 12, 12)
                .withComponent(createTreeGrid());
    }

    private TreeGrid createTreeGrid() {
        LocalDate actualDate = new LocalDate(currentProject.getStartdate());
        LocalDate endDate = new LocalDate(currentProject.getEnddate());
        Months monthsBetween = Months.monthsBetween(actualDate, endDate);
        System.out.println("period.getMonths() = " + monthsBetween.getMonths());

        List<Task> tasks = new ArrayList<>();
        List<TaskRow> taskRows = new ArrayList<>();
        for (Resource<Task> taskResource : taskClient.findByProjectuuid(currentProject.getUuid()).getContent()) {
            tasks.add(taskResource.getContent());
        }

        Map<String, User> usersMap = new HashMap<>();
        for (Resource<User> userResource : userClient.findAllUsers().getContent()) {
            usersMap.put(userResource.getContent().getUuid(), userResource.getContent());
        }

        Map<String, List<Budget>> budgetMap = new HashMap<>();
        List<Budget> budgets = new ArrayList<>();
        //System.out.println("XXXXXXXXX BUDGET XXXXXXXXX");
        for (Resource<Budget> budgetResource : budgetClient.findAllBudgets().getContent()) {
            //System.out.println("budgetResource.getContent() = " + budgetResource.getContent());
            budgets.add(budgetResource.getContent());
            if(!budgetMap.containsKey(budgetResource.getContent().getUseruuid()+budgetResource.getContent().getTaskuuid())) {
                ArrayList<Budget> budgetsValue = new ArrayList<>();
                budgetsValue.add(budgetResource.getContent());
                budgetMap.put(
                        budgetResource.getContent().getUseruuid() + budgetResource.getContent().getTaskuuid(),
                        budgetsValue
                );
            } else {
                budgetMap.get(budgetResource.getContent().getUseruuid()+budgetResource.getContent().getTaskuuid())
                        .add(budgetResource.getContent());
            }
        }
        //System.out.println("XXXXXXXXX BUDGET XXXXXXXXX");
        //System.out.println("budgetMap = " + budgetMap.values().size());

        List<Taskworkerconstraint> taskworkerconstraints = new ArrayList<>();
        Map<String, Taskworkerconstraint> taskworkerconstraintMap = new HashMap<>();
        for (Resource<Taskworkerconstraint> taskResource : taskworkerconstraintClient.findAllTaskworkerconstraints().getContent()) {
            taskworkerconstraints.add(taskResource.getContent());
            taskworkerconstraintMap.put(
                    taskResource.getContent().getUseruuid()+taskResource.getContent().getTaskuuid(),
                    taskResource.getContent()
            );
        }
        for (Task task : tasks) {
            TaskRow taskRow = new TaskRow(task.getUuid(), task.getName(), monthsBetween.getMonths());
            taskRows.add(taskRow);
            System.out.println("task = " + task);
            for (User user : usersMap.values()) {
                if(user.getUsername().equals("hans.lassen")) System.out.println("user = " + user);
                LocalDate budgetDate = actualDate;
                //Taskworkerconstraint taskworkerconstraint = taskworkerconstraintMap.get(user.getUuid() + task.getUuid());
                Optional<Taskworkerconstraint> taskworkerconstraint = taskworkerconstraints.stream()
                        .filter(p ->
                                p.getTaskuuid()!=null &&
                                p.getUseruuid()!=null &&
                                p.getTaskuuid().equals(task.getUuid()) &&
                                p.getUseruuid().equals(user.getUuid()))
                        .findFirst();

                if(!taskworkerconstraint.isPresent()) continue;
                if(user.getUsername().equals("hans.lassen")) System.out.println("task = " + task);

                UserRow userRow = new UserRow(task.getUuid(), task.getName(), monthsBetween.getMonths(),
                        user.getUuid(), user.getUsername(), taskworkerconstraint.get().getPrice());

                //List<Budget> budgets = budgetMap.get(user.getUuid() + task.getUuid());
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
                        userRow.setMonth(month, budget.get().getBudget());
                    } else {
                        userRow.setMonth(month, 0.0);
                    }
                    month++;
                    budgetDate = budgetDate.plusMonths(1);
                }
                taskRow.addUserRow(userRow);
                System.out.println("userRow = " + userRow);
            }
        }


        TreeGrid<TaskRow> treeGrid = new TreeGrid<>();
        treeGrid.setWidth("100%");
        treeGrid.getEditor().setEnabled(true);
        treeGrid.setItems(taskRows, TaskRow::getUserRows);

        treeGrid.addColumn(TaskRow::getTaskName).setCaption("Task Name").setId("name-column");
        treeGrid.addColumn(TaskRow::getUsername).setCaption("Consultant");
        treeGrid.addColumn(TaskRow::getRate).setCaption("Rate");

        LocalDate budgetDate = actualDate;
        while(budgetDate.isBefore(endDate)) {
            final LocalDate filterDate = budgetDate;
            Grid.Column<?, ?> firstHalfColumn = treeGrid.addColumn(
                    taskRow -> taskRow.getMonth(filterDate.getMonthOfYear()))
                    .setStyleGenerator(budgetHistory -> "align-right")
                    .setId(Month.of(filterDate.getMonthOfYear()).name()+filterDate.getYear()).setCaption(Month.of(filterDate.getMonthOfYear()).name()+" "+filterDate.getYear());
            budgetDate = budgetDate.plusMonths(1);
        }
        return treeGrid;
    }
}
