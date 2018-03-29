package dk.trustworks.invoicewebui.web.login.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.UserRepository;
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
            if(getTxtPassword().getValue().equals(getTxtVerifyPassword().getValue())) {
                User userDb = userRepository.findOne(user.getUuid());
                userDb.setPassword(BCrypt.hashpw(getTxtPassword().getValue(), BCrypt.gensalt()));
                userRepository.save(userDb);
                getUI().getNavigator().navigateTo("login");
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
