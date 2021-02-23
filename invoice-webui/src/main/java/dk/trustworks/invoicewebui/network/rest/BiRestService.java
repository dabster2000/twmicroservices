package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.dto.MonthRevenueData;
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
public class BiRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public BiRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<MonthRevenueData> getMonthRevenueData(LocalDate fromdate, LocalDate todate) {
        String url = apiGatewayUrl +"/bi/company?fromdate="+stringIt(fromdate)+"&todate="+stringIt(todate);
        System.out.println("url = " + url);
        ResponseEntity<MonthRevenueData[]> result = systemRestService.secureCall(url, GET, MonthRevenueData[].class);
        return Arrays.asList(result.getBody());
    }

}
