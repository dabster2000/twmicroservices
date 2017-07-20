package dk.trustworks.network;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * Created by hans on 08/07/2017.
 */
public class FeignConfiguration {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
