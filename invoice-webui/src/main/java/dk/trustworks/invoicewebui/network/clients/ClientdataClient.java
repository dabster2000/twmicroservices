package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.network.dto.Clientdata;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@FeignClient("client-service")
public interface ClientdataClient {

    @RequestMapping(method = POST, value = "/clients/{clientuuid}/clientdata/save")
    void create(@PathVariable("clientuuid") String clientuuid, @RequestBody Clientdata clientdata);

    //@RequestMapping(method = POST, value = "//clientdata/save")
    //void create(@RequestBody Clientdata clientdata);

    @RequestMapping(method = PUT, value = "/clientdata/{uuid}")
    void update(@PathVariable("uuid") String uuid, @RequestBody Clientdata clientdata);

    @RequestMapping(method = DELETE, value = "/clientdata/{uuid}")
    void delete(@PathVariable("uuid") String uuid);

}
