package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.network.dto.Taskworkerconstraint;
import dk.trustworks.invoicewebui.network.dto.TaskworkerconstraintCreate;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient("client-service")
public interface TaskworkerconstraintClient {

    @RequestMapping(method = GET, value = "/taskworkerconstraints")
    Resources<Resource<Taskworkerconstraint>> findAllTaskworkerconstraints();

    @RequestMapping(method = PATCH, value = "/taskworkerconstraints/{uuid}")
    void save(@PathVariable("uuid") String uuid, @RequestBody Taskworkerconstraint taskworkerconstraint);

    @RequestMapping(method = POST, value = "/taskworkerconstraints")
    void create(@RequestBody TaskworkerconstraintCreate taskworkerconstraint);
}
