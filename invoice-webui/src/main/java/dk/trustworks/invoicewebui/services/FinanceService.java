package dk.trustworks.invoicewebui.services;


import dk.trustworks.invoicewebui.model.CompanyExpense;
import dk.trustworks.invoicewebui.model.ExpenseDetails;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.dto.FinanceDocument;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.network.dto.enums.EconomicAccountGroup;
import dk.trustworks.invoicewebui.network.rest.FinanceRestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinanceService implements InitializingBean {

    private static FinanceService instance;

    private final FinanceRestService financeRestService;

    @Autowired
    public FinanceService(FinanceRestService financeRestService) {
        this.financeRestService = financeRestService;
    }

    public List<ExpenseDetails> findByExpensedateAndAccountnumberInOrderByAmountDesc(LocalDate expensedate, int... accountnumber) {
        return financeRestService.findByExpensedateAndAccountnumbers(expensedate, accountnumber);
    }

    public List<CompanyExpense> findByMonth(LocalDate month) {
        return financeRestService.findByMonth(month).stream().sorted(Comparator.comparingDouble(CompanyExpense::getAmount)).collect(Collectors.toList());
    }

    public List<FinanceDocument> getAllExpensesByMonth(LocalDate month) {
        return financeRestService.getAllExpensesByMonth(month);
    }

    public List<CompanyExpense> findByAccountAndPeriod(ExcelExpenseType expenseType, LocalDate from, LocalDate to) {
        return financeRestService.findByAccountAndPeriod(expenseType, from, to);
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static FinanceService get() {
        return instance;
    }

    public List<ExpenseDetails> findExpenseDetailsByGroup(EconomicAccountGroup accountGroup) {
        return financeRestService.findExpenseDetailsByGroup(accountGroup);
    }

    public List<FinanceDocument> findExpensesPeriod(LocalDate periodStart, LocalDate periodEnd) {
        return financeRestService.findExpensesPeriod(periodStart, periodEnd);
    }

    public double calcAllExpensesByMonth(LocalDate datemonth) {
        return financeRestService.calcAllExpensesByMonth(datemonth);
    }

    public GraphKeyValue[] getPayoutsByPeriod(LocalDate fromdate, LocalDate todate) {
        return financeRestService.getPayoutsByPeriod(fromdate, todate);
    }
}
