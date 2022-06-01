package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.CKOExpense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Service
public class KnowledgeRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public KnowledgeRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<CKOExpense> findAll() {
        String url = apiGatewayUrl+"/knowledge/expenses";
        ResponseEntity<CKOExpense[]> result = systemRestService.secureCall(url, GET, CKOExpense[].class);
        return Arrays.asList(result.getBody());
    }

    public List<CKOExpense> findCKOExpenseByUseruuid(String useruuid) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/knowledge/expenses";
        ResponseEntity<CKOExpense[]> result = systemRestService.secureCall(url, GET, CKOExpense[].class);
        return Arrays.asList(result.getBody());
    }

    public List<CKOExpense> findByDescription(String description) {
        String url = apiGatewayUrl+"/knowledge/expenses/search/findByDescription?description="+ Base64.getEncoder().encodeToString(description.getBytes());
        ResponseEntity<CKOExpense[]> result = systemRestService.secureCall(url, GET, CKOExpense[].class);
        return Arrays.asList(result.getBody());
    }

    public void saveExpense(CKOExpense ckoExpense) {
        String url = apiGatewayUrl +"/knowledge/expenses";
        systemRestService.secureCall(url, POST, Void.class, ckoExpense);
    }

    public void updateExpense(CKOExpense ckoExpense) {
        String url = apiGatewayUrl +"/knowledge/expenses";
        systemRestService.secureCall(url, PUT, Void.class, ckoExpense);
    }

    public void deleteExpense(String uuid) {
        String url = apiGatewayUrl +"/knowledge/expenses/"+uuid;
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
