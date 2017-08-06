package dk.trustworks;

import dk.trustworks.eventhandlers.WorkEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.hypermedia.DiscoveredResource;
import org.springframework.cloud.client.hypermedia.DynamicServiceInstanceProvider;
import org.springframework.cloud.client.hypermedia.ServiceInstanceProvider;
import org.springframework.cloud.client.hypermedia.StaticServiceInstanceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

/**
 * Created by hans on 25/06/2017.
 */

@EnableCircuitBreaker
@SpringBootApplication
@EnableDiscoveryClient
public class TimemanagerApplication extends RepositoryRestMvcConfiguration {
    public static void main(String[] args) {
        SpringApplication.run(TimemanagerApplication.class, args);
    }

    @Autowired
    private DiscoveryClient discoveryClient;
/*
    @Bean(name="userResource")
    public DiscoveredResource usersByUUIDResource() {
        return new DiscoveredResource(new DynamicServiceInstanceProvider(discoveryClient, "user-service")
                , traverson -> traverson.follow("users", "search", "findByUuid"));
    }
*/
    @Bean(name="taskResource")
    public DiscoveredResource taskByUUIDResource() {
        return new DiscoveredResource(new DynamicServiceInstanceProvider(discoveryClient, "client-service")
                , traverson -> {
            System.out.println("TimemanagerApplication.taskByUUIDResource");
            return traverson.follow("tasks", "search", "findByUuid");
        });
    }

    @Bean(name="userResource")
    public DiscoveredResource usersByUUIDResource(ServiceInstanceProvider provider) {
        System.out.println("TimemanagerApplication.usersByUUIDResource");
        return new DiscoveredResource(provider, traverson -> traverson.follow("users", "search", "findByUuid"));
    }

    @EnableDiscoveryClient
    static class CloudConfiguration {
        @Bean
        public DynamicServiceInstanceProvider dynamicServiceProvider(DiscoveryClient client) {
            System.out.println("CloudConfiguration.dynamicServiceProvider");
            return new DynamicServiceInstanceProvider(client, "user-service");
        }
    }

/*
    @Bean
    @Profile("default")
    public StaticServiceInstanceProvider staticServiceInstanceProvider() {
        return new StaticServiceInstanceProvider(new DefaultServiceInstance("user-service", "localhost", 2222, false));
    }

    @Profile("cloud")
    @EnableDiscoveryClient
    static class CloudConfiguration {
        @Bean(name="user-service")
        public DynamicServiceInstanceProvider dynamicServiceProvider(DiscoveryClient client) {
            return new DynamicServiceInstanceProvider(client, "user-service");
        }
    }
    */
}
