package dk.trustworks;

import com.vaadin.spring.annotation.EnableVaadin;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import reactor.Environment;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executor;

/**
 * Created by hans on 02/07/2017.
 */

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "dk.trustworks"})
@EnableAsync
@EnableCaching
@EnableVaadin
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class InvoiceWebUIApplication {

    @Autowired
    UserService userService;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
        Locale.setDefault(new Locale("da", "DK"));
        SpringApplication.run(InvoiceWebUIApplication.class);
    }

    @PostConstruct
    private void init() {
    }

    @Bean
    Environment env() {
        return Environment.initializeIfEmpty().assignErrorJournal();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("AsyncMethod-");
        executor.initialize();
        return executor;
    }
}

