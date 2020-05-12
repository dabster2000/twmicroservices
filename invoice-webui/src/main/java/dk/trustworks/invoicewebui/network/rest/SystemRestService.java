package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.dto.LoginToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Service
public class SystemRestService {

    @Value("#{environment.USERSERVICE_URL}")
    private String userServiceUrl;

    @Value("#{environment.USERSERVICE_USERNAME}")
    private String userserviceUsername;

    @Value("#{environment.USERSERVICE_PASSWORD}")
    private String userservicePassword;

    private final RestTemplate restTemplate;

    private LoginToken systemToken;

    @Autowired
    public SystemRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void construct() {
        systemToken = login(userserviceUsername, userservicePassword);
    }

    public LoginToken login(String username, String password) {
        String url = userServiceUrl+"/users/command/login?username="+username+"&password="+password;
        return restTemplate.getForObject(url, LoginToken.class);
    }

    public ResponseEntity secureCall(String url, HttpMethod method, Class c) {
        try {
            HttpEntity entity = new HttpEntity(createHeaders(getSystemToken().getToken()));
            return restTemplate.exchange(url, method, entity, c);
        } catch (RestClientException e) {
            systemToken = login(userserviceUsername, userservicePassword);
            HttpEntity entity = new HttpEntity(createHeaders(getSystemToken().getToken()));
            return restTemplate.exchange(url, method, entity, c);
        }
    }

    public ResponseEntity secureCall(String url, HttpMethod method, Class c, Object payload) {
        try {
            HttpEntity entity = new HttpEntity(payload, createHeaders(getSystemToken().getToken()));
            return restTemplate.exchange(url, method, entity, c);
        } catch (RestClientException e) {
            systemToken = login(userserviceUsername, userservicePassword);
            HttpEntity entity = new HttpEntity(payload, createHeaders(getSystemToken().getToken()));
            return restTemplate.exchange(url, method, entity, c);
        }
    }

    private HttpHeaders createHeaders(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.set( "Authorization", "Bearer " + token);
        return headers;
    }

    public LoginToken getSystemToken() {
        return systemToken;
    }
}
