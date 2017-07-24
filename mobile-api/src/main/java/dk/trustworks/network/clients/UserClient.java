package dk.trustworks.network.clients;

import dk.trustworks.network.dto.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, value = "/users/search/findByUsernameAndPassword")
    Resource<User> findByUsernameAndPassword(@RequestParam("username") String username, @RequestParam("password") String password);

}
