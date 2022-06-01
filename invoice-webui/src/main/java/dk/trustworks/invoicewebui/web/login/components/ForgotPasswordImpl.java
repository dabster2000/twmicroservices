package dk.trustworks.invoicewebui.web.login.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.EmailSender;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hans on 12/08/2017.
 */
@SpringComponent
@SpringUI
public class ForgotPasswordImpl extends ForgotPasswordDesign {

    @Autowired
    public ForgotPasswordImpl(UserService userService) {
        System.out.println("ForgotPasswordImpl.ForgotPasswordImpl");

        getImgTop().setSource(new ThemeResource("images/password-card.jpg"));
        getBtnLogin().addClickListener(clickEvent -> {
            if(!getTxtPassword().getValue().equals(getTxtConfirmPassword().getValue())) {
                Notification.show("Password error",
                        "Passwords does not match",
                        Notification.Type.ERROR_MESSAGE);
                getTxtPassword().clear();
                getTxtConfirmPassword().clear();
                return;
            }
            //User user = userService.findByUsername(getTxtUsername().getValue());
            //String uuid = UUID.randomUUID().toString();
            //uuidMap.put(uuid, user);
            //System.out.println("uuid = " + uuid);
            userService.updateUserPassword(getTxtUsername().getValue().toLowerCase(), getTxtPassword().getValue());
            getVlReset().setVisible(false);
            getVlConfirmation().setVisible(true);
            //userService.updateUserPassword(user.getUuid(), getTxtPassword().getValue());
            //emailSender.sendResetPassword(user, uuid);
            //getUI().getNavigator().navigateTo("login");
        });
        getBtnLogin().setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }
/*
    public User getResetUser(String uuid) {
        if(!uuidMap.containsKey(uuid)) return null;
        return uuidMap.remove(uuid);
    }

 */
}
