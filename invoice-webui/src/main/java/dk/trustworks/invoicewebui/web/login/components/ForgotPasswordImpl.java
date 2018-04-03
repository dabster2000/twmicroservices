package dk.trustworks.invoicewebui.web.login.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.services.EmailSender;
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

    private static Map<String, User> uuidMap = new HashMap();

    @Autowired
    public ForgotPasswordImpl(UserRepository userRepository, EmailSender emailSender) {
        System.out.println("ForgotPasswordImpl.ForgotPasswordImpl");

        getImgTop().setSource(new ThemeResource("images/password-card.jpg"));
        getBtnLogin().addClickListener(clickEvent -> {
            User user = userRepository.findByUsername(getTxtUsername().getValue());
            String uuid = UUID.randomUUID().toString();
            uuidMap.put(uuid, user);
            emailSender.sendResetPassword(user, uuid);
            Notification.show("Password reset",
                    "Please check your email for further instructions.",
                    Notification.Type.ASSISTIVE_NOTIFICATION);
            getUI().getNavigator().navigateTo("login");
        });
        getBtnLogin().setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }

    public User getResetUser(String uuid) {
        if(!uuidMap.containsKey(uuid)) return null;
        return uuidMap.remove(uuid);
    }
}
