package dk.trustworks.invoicewebui.events;

import dk.trustworks.invoicewebui.events.services.NotificationService;
import dk.trustworks.invoicewebui.model.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class WorkNotificationConsumer implements Consumer<Event<Work>> {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void accept(Event<Work> notificationDataEvent) {
        Work work = notificationDataEvent.getData();

        try {
            notificationService.workInitiateNotification(work);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
