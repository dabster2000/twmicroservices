package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.BudgetNew;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.dto.BudgetDocument;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.GET;

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

    public List<GraphKeyValue> getBudgetsPerMonth(LocalDate fromdate, LocalDate todate) {
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

    public GraphKeyValue getMonthBudget(LocalDate month) {
        String url = apiGatewayUrl +"/cached/budgets/datemonths/"+stringIt(month);
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody();
    }

    public BudgetNew findByMonthAndYearAndContractConsultantAndProjectuuid(int monthValue, int year, String contractconsultantuuid, String projectuuid) {
        return null;
    }
}
