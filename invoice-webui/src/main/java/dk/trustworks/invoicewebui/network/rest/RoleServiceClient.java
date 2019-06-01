package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Role;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "role-service", path = "roles", fallback = HystrixClientFallback.class)
public interface RoleServiceClient {

    @RequestMapping(method = RequestMethod.GET)
    List<Role> findRolesByUseruuid(@RequestParam("useruuid") String useruuid);

}

