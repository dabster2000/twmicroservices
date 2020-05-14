package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

@Service
public class ClientRestService {

    @Value("#{environment.CRMSERVICE_URL}")
    private String crmServiceUrl;

    private final SystemRestService systemRestService;

    private final RestTemplate restTemplate;

    @Autowired
    public ClientRestService(SystemRestService systemRestService, RestTemplate restTemplate) {
        this.systemRestService = systemRestService;
        this.restTemplate = restTemplate;
    }

    public Client[] findAll() {
        String url = crmServiceUrl +"/clients";
        ResponseEntity<Client[]> result = systemRestService.secureCall(url, GET, Client[].class);
        return result.getBody();
    }

    public Client[] findByActiveTrue() {
        String url = crmServiceUrl +"/clients/search/findByActiveTrue";
        ResponseEntity<Client[]> result = systemRestService.secureCall(url, GET, Client[].class);
        return result.getBody();
    }

    /*
    String url = userServiceUrl+"/users/search/findUsersByDateAndStatusListAndTypes?date="+date+"&consultantStatusList="+String.join(",",consultantStatusList)+"&consultantTypes="+String.join(",", consultantTypes);
        ResponseEntity<User[]> result = systemRestService.secureCall(url, GET, User[].class);
        return Arrays.asList(result.getBody());
     */
/*
    List<Client> findByActiveTrue();
    List<Client> findByActiveTrueOrderByName();
    List<Client> findByOrderByName();
    List<Client> findAllByOrderByActiveDescNameAsc();

    @Override @RestResource(exported = false) void delete(String id);
    @Override @RestResource(exported = false) void delete(Client entity);
 */

}
