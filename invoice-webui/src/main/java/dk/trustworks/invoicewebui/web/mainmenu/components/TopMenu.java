package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.Notification;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.NotificationRepository;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.security.Authorizer;
import dk.trustworks.invoicewebui.web.Broadcaster;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.fancylayouts.FancyNotifications;
import org.vaadin.alump.fancylayouts.FancyNotifications.NotificationsListener;
import org.vaadin.alump.fancylayouts.FancyTransition;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.util.Date;

import static com.jarektoro.responsivelayout.ResponsiveLayout.DisplaySize.SM;
import static com.jarektoro.responsivelayout.ResponsiveLayout.DisplaySize.XS;

/**
 * Created by hans on 28/08/2017.
 */
@SpringComponent
@SpringUI
public class TopMenu extends CssLayout implements Broadcaster.BroadcastListener {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private Authorizer authorizer;

    private FancyNotifications notifications;

    private UserSession userSession;

    @Autowired
    private LeftMenu leftMenu;

    private TopMenuUserDesign topMenuUserDesign;

    @PostConstruct
    public void init() {
        if((userSession = VaadinSession.getCurrent().getAttribute(UserSession.class)) == null) UI.getCurrent().getNavigator().navigateTo("login");;
        User user = userSession.getUser();

        Broadcaster.register(this);
        setStyleName("v-component-group material");
        setWidth("100%");
        setHeight("75px");

        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        this.addComponent(responsiveLayout);

        ResponsiveRow row = responsiveLayout.addRow();
        row.setHorizontalSpacing(false);
        row.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        Image logo = new Image();
        logo.setSource(new ThemeResource("images/logo.png"));
        logo.setSizeFull();
        row.addColumn()
                .withDisplayRules(6, 6, 3, 3)
                .withComponent(logo);

        Button appsButton = new Button(MaterialIcons.APPS);
        appsButton.setStyleName("borderless icon-only h4");
        appsButton.addClickListener(event -> {
            for (MenuItemContainer menuItem : leftMenu.getMenuItems().values()) {
                menuItem.getMenuItemColumn().setVisibility(XS, !menuItem.getMenuItemColumn().isVisibleForDisplaySize(XS));
                menuItem.getMenuItemColumn().setVisibility(SM, !menuItem.getMenuItemColumn().isVisibleForDisplaySize(SM));
            }
        });

        Button searchButton = new Button(MaterialIcons.SEARCH);
        searchButton.setStyleName("borderless icon-only h4");

        HorizontalLayout horizontalLayout = new HorizontalLayout(appsButton, searchButton);
        row.addColumn()
                .withDisplayRules(6, 6, 3, 3)
                .withVisibilityRules(true, true, true, true)
                .withComponent(horizontalLayout);

        row.addColumn()
                .withDisplayRules(0, 0, 3, 3)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new Label());

        topMenuUserDesign = new TopMenuUserDesign();
        Photo photo;
        Image image;
        if((photo = photoRepository.findByRelateduuid(user.getUuid())) != null) {
            image = new Image(null,
                    new StreamResource((StreamResource.StreamSource) () ->
                            new ByteArrayInputStream(photo.getPhoto()),
                            user.getUsername()+System.currentTimeMillis()+".jpg"));
            image.setStyleName("img-circle");
            image.setWidth(75, Unit.PIXELS);
            image.setHeight(75, Unit.PIXELS);
        } else {
            image = new Image(null, MaterialIcons.ACCOUNT_CIRCLE);
        }
        topMenuUserDesign.addComponent(image);
        topMenuUserDesign.getBtnNotification().setIcon(MaterialIcons.SMS_FAILED);

        createNotifications();


        topMenuUserDesign.getBtnNotification().addClickListener(event -> {
            if(!notifications.isAttached()) {
                ((ComponentContainer)this.getParent()).removeComponent(notifications);
                createNotifications();
                ((ComponentContainer)this.getParent()).addComponent(notifications);
            }

            userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
            for (Notification notification : notificationRepository.findByReceiverAndAndExpirationdateAfter(userSession.getUser(), new Date())) {
                System.out.println("notification = " + notification);

                notifications.showNotification(notification, notification.getTitel(), notification.getContent());
            }
        });

        ResponsiveColumn column = row.addColumn();
        column.setAlignment(ResponsiveColumn.ColumnComponentAlignment.RIGHT);
        column.withDisplayRules(0, 0, 3, 3)
                .withVisibilityRules(false, false, true, true)
                .withComponent(topMenuUserDesign);
    }

    private void createNotifications() {
        notifications = new FancyNotifications();
        notifications.setCloseTimeout(10000);
        notifications.setClickClose(true);
        notifications.setTransitionEnabled(FancyTransition.SLIDE, true);
        notifications.addListener((NotificationsListener) id -> UI.getCurrent().getNavigator().navigateTo(((Notification)id).getLink()));
    }

    @Override
    public void receiveBroadcast(String message) {
        /*
        System.out.println("TopMenu.receiveBroadcast");
        System.out.println("message = " + message);

        if(message.equals("notification")) {
            //UserSession userSession = VaadinSession.getCurrent().getAttribute(UserSession.class);
            if(userSession!=null) {
                if(!notifications.isAttached()) ((ComponentContainer)this.getParent()).addComponent(notifications);
                for (Notification notification : notificationRepository.findByReceiverAndAndExpirationdateAfter(userSession.getUser(), new Date())) {
                    System.out.println("GET THEM notification = " + notification);
                    UI.getCurrent().access(() -> notifications.showNotification(null, notification.getTitel(), notification.getContent(), MaterialIcons.DATE_RANGE));
                }
            }
        }
*/
    }

}
