package dk.trustworks.invoicewebui.network.queue;

import dk.trustworks.invoicewebui.web.Broadcaster;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

/**
 * Created by hans on 16/07/2017.
 */

@Component
public class TopicReceiver {
    @CacheEvict(cacheNames={"invoices","projectsummaries"}, allEntries=true)
    public void receiveMessage(String message) {
        System.out.println("TopicReceiver.receiveMessage");
        System.out.println("message = [" + message + "]");
        Broadcaster.broadcast(message);
    }
}
