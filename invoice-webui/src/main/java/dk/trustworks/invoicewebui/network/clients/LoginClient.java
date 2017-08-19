package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.network.dto.Client;
import dk.trustworks.invoicewebui.network.dto.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@FeignClient("mobile-api")
public interface LoginClient {

    @RequestMapping(method = GET, value = "/login")
    Resource<User> login(@RequestParam("username") String username, @RequestParam("password") String password);

}
