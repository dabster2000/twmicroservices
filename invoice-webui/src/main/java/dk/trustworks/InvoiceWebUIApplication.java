package dk.trustworks;

import com.vaadin.spring.annotation.EnableVaadin;
import dk.trustworks.invoicewebui.events.WorkNotificationConsumer;
import dk.trustworks.invoicewebui.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import reactor.bus.EventBus;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executor;

import static reactor.bus.selector.Selectors.$;

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

    private static final Logger log = LoggerFactory.getLogger(InvoiceWebUIApplication.class);

    @Autowired
    private EventBus eventBus;

    @Autowired
    private WorkNotificationConsumer workNotificationConsumer;

    @Autowired
    private StatisticsService statisticsService;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
        Locale.setDefault(new Locale("da", "DK"));

        /*
        System.out.println("1 = " + 1);
        Trello trelloApi = new TrelloImpl("667f15f00708bc0486898e6b6c48e528", "96f9f88e22ba9d229e2699877496006c1a203c880823cbd21d10453b70d87622", new ApacheHttpClient());
        System.out.println("2 = " + 2);
        Board board = trelloApi.getBoard("5c49bf6b621b8f73d27169bd");
        System.out.println("3 = " + 3);
        List<TList> lists = board.fetchLists();
        System.out.println("4 = " + 4);
        for (TList list : lists) {
            System.out.println("5 = " + 5);
            for (Card card : list.getCards()) {

                System.out.println("card = " + card.getName());
                System.out.println("card = " + card.getDesc());
                System.out.println();
            }
        }
        */
        SpringApplication.run(InvoiceWebUIApplication.class);
    }

    @PostConstruct
    private void init() {
        //statisticsService.run();
        //System.exit(0);

        log.info("InitDemoApplication initialization logic ...");
        eventBus.on($("notificationConsumer"), workNotificationConsumer);
    }

    @Bean
    Environment env() {
        return Environment.initializeIfEmpty().assignErrorJournal();
    }

    @Bean
    EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
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
