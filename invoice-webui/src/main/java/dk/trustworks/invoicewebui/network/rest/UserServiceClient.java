package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-service", path = "users", fallback = HystrixClientFallback.class)
public interface UserServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/search/findByUsername")
    User findByUsername(@RequestParam("username") String username);

    @RequestMapping(method = RequestMethod.GET, value = "/{uuid}")
    User findOne(@PathVariable("uuid") String uuid);

    @RequestMapping(method = RequestMethod.GET, value = "/search/findBySlackusername")
    User findBySlackusername(@RequestParam("slackusername") String slackusername);

    @RequestMapping(method = RequestMethod.GET)
    List<User> findByOrderByUsername();
}

