package dk.trustworks.controllers;

import dk.trustworks.network.dto.Work;
import dk.trustworks.network.clients.WorkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

/**
 * Created by hans on 29/06/2017.
 */

@RestController
@RequestMapping("/work")
public class WorkController {


    private final WorkClient workClient;

    @Autowired
    public WorkController(WorkClient workClient) {
        this.workClient = workClient;
    }

    @RequestMapping(value = "/search/findByYearAndMonthAndDayAndTaskUUIDAndUserUUID", method = RequestMethod.GET)
    public Resources<Resource<Work>> findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(@RequestParam("year") int year,
                                           @RequestParam("month") int month,
                                           @RequestParam("day") int day,
                                           @RequestParam("useruuid") String useruuid,
                                           @RequestParam("taskuuid") String taskuuid) {
        Resources<Resource<Work>> workResource = workClient.findByYearAndMonthAndDayAndTaskUUIDAndUserUUID(year, month, day, useruuid, taskuuid);
        return workResource;
    }

    @RequestMapping(value = "/search/findByPeriodAndUserUUID", method = RequestMethod.GET)
    public Resources<Resource<Work>> findByPeriodAndUserUUID(@RequestParam("fromdate") String fromdate,
                                                             @RequestParam("todate") String todate,
                                                             @RequestParam("useruuid") String useruuid) {
        Resources<Resource<Work>> workResource = workClient.findByPeriodAndUserUUID(fromdate, todate, useruuid);
        return workResource;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void saveWork(@RequestBody Work work) {
        workClient.saveWork(work);
    }
}
