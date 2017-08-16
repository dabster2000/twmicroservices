package dk.trustworks.invoicewebui.network.clients;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import dk.trustworks.invoicewebui.network.dto.Client;
import dk.trustworks.invoicewebui.network.dto.Logo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 16/08/2017.
 */
@Service
public class ClientClientImpl {

    @Autowired
    ClientClient clientClient;

    @Autowired
    LogoClient logoClient;

    public Resources<Resource<Client>> findAllClientsAndLogo() {
        Resources<Resource<Client>> clientResources = clientClient.findAllClients();
        List<String> clientuuids = new ArrayList<>();
        for (Resource<Client> clientResource : clientResources) {
            System.out.println("clientResource.getContent().getUuid() = " + clientResource.getContent().getUuid());
            clientuuids.add(clientResource.getContent().getUuid());
        }
        final Resources<Resource<Logo>> logoResources = logoClient.findByClientuuidIn(clientuuids);
        System.out.println("logoResources.getContent().size() = " + logoResources.getContent().size());
        for (Resource<Client> clientResource : clientResources) {
            for (Resource<Logo> logoResource : logoResources) {
                if(clientResource.getContent().getUuid().equals(logoResource.getContent().getClientuuid())) clientResource.getContent().setLogo(logoResource.getContent().getLogo());
            }
        }
        return clientResources;
    }

    public void create(Client client) {
        clientClient.create(client);
        saveLogo(client.getUuid(), client.getLogo());
    }

    public void save(String uuid, Client client) {
        clientClient.save(uuid, client);
        saveLogo(uuid, client.getLogo());
    }

    private void saveLogo(String clientuuid, byte[] logo) {
        Resource<Logo> existingLogo = logoClient.findByClientuuid(clientuuid);
        if(existingLogo!=null) {
            existingLogo.getContent().setLogo(logo);
            logoClient.save(existingLogo.getContent().getUuid(), existingLogo.getContent());
        } else {
            logoClient.create(new Logo(UUID.randomUUID().toString(), clientuuid, logo));
        }
    }
}
