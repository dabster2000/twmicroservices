package dk.trustworks.network.clients;

import dk.trustworks.network.dto.Task;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("client-service")
public interface TaskClient {

    @RequestMapping(method = RequestMethod.GET, value = "/tasks/parents")
    Task findAllWithParents(@RequestParam("uuid") String uuid);

}
