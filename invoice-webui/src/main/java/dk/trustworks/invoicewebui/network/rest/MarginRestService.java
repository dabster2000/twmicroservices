package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.network.dto.ClientMarginResult;
import dk.trustworks.invoicewebui.network.dto.MarginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.GET;

@Service
public class MarginRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    @Autowired
    public MarginRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    private final SystemRestService systemRestService;

    public int calculateMargin(String useruuid, int rate) {
        String url = apiGatewayUrl + "/margin/" + useruuid + "/" + rate;
        ResponseEntity<MarginResult> result = systemRestService.secureCall(url, GET, MarginResult.class);
        return result.getBody().getMargin();
    }

    public List<ClientMarginResult> calculateMarginPerClient(int fiscalYear) {
        String url = apiGatewayUrl + "/margin/clients?fiscalyear=" + fiscalYear;
        ResponseEntity<ClientMarginResult[]> result = systemRestService.secureCall(url, GET, ClientMarginResult[].class);
        return Arrays.asList(result.getBody());
    }
}
