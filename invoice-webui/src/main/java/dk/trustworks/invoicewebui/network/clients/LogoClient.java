package dk.trustworks.invoicewebui.network.clients;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import dk.trustworks.invoicewebui.network.dto.Client;
import dk.trustworks.invoicewebui.network.dto.Logo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@FeignClient(value = "client-service", fallback = LogoClient.LogoClientFallback.class)
public interface LogoClient {

    @HystrixCommand(fallbackMethod = "reliable")
    @RequestMapping(method = GET, value = "/logos/search/findByClientuuid")
    Resource<Logo> findByClientuuid(@RequestParam("clientuuid") String clientuuid);

    @RequestMapping(method = GET, value = "/logos/search/findByClientuuidIn")
    Resources<Resource<Logo>> findByClientuuidIn(@RequestParam("clientuuid") List<String> clientuuids);

    @RequestMapping(method = PATCH, value = "/logos/{uuid}")
    void save(@PathVariable("uuid") String uuid, @RequestBody Logo logo);

    @RequestMapping(method = POST, value = "/logos")
    void create(@RequestBody Logo logo);

    @Component
    class LogoClientFallback implements LogoClient {

        @Override
        public Resource<Logo> findByClientuuid(String clientuuid) {
            return null;
        }

        @Override
        public Resources<Resource<Logo>> findByClientuuidIn(List<String> clientuuids) {
            return null;
        }

        @Override
        public void save(String uuid, Logo logo) {
            System.out.println("LogoClientFallback.save");
            System.out.println("uuid = [" + uuid + "], logo = [" + logo + "]");
        }

        @Override
        public void create(Logo logo) {
            System.out.println("LogoClientFallback.create");
            System.out.println("logo = [" + logo + "]");
        }
    }
}
