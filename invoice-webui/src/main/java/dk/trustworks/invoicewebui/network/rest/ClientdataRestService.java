package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Clientdata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Service
public class ClientdataRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public ClientdataRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Clientdata> findAll() {
        String url = apiGatewayUrl +"/clientdata";
        ResponseEntity<Clientdata[]> result = systemRestService.secureCall(url, GET, Clientdata[].class);
        return Arrays.asList(result.getBody());
    }

    public Clientdata findOne(String uuid) {
        String url = apiGatewayUrl +"/clientdata/"+uuid;
        ResponseEntity<Clientdata> result = systemRestService.secureCall(url, GET, Clientdata.class);
        return result.getBody();
    }

    public List<Clientdata> findByClient(Client client) {
        String url = apiGatewayUrl +"/clients/"+client.getUuid()+"/clientdata";
        ResponseEntity<Clientdata[]> result = systemRestService.secureCall(url, GET, Clientdata[].class);
        return Arrays.asList(result.getBody());
    }

    public Clientdata save(Clientdata clientdata) {
        String url = apiGatewayUrl +"/clientdata";
        ResponseEntity<Clientdata> result = systemRestService.secureCall(url, POST, Clientdata.class, clientdata);
        return result.getBody();
    }

    public void update(Clientdata clientdata) {
        String url = apiGatewayUrl +"/clientdata";
        systemRestService.secureCall(url, PUT, Void.class, clientdata);
    }

    public void delete(Clientdata clientdata) {
        delete(clientdata.getUuid());
    }

    public void delete(String uuid) {
        String url = apiGatewayUrl +"/clientdata/"+uuid;
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
