package dk.trustworks;

import dk.trustworks.model.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

/**
 * Created by hans on 08/07/2017.
 */
@Configuration
public class ExposeEntityIdRestConfiguration extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Budget.class);
        config.exposeIdsFor(Client.class);
        config.exposeIdsFor(Clientdata.class);
        config.exposeIdsFor(Project.class);
        config.exposeIdsFor(Task.class);
        config.exposeIdsFor(Taskworkerconstraint.class);
    }
}