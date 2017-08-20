package dk.trustworks.controllers;

import dk.trustworks.MobileApiApplication;
import dk.trustworks.network.dto.*;
import dk.trustworks.network.clients.WeekClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.hypermedia.DynamicServiceInstanceProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hans on 29/06/2017.
 */

@RestController
@RequestMapping("/weeks")
@CacheConfig(cacheNames = {"weeks"})
public class WeekController {

    private static final Logger LOG = Logger.getLogger(MobileApiApplication.class.getName());

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeekClient weekClient;

    public WeekController() {
    }

    @RequestMapping(value = "/clone", method = RequestMethod.GET)
    public void cloneWeek(@RequestParam("from_week") int fromWeek, @RequestParam("to_week") int toWeek, @RequestParam("from_year") int fromYear, @RequestParam("to_year") int toYear, @RequestParam("useruuid") String useruuid) {
        Resources<Resource<Week>> weekResources = weekClient.findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(fromWeek, toYear, useruuid);
        for (Resource<Week> weekResource : weekResources.getContent()) {
            weekResource.getContent().setWeeknumber(toWeek);
            weekResource.getContent().setYear(toYear);

        }

    }

    @RequestMapping(method = RequestMethod.GET)
    public Resources<Resource<Week>> findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(@RequestParam("weeknumber") int weeknumber, @RequestParam("year") int year, @RequestParam("useruuid") String useruuid) throws URISyntaxException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("weeknumber", weeknumber);
        parameters.put("year", year);
        parameters.put("useruuid", useruuid);

        ParameterizedTypeReference<Resources<Resource<Week>>> resourceParameterizedTypeReference = new ParameterizedTypeReference<Resources<Resource<Week>>>() {};
        //.withTemplateParameters(parameters).toObject(resourceParameterizedTypeReference

        Traverson traverson = new Traverson(new DynamicServiceInstanceProvider(discoveryClient, "time-service").getServiceInstance().getUri(), MediaTypes.HAL_JSON);

        Resources<Resource<Week>> weekResources = traverson.follow("weeks", "search", "findByWeeknumberAndYearAndUseruuidOrderBySortingAsc")
                .withTemplateParameters(parameters).toObject(resourceParameterizedTypeReference);

        //Resources<Resource<Week>> weekResources = weekClient.findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(weeknumber, year, useruuid);


        for (Resource<Week> weekResource : weekResources.getContent()) {
            Link userLink = weekResource.getLink("user");
            Link taskLink = weekResource.getLink("task");
            LOG.log(Level.INFO, "getting user entity");
            ResponseEntity<Resource<User>> userResponseEntity =
                    restTemplate.exchange(userLink.getHref(),
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<Resource<User>>() {},
                            Collections.emptyMap());
            if (userResponseEntity.getStatusCode() == HttpStatus.OK) {
                Resource<User> userResource = userResponseEntity.getBody();
                weekResource.getContent().setUser(userResource);
            }

            LOG.log(Level.INFO, "getting task entity");
            ResponseEntity<Resource<Task>> taskResponseEntity =
                    restTemplate.exchange(taskLink.getHref(),
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<Resource<Task>>() {},
                            Collections.emptyMap());
            Resource<Task> taskResource = null;
            if (taskResponseEntity.getStatusCode() == HttpStatus.OK) {
                taskResource = taskResponseEntity.getBody();
                weekResource.getContent().setTask(taskResource);
            }

            LOG.log(Level.INFO, "getting project entity");
            ResponseEntity<Resource<Project>> projectResponseEntity =
                    restTemplate.exchange(taskResource.getLink("project").getHref(),
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<Resource<Project>>() {},
                            Collections.emptyMap());
            Resource<Project> projectResource = null;
            if (projectResponseEntity.getStatusCode() == HttpStatus.OK) {
                projectResource = projectResponseEntity.getBody();
                taskResource.getContent().setProject(projectResource);
            }

            LOG.log(Level.INFO, "getting client entity");
            ResponseEntity<Resource<Client>> clientResponseEntity =
                    restTemplate.exchange(projectResource.getLink("client").getHref(),
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<Resource<Client>>() {},
                            Collections.emptyMap());
            Resource<Client> clientResource = null;
            if (clientResponseEntity.getStatusCode() == HttpStatus.OK) {
                clientResource = clientResponseEntity.getBody();
                projectResource.getContent().setClient(clientResource);
            }
        }

        return weekResources;
    }
}
