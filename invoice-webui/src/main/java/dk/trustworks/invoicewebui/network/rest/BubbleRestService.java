package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Bubble;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@JBossLog
@Service
public class BubbleRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public BubbleRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Bubble> findBubblesByActiveTrueOrderByCreatedDesc() {
        String url = apiGatewayUrl +"/bubbles/active";
        List<Bubble> bubbles;
        try {
            ResponseEntity<Bubble[]> result = systemRestService.secureCall(url, GET, Bubble[].class);
            bubbles = Arrays.asList(result.getBody());
        } catch (Exception e) {
            log.warn("Could not load bubbles: "+url, e);
            bubbles = new ArrayList<>();
        }
        return bubbles;
    }

    public List<Bubble> findByUseruuid(String useruuid) {
        String url = apiGatewayUrl +"/users/"+useruuid+"/bubbles/active";
        List<Bubble> bubbles;
        try {
            ResponseEntity<Bubble[]> result = systemRestService.secureCall(url, GET, Bubble[].class);
            bubbles = Arrays.asList(result.getBody());
        } catch (Exception e) {
            log.warn("Could not load bubbles: "+url, e);
            bubbles = new ArrayList<>();
        }
        return bubbles;
    }

    public void saveBubble(Bubble bubble) {
        String url = apiGatewayUrl +"/bubbles";
        systemRestService.secureCall(url, POST, Void.class, bubble);
    }

    public void updateBubble(Bubble bubble) {
        String url = apiGatewayUrl +"/bubbles";
        systemRestService.secureCall(url, PUT, Void.class, bubble);
    }

    public void addBubbleMember(String bubbleuuid, String useruuid) {
        String url = apiGatewayUrl +"/bubbles/"+bubbleuuid+"/users/"+useruuid;
        systemRestService.secureCall(url, POST, Void.class);
    }

    public void removeBubbleMember(String bubbleuuid, String useruuid) {
        String url = apiGatewayUrl +"/bubbles/"+bubbleuuid+"/users/"+useruuid;
        systemRestService.secureCall(url, DELETE, Void.class);
    }

    public void removeAllMembers(String bubbleuuid) {
        String url = apiGatewayUrl +"/bubbles/"+bubbleuuid+"/users";
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
