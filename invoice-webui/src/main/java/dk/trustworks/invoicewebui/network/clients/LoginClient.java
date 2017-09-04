package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Service
public class LoginClient {

    protected static Logger logger = LoggerFactory.getLogger(LoginClient.class.getName());

    @Autowired
    private UserRepository userRepository;

    public User login(String username, String password) {
        logger.info(String.format("User.login(%s)", username));
        User user = userRepository.findByUsernameAndPassword(username, password);
        return user;
    }

}
