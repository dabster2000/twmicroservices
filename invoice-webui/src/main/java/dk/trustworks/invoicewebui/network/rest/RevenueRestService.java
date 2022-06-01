package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.GraphKeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.GET;

@Service
public class RevenueRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public RevenueRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<GraphKeyValue> getRegisteredRevenueByPeriod(LocalDate fromdate, LocalDate todate) {
        String url = apiGatewayUrl + "/revenue/registered?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate);
        ResponseEntity<GraphKeyValue[]> result = systemRestService.secureCall(url, GET, GraphKeyValue[].class);
        return Arrays.asList(result.getBody());
    }

    public GraphKeyValue getRegisteredRevenueForSingleMonth(LocalDate month) {
        String url = apiGatewayUrl +"/revenue/registered/months/"+stringIt(month);
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody();
    }

    public GraphKeyValue getRegisteredHoursForSingleMonth(LocalDate month) {
        String url = apiGatewayUrl +"/revenue/registered/months/"+stringIt(month)+"/hours";
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody();
    }

    public List<GraphKeyValue> getSumOfRegisteredRevenueByClient() {
        String url = apiGatewayUrl + "/revenue/registered/clients";
        ResponseEntity<GraphKeyValue[]> result = systemRestService.secureCall(url, GET, GraphKeyValue[].class);
        return Arrays.asList(result.getBody());
    }

    public List<GraphKeyValue> getSumOfRegisteredRevenueByClientByFiscalYear(int fiscalYear) {
        String url = apiGatewayUrl + "/revenue/registered/clients/fiscalyear/"+fiscalYear;
        ResponseEntity<GraphKeyValue[]> result = systemRestService.secureCall(url, GET, GraphKeyValue[].class);
        return Arrays.asList(result.getBody());
    }

    public GraphKeyValue getRegisteredRevenueForSingleMonthAndSingleConsultant(String useruuid, LocalDate month) {
        String url = apiGatewayUrl + "/revenue/registered/consultants/"+useruuid+"/months/"+stringIt(month);
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody();
    }

    public HashMap<String, Double> getRegisteredRevenueByPeriodAndSingleConsultant(String useruuid, LocalDate periodFrom, LocalDate periodTo) {
        String url = apiGatewayUrl + "/revenue/registered/consultants/"+useruuid+"?periodFrom="+stringIt(periodFrom)+"&periodTo="+stringIt(periodTo);
        ResponseEntity<HashMap<String, Double>> result = systemRestService.secureCall(url, GET, HashMap.class);
        return result.getBody();
    }

    public List<GraphKeyValue> getRegisteredHoursPerConsultantForSingleMonth(LocalDate month) {
        String url = apiGatewayUrl + "/revenue/registered/consultants/hours?month="+stringIt(month);
        ResponseEntity<GraphKeyValue[]> result = systemRestService.secureCall(url, GET, GraphKeyValue[].class);
        return Arrays.asList(result.getBody());
    }

    public GraphKeyValue getRegisteredHoursForSingleMonthAndSingleConsultant(String useruuid, LocalDate month) {
        String url = apiGatewayUrl + "/revenue/registered/consultants/"+useruuid+"/hours?month="+stringIt(month);
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody();
    }

    public List<GraphKeyValue> getInvoicedOrRegisteredRevenueByPeriod(LocalDate fromdate, LocalDate todate) {
        String url = apiGatewayUrl + "/revenue/invoiced?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate);
        ResponseEntity<GraphKeyValue[]> result = systemRestService.secureCall(url, GET, GraphKeyValue[].class);
        return Arrays.asList(result.getBody());
    }

    public GraphKeyValue getInvoicedRevenueForSingleMonth(LocalDate month) {
        String url = apiGatewayUrl +"/revenue/invoiced/months/"+stringIt(month);
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody();
    }

    public List<GraphKeyValue> getProfitsByPeriod(LocalDate fromdate, LocalDate todate) {
        String url = apiGatewayUrl + "/revenue/profits?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate);
        ResponseEntity<GraphKeyValue[]> result = systemRestService.secureCall(url, GET, GraphKeyValue[].class);
        return Arrays.asList(result.getBody());
    }

    public List<GraphKeyValue> getRegisteredProfitsForSingleConsultant(String useruuid, LocalDate fromdate, LocalDate todate, int interval) {
        String url = apiGatewayUrl + "/revenue/profits/consultants/"+useruuid+"?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate)+"&interval="+interval;
        ResponseEntity<GraphKeyValue[]> result = systemRestService.secureCall(url, GET, GraphKeyValue[].class);
        return Arrays.asList(result.getBody());
    }

    public GraphKeyValue getTotalTeamProfits(int fiscalYear, List<String> teamuuids) {
        String url = apiGatewayUrl +"/revenue/profits/teams?fiscalyear="+fiscalYear+"&teamuuids="+String.join(",", teamuuids);
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody();
    }

    public GraphKeyValue getTotalTeamProfitsByPeriod(LocalDate fromdate, LocalDate todate, List<String> teamuuids) {
        String url = apiGatewayUrl +"/revenue/profits/teams?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate)+"&teamuuids="+String.join(",", teamuuids);
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody();
    }
}
