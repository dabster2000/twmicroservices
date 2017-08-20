package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.network.dto.CreatedWeek;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient("time-service")
public interface WeekClient {

    @RequestMapping(method = POST, value = "/weeks")
    void save(@RequestBody CreatedWeek week);

}
