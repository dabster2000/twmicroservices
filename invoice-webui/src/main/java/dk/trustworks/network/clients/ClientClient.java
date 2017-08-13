package dk.trustworks.network.clients;

import dk.trustworks.network.dto.Client;
import dk.trustworks.network.dto.Invoice;
import dk.trustworks.network.dto.InvoiceStatus;
import dk.trustworks.network.dto.PdfContainer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@FeignClient("client-service")
public interface ClientClient {

    @RequestMapping(method = GET, value = "/clients")
    Resources<Resource<Client>> findAllClients();

}
