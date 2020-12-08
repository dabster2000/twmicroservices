package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.BudgetNew;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.dto.BudgetDocument;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@JBossLog
@Service
public class BudgetRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public BudgetRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    @Cacheable("budgets")
    public List<GraphKeyValue> getBudgetsByPeriod(LocalDate fromdate, LocalDate todate) {
        String url = apiGatewayUrl +"/cached/budgets/datemonths?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate);
        List<GraphKeyValue> graphKeyValues;
        try {
            ResponseEntity<GraphKeyValue[]> result = systemRestService.secureCall(url, GET, GraphKeyValue[].class);
            graphKeyValues = Arrays.asList(result.getBody());
        } catch (Exception e) {
            log.warn("Could not contact: "+url, e);
            graphKeyValues = new ArrayList<>();
        }
        return graphKeyValues;
    }

    public GraphKeyValue getConsultantBudgetHoursByMonth(String useruuid, LocalDate month) {
        String url = apiGatewayUrl +"/cached/budgets/users/"+useruuid+"/datemonths/"+stringIt(month);
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody();
    }

    public List<BudgetDocument> getConsultantBudgetHoursByMonthDocuments(String useruuid, LocalDate month) {
        String url = apiGatewayUrl +"/cached/budgets/users/"+useruuid+"/datemonths/"+stringIt(month)+"/documents";
        ResponseEntity<BudgetDocument[]> result = systemRestService.secureCall(url, GET, BudgetDocument[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable("budgetdocuments")
    public List<BudgetDocument> getConsultantBudgetHoursByPeriodDocuments(LocalDate fromDate, LocalDate toDate) {
        String url = apiGatewayUrl +"/cached/budgets?fromdate="+stringIt(fromDate)+"&todate="+stringIt(toDate);
        ResponseEntity<BudgetDocument[]> result = systemRestService.secureCall(url, GET, BudgetDocument[].class);
        return Arrays.asList(result.getBody());
    }

    public GraphKeyValue getMonthBudget(LocalDate month) {
        String url = apiGatewayUrl +"/cached/budgets/datemonths/"+stringIt(month);
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody();
    }

    public List<BudgetNew> findByConsultantAndProject(String projectUuid, String contractConsultantUuid) {
        String url = apiGatewayUrl +"/cached/budgets/consultants/"+contractConsultantUuid+"?projectuuid="+projectUuid;
        List<BudgetNew> budgetNewList;
        try {
            ResponseEntity<BudgetNew[]> result = systemRestService.secureCall(url, GET, BudgetNew[].class);
            budgetNewList = Arrays.asList(result.getBody());
        } catch (Exception e) {
            log.warn("Could not budgets: "+url, e);
            budgetNewList = new ArrayList<>();
        }
        return budgetNewList;
    }

    public void save(BudgetNew budget) {
        String url = apiGatewayUrl +"/projects/"+budget.getProjectuuid()+"/budgets";
        systemRestService.secureCall(url, POST, Void.class, budget);
    }
}
