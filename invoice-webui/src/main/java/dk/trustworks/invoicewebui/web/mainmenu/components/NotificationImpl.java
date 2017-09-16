package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.vaadin.ui.UI;
import dk.trustworks.invoicewebui.model.Notification;

/**
 * Created by hans on 30/08/2017.
 */
public class NotificationImpl extends NotificationDesign {


    public NotificationImpl() {
    }

    public NotificationImpl(Notification notification) {
        getTxtEvent().setCaption(notification.getTitel());
        getTxtEvent().setValue(notification.getContent());
        if(!notification.getLink().equals("")) addLayoutClickListener(event -> UI.getCurrent().getNavigator().navigateTo(notification.getLink()));
    }
}
