package dk.trustworks;

import dk.trustworks.eventhandlers.WorkEventHandler;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

@Configuration
@ComponentScan("dk.trustworks")
public class TimeConfiguration {


    @Bean
    public AlwaysSampler defaultSampler() {
        return new AlwaysSampler();
    }

	/*
    @Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.exposeIdsFor(MyEntity.class);
	}
	*/


}
