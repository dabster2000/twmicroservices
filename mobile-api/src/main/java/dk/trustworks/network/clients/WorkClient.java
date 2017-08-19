package dk.trustworks.network.clients;

import dk.trustworks.network.dto.Work;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by hans on 29/06/2017.
 */
@FeignClient("time-service")
public interface WorkClient {

    @RequestMapping(method = RequestMethod.GET, value = "/work/search/findByYearAndMonthAndDayAndTaskUUIDAndUserUUID")
    Resources<Resource<Work>> findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(@RequestParam("year") int year,
                                                                             @RequestParam("month") int month,
                                                                             @RequestParam("day") int day,
                                                                             @RequestParam("useruuid") String useruuid,
                                                                             @RequestParam("taskuuid") String taskuuid);

    @RequestMapping(method = RequestMethod.GET, value = "/work/search/findByPeriodAndUserUUID")
    Resources<Resource<Work>> findByPeriodAndUserUUID(@RequestParam("fromdate") String fromdate,
                                                      @RequestParam("todate") String todate,
                                                      @RequestParam("useruuid") String useruuid);


    @RequestMapping(method = RequestMethod.POST, value = "/work")
    Resource<Work> saveWork(Work work);
}
