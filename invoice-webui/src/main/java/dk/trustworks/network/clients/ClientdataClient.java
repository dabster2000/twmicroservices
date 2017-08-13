package dk.trustworks.network.clients;

import dk.trustworks.network.dto.Clientdata;
import feign.Body;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@FeignClient("client-service")
public interface ClientdataClient {
    @RequestMapping(method = PUT, value = "/clientdata/{uuid}")
    void save(@PathVariable("uuid") String uuid, @RequestBody Clientdata clientdata);
}
