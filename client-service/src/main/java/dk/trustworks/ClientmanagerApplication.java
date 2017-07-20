package dk.trustworks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.hypermedia.DiscoveredResource;
import org.springframework.cloud.client.hypermedia.DynamicServiceInstanceProvider;
import org.springframework.context.annotation.Bean;

/**
 * Created by hans on 25/06/2017.
 */

@EnableCircuitBreaker
@SpringBootApplication
@EnableDiscoveryClient
public class ClientmanagerApplication {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean(name="userResource")
    public DiscoveredResource usersByUUIDResource() {
        return new DiscoveredResource(new DynamicServiceInstanceProvider(discoveryClient, "user-service")
                , traverson -> traverson.follow("users", "search", "findByUuid"));
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientmanagerApplication.class, args);
    }
}
