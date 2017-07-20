package dk.trustworks;

import feign.Logger;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("dk.trustworks")
public class WebApiConfiguration {


	@Bean
	public AlwaysSampler defaultSampler() {
	  return new AlwaysSampler();
	}

}
