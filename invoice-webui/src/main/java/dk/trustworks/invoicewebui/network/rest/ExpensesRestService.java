package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Expense;
import dk.trustworks.invoicewebui.model.ExpenseDetails;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.network.dto.enums.EconomicAccountGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.GET;

@Service
public class ExpensesRestService {

    @Value("#{environment.EXPENSESERVICE_URL}")
    private String expenseServiceUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public ExpensesRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<ExpenseDetails> findExpenseDetailsByGroup(EconomicAccountGroup accountGroup) {
        String url = expenseServiceUrl +"/expensesdetails/"+accountGroup.name();
        return Arrays.asList((ExpenseDetails[]) systemRestService.secureCall(url, GET, ExpenseDetails[].class).getBody());
    }

    public List<Expense> findByAccountAndPeriod(ExcelExpenseType expenseType, LocalDate from, LocalDate to) {
        String url = expenseServiceUrl + "/expenses/"+expenseType+"/search/findByPeriod?from="+stringIt(from)+"&to="+stringIt(to);
        return Arrays.asList((Expense[]) systemRestService.secureCall(url, GET, Expense[].class).getBody());
    }

    public List<ExpenseDetails> findByExpensedateAndAccountnumbers(LocalDate expensedate, int[] accountnumbers) {
        String url = expenseServiceUrl + "/expensedetails/search/findByExpenseMonthAndAccountnumbers?month=" +
                stringIt(expensedate)+"&accountNumbers=" +
                Arrays.stream(accountnumbers)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(","));
        return Arrays.asList((ExpenseDetails[]) systemRestService.secureCall(url, GET, ExpenseDetails[].class).getBody());
    }

    public List<Expense> findByMonth(LocalDate month) {
        String url = expenseServiceUrl + "/expenses/search/findByMonth?month="+stringIt(month.withDayOfMonth(1));
        return Arrays.asList((Expense[]) systemRestService.secureCall(url, GET, Expense[].class).getBody());
    }
}
