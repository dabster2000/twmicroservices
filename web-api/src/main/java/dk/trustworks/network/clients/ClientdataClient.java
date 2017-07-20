package dk.trustworks.network.clients;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import dk.trustworks.network.dto.Clientdata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ClientdataClient {

    protected static Logger logger = LoggerFactory.getLogger(ClientdataClient.class.getName());

    @Autowired
    RestTemplate restTemplate;

    @Cacheable("clientdata")
    @HystrixCommand(fallbackMethod = "defaultStores")
    public Resource<Clientdata> findClientdataByRestLink(String link) {
        return restTemplate.exchange(link, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<Clientdata>>() {}).getBody();
    }

    public Resource<Clientdata> defaultStores(String link) {
        logger.error("ClientdataClient.defaultStores");
        logger.error("link = [" + link + "]");
        logger.error("Clientdata not found");
        return null;
    }

}
