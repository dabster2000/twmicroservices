package dk.trustworks.invoicewebui.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Viewport;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.security.Authorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Created by hans on 02/07/2017.
 */
@SpringUI
@SpringViewDisplay
@Viewport("width=device-width,initial-scale=1.0,user-scalable=no")
@Theme("invoice")
public class VaadinUI extends UI implements Broadcaster.BroadcastListener, ViewDisplay {

    protected final static Logger logger = LoggerFactory.getLogger(VaadinUI.class.getName());

    //DefaultNotificationHolder notifications = new DefaultNotificationHolder();
    //DefaultBadgeHolder badge = new DefaultBadgeHolder();

    private Panel springViewDisplay;

    @Autowired
    private Navigator navigator;

    @Autowired
    private Authorizer authorizer;

    //@Autowired
    //private BirthdayEventView birthdayEventView;

    @Override
    protected void init(VaadinRequest request) {
        //notifications.setNotificationClickedListener(newStatus -> Notification.show(newStatus.getTitle()));

        final VerticalLayout root;
        root = new VerticalLayout();
        root.setSpacing(false);
        root.setMargin(false);
        root.setSizeFull();
        setContent(root);

        //oot.addComponent(fancyNotifications);

        springViewDisplay = new Panel();
        springViewDisplay.setSizeFull();
        root.addComponent(springViewDisplay);
        root.setExpandRatio(springViewDisplay, 1.0f);
        Broadcaster.register(this);

        final String hostname = Page.getCurrent().getLocation().getHost();
        if(hostname.equals("event.trustworks.dk")) UI.getCurrent().getNavigator().navigateTo("birthdayevent");

        navigator.addViewChangeListener((ViewChangeListener) event -> {
            if(!authorizer.hasAccess(event.getNewView())) {
                if(hostname.equals("event.trustworks.dk")) UI.getCurrent().getNavigator().navigateTo("birthdayevent");
                event.getNavigator().navigateTo("login");
            }
            return authorizer.hasAccess(event.getNewView());
        });


        UI.getCurrent().setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                String errorUuid = UUID.randomUUID().toString();

                logger.error(errorUuid, event.getThrowable());

                Notification notification = new Notification("The click failed: ",
                        errorUuid,
                        Notification.Type.ASSISTIVE_NOTIFICATION);
                notification.setStyleName("failure");
                notification.show(Page.getCurrent());
            }
        });
    }

    @Override
    public void receiveBroadcast(String message) {
        logger.info("MainWindowImpl.receiveBroadcast");
        logger.info("message = " + message);
/*
        if(message.equals("notification")) {
            UserSession userSession = this.getSession().getAttribute(UserSession.class);
            if(userSession!=null) {
                for (Notification notification : notificationRepository.findByUseruuidAndAndExpirationdateAfter(userSession.getUser(), new Date())) {
                    //System.out.println("notification = " + notification);

                    //getCurrent().access(() ->fancyNotifications.showNotification(null, notification.getTitel(), notification.getContent(), MaterialIcons.DATE_RANGE));
                }
            }
            //topMenu.reload();
        }
*/
        //projectList.reloadData();
        //int numberOfDrafts = mainWindow.invoiceClient.findByStatus(DRAFT).getContent().size();
        //System.out.println("numberOfDrafts = " + numberOfDrafts);
        if(message.equals("invoice")) {
            getUI().access(() -> {
                //draftList.receiveBroadcast(message);
            });
        }

    }

    @Override
    public void showView(View view) {
        /*
        if((VaadinSession.getCurrent().getAttribute(UserSession.class)) == null && !view.getViewComponent().getId().equals("login")) {
            UI.getCurrent().getNavigator().navigateTo("login");
            return;
        }
        */
        if(!authorizer.hasAccess(view)) this.getNavigator().navigateTo("login");
/*
        String hostname = Page.getCurrent().getLocation().getHost();
        System.out.println("view.getViewComponent() = " + view.getViewComponent());
        System.out.println("view.getViewComponent().getId() = " + view.getViewComponent().getId());
        if(hostname.equals("localhost") && (view.getViewComponent().getId()==null || !view.getViewComponent().getId().equals("birthdayevent"))) {
            UI.getCurrent().getNavigator().navigateTo("birthdayevent");
            return;
        }
*/

        springViewDisplay.setContent((Component) view);
    }


}