package dk.trustworks.invoicewebui.web.stats.components;

import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import dk.trustworks.invoicewebui.model.Expense;
import dk.trustworks.invoicewebui.model.dto.UserExpenseDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.services.ExpenseService;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.model.stats.ExpenseItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 20/09/2017.
 */

@SpringComponent
@SpringUI
public class ExpenseTable {

    private final StatisticsService statisticsService;

    private final UserService userService;

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseTable(StatisticsService statisticsService, UserService userService, ExpenseService expenseService) {
        this.statisticsService = statisticsService;
        this.userService = userService;
        this.expenseService = expenseService;
    }

    public Grid<ExpenseItem> createRevenuePerConsultantChart() {
        LocalDate startDate = LocalDate.of(2014, 7, 1);
        int months = (int) ChronoUnit.MONTHS.between(startDate, LocalDate.now())+2;

        Map<String, ExpenseItem> expenseItemList = new HashMap<>();

        LocalDate currentDate = startDate;
        for (ExcelExpenseType expenseType : ExcelExpenseType.values()) {
            expenseItemList.put(expenseType.name(), new ExpenseItem(Math.random(), "1_"+expenseType.getText(), new double[months+2]));
        }
        expenseItemList.put("2_SUM", new ExpenseItem(Math.random(), "2_Sum", new double[months+2]));
        expenseItemList.put("3_SHARED_EXP", new ExpenseItem(Math.random(), "3_Shared expenses", new double[months+2]));
        expenseItemList.put("3_SHARED_SAL", new ExpenseItem(Math.random(), "3_Shared salaries", new double[months+2]));
        expenseItemList.put("3_SALARIES", new ExpenseItem(Math.random(), "3_Salaries", new double[months+2]));
        expenseItemList.put("4_SUM", new ExpenseItem(Math.random(), "4_Sum", new double[months+2]));

        for (int j = 0; j < months; j++) {
            double sum = 0.0;
            for (Expense expense : expenseService.findByMonth(currentDate.plusMonths(j))) {
                expenseItemList.get(expense.getExpensetype().name()).getExpenses()[j] = expense.getAmount();
                sum += expense.getAmount();
            }
            expenseItemList.get("2_SUM").getExpenses()[j] = sum;
        }

        for (int i = 0; i < months; i++) {
            currentDate = startDate.plusMonths(i);
            double sharedExpenses = 0.0;
            double staffSalaries = 0.0;
            for (UserExpenseDocument userExpenseDocument : statisticsService.getConsultantsExpensesByMonth(currentDate)) {
                staffSalaries += userExpenseDocument.getStaffSalaries();
                sharedExpenses += userExpenseDocument.getSharedExpense();
            }

            //double sharedExpenses = NumberUtils.round(statisticsService.getSharedExpensesAndStaffSalariesByMonth(currentDate), 0);
            expenseItemList.get("3_SHARED_EXP").getExpenses()[i] = sharedExpenses;
            expenseItemList.get("3_SHARED_SAL").getExpenses()[i] = staffSalaries;
            //long salaries = Math.round((statisticsService.getAllExpensesByMonth(currentDate) - sharedExpenses - staffSalaries));
            int salaries = userService.calcMonthSalaries(currentDate, ConsultantType.CONSULTANT.toString());
            expenseItemList.get("3_SALARIES").getExpenses()[i] = salaries;
            double sum = sharedExpenses + staffSalaries + salaries;
            expenseItemList.get("4_SUM").getExpenses()[i] = sum;
        }

        Grid<ExpenseItem> treeGrid = new Grid<>();
        treeGrid.setWidth(100, Sizeable.Unit.PERCENTAGE);
        treeGrid.setItems(expenseItemList.values());

        treeGrid.addColumn(ExpenseItem::getName).setCaption("Expense name").setId("name-column");
        treeGrid.setFrozenColumnCount(1);
        int c = 0;
        for (int i = 24; i < months; i++) {
            int finalI = i;
            treeGrid.addColumn(expenseItem -> Math.round(expenseItem.getExpenses()[finalI])).setCaption(DateUtils.stringIt(LocalDate.of(2014,7,1).plusMonths(i), "MMM/yy")).setId("date_"+c);
            c++;
        }

        return treeGrid;
    }

}