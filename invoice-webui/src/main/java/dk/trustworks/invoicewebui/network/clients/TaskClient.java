package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.network.dto.Task;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient("client-service")
public interface TaskClient {

    @RequestMapping(method = GET, value = "/tasks")
    Resources<Resource<Task>> findAllTasks();

    @RequestMapping(method = GET, value = "/tasks/search/findByProjectuuid")
    Resources<Resource<Task>> findByProjectuuid(@RequestParam("projectuuid") String projectuuid);

}
