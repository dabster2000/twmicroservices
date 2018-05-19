package dk.trustworks.invoicewebui.web.time.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.Budget;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.BudgetRepository;
import dk.trustworks.invoicewebui.repositories.ProjectRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.web.time.model.BudgetRemainingItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class BudgetReportImpl extends BudgetReportDesign {

    private final ProjectRepository projectRepository;

    private final WorkRepository workRepository;

    private final BudgetRepository budgetRepository;

    private final ContractService contractService;

    @Autowired
    public BudgetReportImpl(ProjectRepository projectRepository, WorkRepository workRepository, BudgetRepository budgetRepository, ContractService contractService) {
        this.projectRepository = projectRepository;
        this.workRepository = workRepository;
        this.budgetRepository = budgetRepository;
        this.contractService = contractService;
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.USER})
    public void init(String projectUUID) {
        List<Task> tasks = projectRepository.findOne(projectUUID).getTasks();

        List<BudgetRemainingItem> budgetRemainingItems = new ArrayList<>();
        for (Task task : tasks) {
            //List<Work> workList = task.getWorkList();
            List<Work> workList = workRepository.findByTask(task.getUuid());
            //List<Taskworkerconstraint> taskworkerconstraints = task.getTaskworkerconstraint();
            for (Work work : workList) {
                BudgetRemainingItem budgetRemainingItem = null;
                for (BudgetRemainingItem item : budgetRemainingItems) {
                    if(item.getUserUUID().equals(work.getUser().getUuid())
                        && item.getTaskUUID().equals(work.getTask().getUuid())) {
                        budgetRemainingItem = item;
                    }
                }
                if(budgetRemainingItem == null) {
                    budgetRemainingItem = new BudgetRemainingItem(
                            work.getUser().getUuid(),
                            work.getTask().getUuid(),
                            work.getTask().getName(),
                            work.getUser().getUsername());
                    budgetRemainingItems.add(budgetRemainingItem);
                }

                /*
                Optional<Taskworkerconstraint> taskworkerconstraint = work.getTask().getTaskworkerconstraint().stream().
                        filter(p -> p.getUser().getUuid().equals(work.getUser().getUuid())
                                && p.getTask().getUuid().equals(work.getTask().getUuid())).
                        findFirst();
*/
                Double rate = contractService.findConsultantRateByWork(work);
                if(rate!=null) budgetRemainingItem.addUsedBudget(work.getWorkduration()*rate);
            }

            List<Budget> budgets = budgetRepository.findByTaskuuid(task.getUuid());//task.getBudget();
            for (Budget budget : budgets) {
                BudgetRemainingItem budgetRemainingItem = null;
                for (BudgetRemainingItem item : budgetRemainingItems) {
                    if(item.getUserUUID().equals(budget.getUser().getUuid())
                            && item.getTaskUUID().equals(budget.getTask().getUuid())) {
                        budgetRemainingItem = item;
                    }
                }
                if(budgetRemainingItem == null) {
                    budgetRemainingItem = new BudgetRemainingItem(
                            budget.getUser().getUuid(),
                            budget.getTask().getUuid(),
                            budget.getTask().getName(),
                            budget.getUser().getUsername());
                    budgetRemainingItems.add(budgetRemainingItem);
                }

                budgetRemainingItem.addTotalBudget(budget.getBudget());
            }
        }

        budgetRemainingItems = budgetRemainingItems.stream().filter(p -> p.getTotalBudget() > 1.0).collect(Collectors.toList());

        String[] xAxisCategories = new String[budgetRemainingItems.size()];
        Number[] totalBudgetList = new Number[budgetRemainingItems.size()];
        Number[] usedBudgetList = new Number[budgetRemainingItems.size()];
        int i = 0;
        for (BudgetRemainingItem budgetRemainingItem : budgetRemainingItems) {
            xAxisCategories[i] = budgetRemainingItem.getProjectTaskName()+" ("+budgetRemainingItem.getUsername()+")";
            totalBudgetList[i] = budgetRemainingItem.getTotalBudget()-budgetRemainingItem.getUsedBudget();
            usedBudgetList[i] = budgetRemainingItem.getUsedBudget();
            i++;
        }
        getContainer().removeAllComponents();
        getContainer().addComponent(getChart(xAxisCategories, totalBudgetList, usedBudgetList));
    }

    protected Component getChart(String[] xAxisCategories, Number[] totalBudgetList, Number[] usedBudgetList) {
        Chart chart = new Chart(ChartType.COLUMN);

        Configuration conf = chart.getConfiguration();

        conf.setTitle(new Title("Budgets"));

        XAxis xAxis = new XAxis();
        xAxis.setCategories(xAxisCategories);
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setMin(0);
        yAxis.setTitle(new AxisTitle("Kr"));
        conf.addyAxis(yAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ this.y +' ('+ Math.round(this.percentage) +'%)'");
        conf.setTooltip(tooltip);

        PlotOptionsColumn plotOptions = new PlotOptionsColumn();
        plotOptions.setStacking(Stacking.NORMAL);
        conf.setPlotOptions(plotOptions);

        conf.addSeries(new ListSeries("Remaining Budget", totalBudgetList));
        conf.addSeries(new ListSeries("Used Budget", usedBudgetList));

        chart.drawChart(conf);

        return chart;
    }
}
