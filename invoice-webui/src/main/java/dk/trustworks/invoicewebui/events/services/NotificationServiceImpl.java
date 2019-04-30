package dk.trustworks.invoicewebui.events.services;

import dk.trustworks.invoicewebui.model.Work;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void workInitiateNotification(Work work)
            throws InterruptedException {
        System.out.println("Notification service started for "
                + "Notification ID: " + work);

        Thread.sleep(5000);
        System.out.println("Notification service ended for "
                + "Notification ID: " + work);
    }
}
