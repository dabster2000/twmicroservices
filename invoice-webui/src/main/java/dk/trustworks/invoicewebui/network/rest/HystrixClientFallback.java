package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HystrixClientFallback  implements UserServiceClient {
    @Override
    public User findByUsername(String username) {
        return new User();
    }

    @Override
    public User findOne(String uuid) {
        return new User();
    }

    @Override
    public User findBySlackusername(String slackusername) {
        return new User();
    }

    @Override
    public List<User> findByOrderByUsername() {
        return new ArrayList<>();
    }
}
