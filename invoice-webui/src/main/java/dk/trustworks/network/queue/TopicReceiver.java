package dk.trustworks.network.queue;

import dk.trustworks.web.Broadcaster;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

/**
 * Created by hans on 16/07/2017.
 */

@Component
public class TopicReceiver {

    @CacheEvict(cacheNames={"invoices","projectsummaries"}, allEntries=true)
    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
        Broadcaster.broadcast(message);
    }

}
