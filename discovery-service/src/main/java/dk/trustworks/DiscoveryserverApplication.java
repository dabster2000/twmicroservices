package dk.trustworks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Created by hans on 25/06/2017.
 */
@EnableEurekaServer
@SpringBootApplication
public class DiscoveryserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryserverApplication.class, args);
    }

}
