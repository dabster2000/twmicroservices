package dk.trustworks.invoicewebui.web.login.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.security.PasswordConstraintValidator;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hans on 12/08/2017.
 */
@SpringComponent
@SpringUI
public class ResetPasswordImpl extends ResetPasswordDesign {

    private String uuid;
    private User user;
    private ForgotPasswordImpl forgotPassword;

    @Autowired
    public ResetPasswordImpl(UserRepository userRepository, ForgotPasswordImpl forgotPassword) {
        this.forgotPassword = forgotPassword;
        System.out.println("LoginImpl.LoginImpl");
        getImgTop().setSource(new ThemeResource("images/password-card.jpg"));
        getBtnSave().addClickListener(clickEvent -> {
            if (getTxtPassword().getValue().equals(getTxtVerifyPassword().getValue())) {
                User userDb = userRepository.findOne(user.getUuid());
                userDb.setPassword(BCrypt.hashpw(getTxtPassword().getValue(), BCrypt.gensalt()));
                userRepository.save(userDb);
                getUI().getNavigator().navigateTo("login");
                Notification.show("Succes",
                        "Your may now use your new password.",
                        Notification.Type.ASSISTIVE_NOTIFICATION);
            } else if (new PasswordConstraintValidator().isValid(getTxtPassword().getValue())) {
                Notification.show("Failed",
                        "Password not strong enough!",
                        Notification.Type.WARNING_MESSAGE);
                getTxtPassword().setValue("");
                getTxtPassword().setWidth(100, Unit.PERCENTAGE);
                getTxtVerifyPassword().setValue("");
                getTxtVerifyPassword().setWidth(100, Unit.PERCENTAGE);
            } else {
                Notification.show("Failed",
                        "The passwords do not match, please try again.",
                        Notification.Type.WARNING_MESSAGE);
                getTxtPassword().setValue("");
                getTxtPassword().setWidth(100, Unit.PERCENTAGE);
                getTxtVerifyPassword().setValue("");
                getTxtVerifyPassword().setWidth(100, Unit.PERCENTAGE);
            }
        });
        getBtnSave().setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }

    public void setUser(String uuid) {
        this.uuid = uuid;
        user = forgotPassword.getResetUser(uuid);
        getTxtUsername().setValue(user.getUsername());
    }
}
