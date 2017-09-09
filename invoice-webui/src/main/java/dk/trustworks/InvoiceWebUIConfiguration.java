package dk.trustworks;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@ComponentScan("dk.trustworks.invoicewebui")
public class InvoiceWebUIConfiguration {



}
