package dk.trustworks.invoicewebui.services;


import dk.trustworks.invoicewebui.model.Expense;
import dk.trustworks.invoicewebui.model.ExpenseDetails;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.network.dto.enums.EconomicAccountGroup;
import dk.trustworks.invoicewebui.network.rest.ExpensesRestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService implements InitializingBean {

    private static ExpenseService instance;

    private final ExpensesRestService expensesRestService;

    @Autowired
    public ExpenseService(ExpensesRestService expensesRestService) {
        this.expensesRestService = expensesRestService;
    }

    public List<ExpenseDetails> findByExpensedateAndAccountnumberInOrderByAmountDesc(LocalDate expensedate, int... accountnumber) {
        return expensesRestService.findByExpensedateAndAccountnumbers(expensedate, accountnumber);
    }

    public List<Expense> findByMonth(LocalDate month) {
        return expensesRestService.findByMonth(month).stream().sorted(Comparator.comparingDouble(Expense::getAmount)).collect(Collectors.toList());
    }

    public List<Expense> findByAccountAndPeriod(ExcelExpenseType expenseType, LocalDate from, LocalDate to) {
        return expensesRestService.findByAccountAndPeriod(expenseType, from, to);
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static ExpenseService get() {
        return instance;
    }

    public List<ExpenseDetails> findExpenseDetailsByGroup(EconomicAccountGroup accountGroup) {
        return expensesRestService.findExpenseDetailsByGroup(accountGroup);
    }
}
