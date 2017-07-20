package dk.trustworks.network;

import dk.trustworks.network.dto.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("user-service")
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, value = "/users/{useruuid}")
    User getUser(@PathVariable("useruuid") String useruuid);

}
