package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.network.rest.ClientdataRestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientdataService implements InitializingBean {

    private static ClientdataService instance;

    private final ClientdataRestService clientdataRestService;

    @Autowired
    public ClientdataService(ClientdataRestService clientdataRestService) {
        this.clientdataRestService = clientdataRestService;
    }

    public Clientdata findOne(String uuid) {
        return clientdataRestService.findOne(uuid);
    }

    public List<Clientdata> findByClient(Client client) {
        return clientdataRestService.findByClient(client);
    }

    public void save(Clientdata clientdata) {
        clientdataRestService.save(clientdata);
    }

    public void delete(String uuid) {
        clientdataRestService.delete(uuid);
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static ClientdataService get() {
        return instance;
    }
}
