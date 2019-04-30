package dk.trustworks.invoicewebui.events.services;

import dk.trustworks.invoicewebui.model.Work;

public interface NotificationService {
    void workInitiateNotification(Work work) throws InterruptedException;
}
