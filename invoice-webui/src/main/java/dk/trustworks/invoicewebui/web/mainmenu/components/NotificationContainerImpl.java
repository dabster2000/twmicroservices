package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.Notification;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.NotificationRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.time.components.MonthReportDesign;
import dk.trustworks.invoicewebui.web.time.model.UserHourItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class NotificationContainerImpl extends NotificationContainerDesign {

    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationContainerImpl() {

    }

    @Transactional
    public NotificationContainerImpl init() {
        loadNotifications();
        return this;
    }

    @Transactional
    public void loadNotifications() {
        System.out.println("NotificationContainerImpl.loadNotifications");
        this.removeAllComponents();
        UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
        if(userSession==null) return;
        List<Notification> notifications = notificationRepository.findByReceiverAndAndExpirationdateAfter(userSession.getUser(), new Date());
        System.out.println("notifications.size() = " + notifications.size());
        for (Notification notification : notifications) {
            System.out.println("notification = " + notification);
            addComponent(new NotificationImpl(notification));
        }

    }
}
