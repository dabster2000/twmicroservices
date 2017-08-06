package dk.trustworks.network.clients.feign;

import dk.trustworks.network.dto.Invoice;
import dk.trustworks.network.dto.Task;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by hans on 11/07/2017.
 */

@FeignClient("client-service")
public interface TaskClient {
    @Cacheable("task")
    @RequestMapping(value = "/tasks", method = GET)
    Resources<Resource<Task>> findAll();
}
