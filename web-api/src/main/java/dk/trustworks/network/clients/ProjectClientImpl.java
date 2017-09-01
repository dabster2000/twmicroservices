package dk.trustworks.network.clients;

import dk.trustworks.network.dto.Project;
import dk.trustworks.network.dto.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by hans on 08/07/2017.
 */

@Component
public class ProjectClientImpl {

    @Autowired
    RestTemplate restTemplate;

    @Cacheable("project")
    public Resource<Project> findProjectByRestLink(String link) {
        System.out.println("ProjectClientImpl.findProjectByRestLink");
        System.out.println("link = [" + link + "]");
        return restTemplate.exchange(link, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<Project>>() {}).getBody();
    }

}
