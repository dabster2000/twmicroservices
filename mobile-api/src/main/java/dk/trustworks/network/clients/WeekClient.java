package dk.trustworks.network.clients;

import dk.trustworks.network.dto.Week;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("time-service")
public interface WeekClient {

    @RequestMapping(method = RequestMethod.GET, value = "/weeks/search/findByWeeknumberAndYearAndUseruuidOrderBySortingAsc")
    Resources<Resource<Week>> findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(@RequestParam("weeknumber") int weeknumber, @RequestParam("year") int year, @RequestParam("useruuid") String useruuid);

    @RequestMapping(method = RequestMethod.POST, value = "/weeks")
    void postWeek(Week week);

}
