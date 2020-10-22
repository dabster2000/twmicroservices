package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Service
public class ClientRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public ClientRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Client> findAll() {
        String url = apiGatewayUrl +"/clients";
        ResponseEntity<Client[]> result = systemRestService.secureCall(url, GET, Client[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable(cacheNames = "client")
    public Client findOne(String uuid) {
        String url = apiGatewayUrl +"/clients/"+uuid;
        ResponseEntity<Client> result = systemRestService.secureCall(url, GET, Client.class);
        return result.getBody();
    }

    @Cacheable(cacheNames = "client")
    public List<Client> findByActiveTrue() {
        String url = apiGatewayUrl +"/clients/active";
        ResponseEntity<Client[]> result = systemRestService.secureCall(url, GET, Client[].class);
        return Arrays.asList(result.getBody());
    }

    public Client save(Client client) {
        String url = apiGatewayUrl +"/clients";
        return (Client) systemRestService.secureCall(url, POST, Client.class, client).getBody();//restTemplate.postForObject(url, user, User.class);
    }

    public void update(Client client) {
        String url = apiGatewayUrl +"/clients";
        systemRestService.secureCall(url, PUT, Client.class, client).getBody(); //restTemplate.put(url, user);
    }

    public void delete(Client client) {
        delete(client.getUuid());
    }

    public void delete(String uuid) {
        String url = apiGatewayUrl +"/clients/"+uuid;
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
