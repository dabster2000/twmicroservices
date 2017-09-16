package dk.trustworks.invoicewebui.web;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Notification;
import dk.trustworks.invoicewebui.repositories.NotificationRepository;
import dk.trustworks.invoicewebui.security.Authorizer;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.invoice.components.DraftListImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.fancylayouts.FancyNotifications;
import org.vaadin.alump.materialicons.MaterialIcons;

import java.util.Date;

/**
 * Created by hans on 02/07/2017.
 */
@Push
@SpringUI
@SpringViewDisplay
@Theme("invoice")
public class VaadinUI extends UI implements Broadcaster.BroadcastListener, ViewDisplay {

    private Panel springViewDisplay;

    @Autowired
    private DraftListImpl draftList;

    @Autowired
    private Navigator navigator;

    @Autowired
    private Authorizer authorizer;

    private boolean clickNotifications = false;

    private FancyNotifications fancyNotifications;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout root;
        root = new VerticalLayout();
        root.setSpacing(false);
        root.setMargin(false);
        root.setSizeFull();
        setContent(root);

        fancyNotifications = new FancyNotifications();
        root.addComponent(fancyNotifications);

        springViewDisplay = new Panel();
        springViewDisplay.setSizeFull();
        root.addComponent(springViewDisplay);
        root.setExpandRatio(springViewDisplay, 1.0f);
        Broadcaster.register(this);

        navigator.addViewChangeListener((ViewChangeListener) event -> {
            if(!authorizer.hasAccess(event.getNewView())) event.getNavigator().navigateTo("login");
            return authorizer.hasAccess(event.getNewView());
        });

        UI.getCurrent().setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                // Find the final cause
                String cause = "<b>The click failed because:</b><br/>";
                for (Throwable t = event.getThrowable(); t != null;
                     t = t.getCause())
                    if (t.getCause() == null) // We're at final cause
                        cause += t.getClass().getName() + "<br/>";

                // Do the default error handling (optional)
                doDefault(event);
            }
        });
    }

    @Override
    public void receiveBroadcast(String message) {
        System.out.println("MainWindowImpl.receiveBroadcast");
        System.out.println("message = " + message);

        if(message.equals("notification")) {
            UserSession userSession = this.getSession().getAttribute(UserSession.class);
            if(userSession!=null) {
                for (Notification notification : notificationRepository.findByReceiverAndAndExpirationdateAfter(userSession.getUser(), new Date())) {
                    System.out.println("notification = " + notification);

                    getCurrent().access(() ->fancyNotifications.showNotification(null, notification.getTitel(), notification.getContent(), MaterialIcons.DATE_RANGE));
                }
            }
            //topMenu.reload();
        }

        //projectList.reloadData();
        //int numberOfDrafts = mainWindow.invoiceClient.findByStatus(DRAFT).getContent().size();
        //System.out.println("numberOfDrafts = " + numberOfDrafts);
        if(message.equals("invoice")) {
            getUI().access(() -> {
                draftList.receiveBroadcast(message);
            });
        }

    }

    @Override
    public void showView(View view) {
        springViewDisplay.setContent((Component) view);
    }


}