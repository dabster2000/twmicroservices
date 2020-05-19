package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.User;
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

    @Value("#{environment.CRMSERVICE_URL}")
    private String crmServiceUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public ClientRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Client> findAll() {
        String url = crmServiceUrl +"/clients";
        ResponseEntity<Client[]> result = systemRestService.secureCall(url, GET, Client[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable("clients")
    public Client findOne(String uuid) {
        String url = crmServiceUrl +"/clients/"+uuid;
        ResponseEntity<Client> result = systemRestService.secureCall(url, GET, Client.class);
        return result.getBody();
    }

    @Cacheable("clients")
    public List<Client> findByActiveTrue() {
        String url = crmServiceUrl +"/clients/search/findByActiveTrue";
        ResponseEntity<Client[]> result = systemRestService.secureCall(url, GET, Client[].class);
        return Arrays.asList(result.getBody());
    }

    @CacheEvict(value = "clients", allEntries = true)
    public Client save(Client client) {
        String url = crmServiceUrl+"/clients";
        return (Client) systemRestService.secureCall(url, POST, Client.class, client).getBody();//restTemplate.postForObject(url, user, User.class);
    }

    @CacheEvict(value = "clients", allEntries = true)
    public void update(Client client) {
        String url = crmServiceUrl+"/clients";
        systemRestService.secureCall(url, PUT, Client.class, client).getBody(); //restTemplate.put(url, user);
    }

    @CacheEvict(value = "clients", allEntries = true)
    public void delete(Client client) {
        delete(client.getUuid());
    }

    @CacheEvict(value = "clients", allEntries = true)
    public void delete(String uuid) {
        String url = crmServiceUrl+"/clients/"+uuid;
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
