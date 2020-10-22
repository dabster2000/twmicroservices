package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Week;
import dk.trustworks.invoicewebui.model.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Service
public class WeekRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public WeekRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Week> findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(int weeknumber, int year, String useruuid) {
        String url = apiGatewayUrl + "/users/"+useruuid+"/weeks/"+year+"/"+weeknumber;
        System.out.println("url = " + url);
        ResponseEntity<Week[]> result = systemRestService.secureCall(url, GET, Week[].class);
        return new ArrayList<>(Arrays.asList(result.getBody()));
    }

    public void save(Week week) {
        String url = apiGatewayUrl + "/weeks";
        systemRestService.secureCall(url, POST, Void.class, week);
    }

    public void delete(String weekuuid) {
        String url = apiGatewayUrl + "/weeks/"+weekuuid;
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
