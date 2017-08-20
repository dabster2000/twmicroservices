package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.network.dto.Week;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient("mobile-api")
public interface MobileApiClient {

    @RequestMapping(method = GET, value = "/weeks")
    Resources<Resource<Week>> findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(@RequestParam("weeknumber") int weeknumber, @RequestParam("year") int year, @RequestParam("useruuid") String useruuid);

}
