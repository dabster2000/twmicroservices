package dk.trustworks.network.clients;

import dk.trustworks.network.dto.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by hans on 08/07/2017.
 */

@Component
public class TaskClient {

    @Autowired
    RestTemplate restTemplate;

    @Cacheable("task")
    public Resource<Task> findTaskByRestLink(String link) {
        return restTemplate.exchange(link, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<Task>>() {}).getBody();
    }

}
