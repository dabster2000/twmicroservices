package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.network.dto.Project;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@FeignClient("client-service")
public interface ProjectClient {

    @RequestMapping(method = GET, value = "/projects")
    Resources<Resource<Project>> findAllProjects();

    @RequestMapping(method = GET, value = "/projects/search/findByClientuuidAndActiveTrue")
    Resources<Resource<Project>> findByClientuuidAndActiveTrue(@RequestParam("clientuuid") String clientuuid);

    @RequestMapping(method = GET, value = "/projects/search/findByClientdatauuid")
    Resources<Resource<Project>> findByClientdatauuid(@RequestParam("clientdatauuid") String clientdatauuid);

    @RequestMapping(method = PUT, value = "/projects/{uuid}")
    void save(@PathVariable("uuid") String uuid, @RequestBody Project project);


}
