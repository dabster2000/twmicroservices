package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Achievement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Service
public class AchievementRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public AchievementRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Achievement> findAchievementsByUseruuid(String useruuid) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/achievements";
        ResponseEntity<Achievement[]> result = systemRestService.secureCall(url, GET, Achievement[].class);
        return Arrays.asList(result.getBody());
    }
}
