package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.network.dto.Client;
import dk.trustworks.invoicewebui.network.dto.Logo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@FeignClient("client-service")
public interface ClientClient {

    @RequestMapping(method = GET, value = "/clients")
    Resources<Resource<Client>> findAllClients();

    @RequestMapping(method = PATCH, value = "/clients/{uuid}")
    void save(@PathVariable("uuid") String uuid, @RequestBody Client client);

    @RequestMapping(method = POST, value = "/clients")
    void create(@RequestBody Client client);
}
