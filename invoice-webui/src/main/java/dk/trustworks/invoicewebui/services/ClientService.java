package dk.trustworks.invoicewebui.services;


import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.network.rest.ClientRestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Service
public class ClientService implements InitializingBean {

    private static ClientService instance;

    private final ClientRestService clientRestService;

    @Autowired
    public ClientService(ClientRestService clientRestService) {
        this.clientRestService = clientRestService;
    }

    public Client findOne(String uuid) {
        return clientRestService.findOne(uuid);
    }

    public List<Client> findAll() {
        return clientRestService.findAll();
    }

    public List<Client> findByActiveTrue() {
        return clientRestService.findByActiveTrue();
    }

    public List<GraphKeyValue> findClientFiscalBudgetSums(int fiscalYear) {
        return clientRestService.findClientFiscalBudgetSums(fiscalYear);
    }

    public Client save(Client client) {
        return clientRestService.save(client);
    }

    public void update(Client client) {
        clientRestService.update(client);
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static ClientService get() {
        return instance;
    }
}
