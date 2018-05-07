package dk.trustworks;

import com.vaadin.spring.annotation.EnableVaadin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.Locale;
import java.util.concurrent.Executor;

/**
 * Created by hans on 02/07/2017.
 */

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "dk.trustworks"})
@EnableAsync
@EnableCaching
@EnableVaadin
@EnableAspectJAutoProxy
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class InvoiceWebUIApplication {

    private static final Logger log = LoggerFactory.getLogger(InvoiceWebUIApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(InvoiceWebUIApplication.class);
        Locale.setDefault(new Locale("da", "DK"));
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
