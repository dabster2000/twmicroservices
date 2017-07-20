package dk.trustworks;

import dk.trustworks.model.*;
import dk.trustworks.repositories.TaskworkerconstraintRepository;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

@Configuration
@ComponentScan("dk.trustworks")
public class ClientConfiguration extends RepositoryRestConfigurerAdapter {

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.exposeIdsFor(Task.class);
		config.exposeIdsFor(Project.class);
		config.exposeIdsFor(Client.class);
		config.exposeIdsFor(Taskworkerconstraint.class);
		config.exposeIdsFor(Budget.class);
		config.exposeIdsFor(Clientdata.class);
	}


	@Bean
	public AlwaysSampler defaultSampler() {
	  return new AlwaysSampler();
	}

}
