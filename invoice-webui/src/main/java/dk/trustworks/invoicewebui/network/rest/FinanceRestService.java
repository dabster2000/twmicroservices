package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.CompanyExpense;
import dk.trustworks.invoicewebui.model.ExpenseDetails;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.dto.FinanceDocument;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.network.dto.enums.EconomicAccountGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.GET;

@Service
public class FinanceRestService {

    @Value("#{environment.APISERVICE_URL}")
    private String apiServiceUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public FinanceRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<ExpenseDetails> findExpenseDetailsByGroup(EconomicAccountGroup accountGroup) {
        String url = apiServiceUrl +"/expensesdetails/"+accountGroup.name();
        return Arrays.asList((ExpenseDetails[]) systemRestService.secureCall(url, GET, ExpenseDetails[].class).getBody());
    }

    public List<CompanyExpense> findByAccountAndPeriod(ExcelExpenseType expenseType, LocalDate from, LocalDate to) {
        String url = apiServiceUrl + "/expenses/"+expenseType+"/search/findByPeriod?from="+stringIt(from)+"&to="+stringIt(to);
        return Arrays.asList((CompanyExpense[]) systemRestService.secureCall(url, GET, CompanyExpense[].class).getBody());
    }

    public List<ExpenseDetails> findByExpensedateAndAccountnumbers(LocalDate expensedate, int[] accountnumbers) {
        String url = apiServiceUrl + "/expensedetails/search/findByExpenseMonthAndAccountnumbers?month=" +
                stringIt(expensedate)+"&accountNumbers=" +
                Arrays.stream(accountnumbers)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(","));
        return Arrays.asList((ExpenseDetails[]) systemRestService.secureCall(url, GET, ExpenseDetails[].class).getBody());
    }

    public List<CompanyExpense> findByMonth(LocalDate month) {
        String url = apiServiceUrl + "/expenses/company/search/findByMonth?month="+stringIt(month.withDayOfMonth(1));
        return Arrays.asList((CompanyExpense[]) systemRestService.secureCall(url, GET, CompanyExpense[].class).getBody());
    }

    public List<FinanceDocument> getAllExpensesByMonth(LocalDate month) {
        String url = apiServiceUrl +"/cached/expenses/datemonths/"+stringIt(month);
        ResponseEntity<FinanceDocument[]> result = systemRestService.secureCall(url, GET, FinanceDocument[].class);
        return Arrays.asList(result.getBody());
    }

    public double calcAllExpensesByMonth(LocalDate datemonth) {
        String url = apiServiceUrl + "/expenses/company/datemonths/"+stringIt(datemonth.withDayOfMonth(1))+"/sum";
        return Arrays.stream((CompanyExpense[]) systemRestService.secureCall(url, GET, CompanyExpense[].class).getBody()).mapToDouble(CompanyExpense::getAmount).sum();
    }

    public GraphKeyValue[] getPayoutsByPeriod(LocalDate fromdate, LocalDate todate) {
        String url = apiServiceUrl +"/expenses/company/bonus?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate);
        return (GraphKeyValue[]) systemRestService.secureCall(url, GET, GraphKeyValue[].class).getBody();
    }
}
