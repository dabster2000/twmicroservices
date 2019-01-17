package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.LogType;
import dk.trustworks.invoicewebui.services.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginClient {

    protected static Logger logger = LoggerFactory.getLogger(LoginClient.class.getName());

    @Autowired
    private UserService userService;

    public User login(String username, String password) {
        logger.info(String.format("User.login(%s) attempt ", username));
        User user = userService.findByUsername(username);
        if(user.getPassword().trim().equals("")) {
            logger.info("Failed login attempt (no password) by "+user.getUsername());
            return null;
        }
        if (BCrypt.checkpw(password, user.getPassword())) {
            logger.info("Login by " + user.getUsername(), LogType.LOGIN.name(), user.getUuid());
            return user;
        }  else {
            logger.info("Failed login attempt (wrong password) by " + user.getUsername());
            return null;
        }
    }

}
