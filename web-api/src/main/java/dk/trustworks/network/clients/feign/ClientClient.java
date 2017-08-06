package dk.trustworks.network.clients.feign;

import dk.trustworks.network.dto.Client;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by hans on 11/07/2017.
 */

@FeignClient("client-service")
public interface ClientClient {
    @Cacheable("client")
    @RequestMapping(value = "/clients", method = GET)
    Resources<Resource<Client>> findAll();
}
