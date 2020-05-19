package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.network.dto.MarginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MarginRestService {

    @Value("#{environment.MARGINSERVICE_URL}")
    private String marginServiceUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public MarginRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public int calculateMargin(String useruuid, int rate) {
        String url = marginServiceUrl + "/margin/" + useruuid + "/" + rate;
        int margin = restTemplate.getForObject(url, MarginResult.class).getMargin();
        return margin;
    }
}
