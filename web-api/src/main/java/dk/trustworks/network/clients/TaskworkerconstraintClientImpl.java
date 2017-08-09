package dk.trustworks.network.clients;

import dk.trustworks.network.dto.Task;
import dk.trustworks.network.dto.Taskworkerconstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by hans on 08/07/2017.
 */

@Component
public class TaskworkerconstraintClientImpl {

    @Autowired
    RestTemplate restTemplate;

    @Cacheable("taskworkerconstraints")
    public Resources<Resource<Taskworkerconstraint>> findTaskworkerconstraintsByRestLink(String link) {
        return restTemplate.exchange(link, HttpMethod.GET, null, new ParameterizedTypeReference<Resources<Resource<Taskworkerconstraint>>>() {}).getBody();
    }

}
