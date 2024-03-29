package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.CompanyExpense;
import dk.trustworks.invoicewebui.model.ExpenseDetails;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.dto.FinanceDocument;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.network.dto.KeyValueDTO;
import dk.trustworks.invoicewebui.network.dto.enums.EconomicAccountGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    protected static Logger log = LoggerFactory.getLogger(FinanceRestService.class.getName());

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public FinanceRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<ExpenseDetails> findExpenseDetailsByGroup(EconomicAccountGroup accountGroup) {
        String url = apiGatewayUrl +"/company/expenses/entries/"+accountGroup.name();
        log.info("findExpenseDetailsByGroup: " + url);
        return Arrays.asList((ExpenseDetails[]) systemRestService.secureCall(url, GET, ExpenseDetails[].class).getBody());
    }

    public List<CompanyExpense> findByAccountAndPeriod(ExcelExpenseType expenseType, LocalDate from, LocalDate to) {
        String url = apiGatewayUrl + "/company/expenses/"+expenseType+"/search/findByPeriod?from="+stringIt(from)+"&to="+stringIt(to);
        return Arrays.asList((CompanyExpense[]) systemRestService.secureCall(url, GET, CompanyExpense[].class).getBody());
    }

    public List<ExpenseDetails> findByExpensedateAndAccountnumbers(LocalDate expensedate, int[] accountnumbers) {
        String url = apiGatewayUrl + "/expensedetails/search/findByExpenseMonthAndAccountnumbers?month=" +
                stringIt(expensedate)+"&accountNumbers=" +
                Arrays.stream(accountnumbers)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(","));
        return Arrays.asList((ExpenseDetails[]) systemRestService.secureCall(url, GET, ExpenseDetails[].class).getBody());
    }

    public List<CompanyExpense> findByMonth(LocalDate month) {
        String url = apiGatewayUrl + "/company/expenses/search/findByMonth?month="+stringIt(month.withDayOfMonth(1));
        return Arrays.asList((CompanyExpense[]) systemRestService.secureCall(url, GET, CompanyExpense[].class).getBody());
    }

    public List<FinanceDocument> getAllExpensesByMonth(LocalDate month) {
        String url = apiGatewayUrl +"/company/expensedocuments/datemonths/"+stringIt(month);
        ResponseEntity<FinanceDocument[]> result = systemRestService.secureCall(url, GET, FinanceDocument[].class);
        return Arrays.asList(result.getBody());
    }

    public List<FinanceDocument> findExpensesPeriod(LocalDate fromdate, LocalDate todate) {
        String url = apiGatewayUrl +"/company/expensedocuments?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate);
        ResponseEntity<FinanceDocument[]> result = systemRestService.secureCall(url, GET, FinanceDocument[].class);
        return Arrays.asList(result.getBody());
    }

    public double calcAllExpensesByMonth(LocalDate datemonth) {
        String url = apiGatewayUrl + "/company/expenses/datemonths/"+stringIt(datemonth.withDayOfMonth(1))+"/sum";
        return Double.parseDouble(((KeyValueDTO) systemRestService.secureCall(url, GET, KeyValueDTO.class).getBody()).getValue());
    }

    public GraphKeyValue[] getPayoutsByPeriod(LocalDate fromdate, LocalDate todate) {
        String url = apiGatewayUrl +"/company/bonus?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate);
        return (GraphKeyValue[]) systemRestService.secureCall(url, GET, GraphKeyValue[].class).getBody();
    }

    /* TODO

    @GET
    @Path("/expenses")
    @RolesAllowed({"TEAMLEAD", "CXO", "ADMIN"})
    public List<UserFinanceDocument> getConsultantsExpensesByMonth(LocalDate month) {
        List<User> users = userAPI.findUsersByDateAndStatusListAndTypes(stringIt(month), "ACTIVE, NON_PAY_LEAVE", "CONSULTANT", "true");
        return financeService.getUserFinanceData().stream().filter(userFinanceDocument -> users.stream().map(User::getUuid).anyMatch(s -> s.equals(userFinanceDocument.getUser().getUuid()))).collect(Collectors.toList());
    }
     */
}
