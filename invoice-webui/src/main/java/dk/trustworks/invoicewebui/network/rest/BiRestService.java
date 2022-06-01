package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.dto.CompanyAggregateData;
import dk.trustworks.invoicewebui.model.dto.EmployeeAggregateData;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.GET;

@JBossLog
@Service
public class   BiRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public BiRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<CompanyAggregateData> getCompanyAggregateData(LocalDate fromdate, LocalDate todate) {
        String url = apiGatewayUrl +"/bi/company?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate);
        ResponseEntity<CompanyAggregateData[]> result = systemRestService.secureCall(url, GET, CompanyAggregateData[].class);
        return Arrays.asList(result.getBody());
    }

    public List<EmployeeAggregateData> getEmployeeAggregateData(LocalDate fromdate, LocalDate todate) {
        String url = apiGatewayUrl +"/bi/employees?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate);
        ResponseEntity<EmployeeAggregateData[]> result = systemRestService.secureCall(url, GET, EmployeeAggregateData[].class);
        return Arrays.asList(result.getBody());
    }

}
