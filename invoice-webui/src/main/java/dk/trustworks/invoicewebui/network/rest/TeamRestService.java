package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Team;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.GET;

@JBossLog
@Service
public class TeamRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public TeamRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Team> getAllTeams() {
        String url = apiGatewayUrl +"/teams";
        ResponseEntity<Team[]> result = systemRestService.secureCall(url, GET, Team[].class);
        return Arrays.asList(result.getBody());
    }

}
