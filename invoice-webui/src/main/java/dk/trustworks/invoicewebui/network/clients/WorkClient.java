package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.network.dto.Work;
import feign.Body;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient("mobile-api")
public interface WorkClient {

    @RequestMapping(method = GET, value = "/work/search/findByPeriodAndUserUUID")
    Resources<Resource<Work>> findByPeriodAndUserUUID(@RequestParam("fromdate") String fromdate,
                                                      @RequestParam("todate") String todate,
                                                      @RequestParam("useruuid") String useruuid);

    @RequestMapping(method = POST, value = "/work")
    void save(@RequestBody Work work);
}
