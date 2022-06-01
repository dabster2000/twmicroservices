package dk.trustworks.invoicewebui.network.rest;

import com.vaadin.ui.UI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.LoginToken;
import dk.trustworks.invoicewebui.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    protected static Logger logger = LoggerFactory.getLogger(SystemRestService.class.getName());

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final RestTemplate restTemplate;

    private LoginToken systemToken;

    @Autowired
    public SystemRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void construct() {
        //systemToken = login(userserviceUsername, userservicePassword);
    }

    public LoginToken login(String username, String password) {
        LoginToken loginToken;
        try {
            String url = apiGatewayUrl + "/login?username=" + username + "&password=" + password;
            loginToken = restTemplate.getForObject(url, LoginToken.class);
            logger.info("loginToken: "+loginToken);
        } catch (RestClientException e) {
            loginToken = new LoginToken();
        }
        return loginToken;
    }

    public LoginToken relogin() {
        if(!UserService.get().getLoggedInUser().isPresent()) return new LoginToken();
        User user = UserService.get().getLoggedInUser().get();
        LoginToken loginToken;
        try {
            String url = apiGatewayUrl + "/login?username=" + user.getUsername() + "&password=" + user.getPassword();
            loginToken = restTemplate.getForObject(url, LoginToken.class);
            logger.info("relogin loginToken: "+loginToken);
            systemToken = loginToken;
        } catch (RestClientException e) {
            loginToken = new LoginToken();
            UI.getCurrent().getNavigator().navigateTo("login");
        }
        return loginToken;
    }

    public void unsafePutCall(String url) {
        restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(new HttpHeaders()), Void.class);
    }

    public void unsafePostCall(String url) {
        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(new HttpHeaders()), Void.class);
    }

    public ResponseEntity secureCall(String url, HttpMethod method, Class c) {
        try {
            HttpEntity entity = new HttpEntity(createHeaders(UserService.get().getLoggedInUserToken().get().getToken()));
            return restTemplate.exchange(url, method, entity, c);
        } catch (RestClientException e) {
            systemToken = relogin();
            HttpEntity entity = new HttpEntity(createHeaders(UserService.get().getLoggedInUserToken().get().getToken()));
            return restTemplate.exchange(url, method, entity, c);
        }
    }

    public ResponseEntity secureCall(String url, HttpMethod method, Class c, Object payload) {
        try {
            HttpEntity entity = new HttpEntity(payload, createHeaders(UserService.get().getLoggedInUserToken().get().getToken()));
            return restTemplate.exchange(url, method, entity, c);
        } catch (RestClientException e) {
            systemToken = relogin();
            HttpEntity entity = new HttpEntity(payload, createHeaders(UserService.get().getLoggedInUserToken().get().getToken()));
            return restTemplate.exchange(url, method, entity, c);
        }
    }

    public ResponseEntity secureCallGZip(String url, HttpMethod method, Class c, Object payload) {
        try {
            HttpEntity entity = new HttpEntity(payload, createGZipHeaders(UserService.get().getLoggedInUserToken().get().getToken()));
            return restTemplate.exchange(url, method, entity, c);
        } catch (RestClientException e) {
            systemToken = relogin();
            HttpEntity entity = new HttpEntity(payload, createHeaders(UserService.get().getLoggedInUserToken().get().getToken()));
            return restTemplate.exchange(url, method, entity, c);
        }
    }

    private HttpHeaders createHeaders(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.set( "Authorization", "Bearer " + token);
        return headers;
    }

    private HttpHeaders createGZipHeaders(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.set( "Authorization", "Bearer " + token);
        headers.set("Content-Encoding", "gzip");
        return headers;
    }
}
