package dk.trustworks;

import dk.trustworks.model.Invoice;
import dk.trustworks.model.InvoiceItem;
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
        config.exposeIdsFor(Invoice.class);
        config.exposeIdsFor(InvoiceItem.class);
    }
}