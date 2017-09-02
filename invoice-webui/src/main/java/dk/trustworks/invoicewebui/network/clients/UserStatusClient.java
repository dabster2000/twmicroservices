package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.network.dto.UserStatus;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient("user-service")
public interface UserStatusClient {

    @RequestMapping(method = GET, value = "/statuses/search/findAllActive")
    Resources<Resource<UserStatus>> findAllActive();

    @RequestMapping(method = GET, value = "/statuses/search/findAllActiveByDate")
    Resources<Resource<UserStatus>> findAllActiveByDate(@RequestParam("actualdate") String actualdate);

}
