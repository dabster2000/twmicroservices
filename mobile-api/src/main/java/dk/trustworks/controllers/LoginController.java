package dk.trustworks.controllers;

import dk.trustworks.network.dto.User;
import dk.trustworks.network.clients.UserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hans on 29/06/2017.
 */

@RestController
@RequestMapping("/login")
public class LoginController {

    protected static Logger logger = LoggerFactory.getLogger(LoginController.class.getName());

    private final UserClient userClient;

    @Autowired
    public LoginController(UserClient userClient) {
        this.userClient = userClient;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Resource<User> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        logger.info(String.format("User.login(%s)", username));
        Resource<User> userResource = userClient.findByUsernameAndPassword(username, password);
        return userResource;
    }

}
