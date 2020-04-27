package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.network.dto.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NewsRestService {

    @Value("#{environment.NEWSSERVICE_URL}")
    private String marginServiceUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public NewsRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public News[] getActiveNews() {
        String url = marginServiceUrl + "/news";
        News[] news = restTemplate.getForObject(url, News[].class);
        return news;
    }
}
