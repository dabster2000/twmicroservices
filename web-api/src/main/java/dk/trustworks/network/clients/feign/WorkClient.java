package dk.trustworks.network.clients.feign;

import dk.trustworks.network.FeignConfiguration;
import dk.trustworks.network.dto.Work;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by hans on 29/06/2017.
 */
@FeignClient(value = "time-service", configuration = FeignConfiguration.class)
public interface WorkClient {

    @Cacheable(value = "work", sync = true)
    @RequestMapping(method = RequestMethod.GET, value = "/work/search/findByYearAndMonth", consumes = "application/hal+json")
    Resources<Resource<Work>> findByYearAndMonth(@RequestParam("year") int year, @RequestParam("month") int month);

}



