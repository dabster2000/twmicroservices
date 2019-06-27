package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    public User findByUsername(String username) {
        return restTemplate.getForObject("http://localhost:5460/users/search/findByUsername?username="+username, User.class);
    }

    public User findOne(String uuid) {
        return restTemplate.getForObject("http://localhost:5460/users/"+uuid, User.class);
    }

    public List<User> findByOrderByUsername() {
        return Arrays.asList(restTemplate.getForObject("http://localhost:5460/users", User[].class));
    }

    public User[] findBySlackusername(String userId) {
        return restTemplate.getForObject("http://localhost:5460/users/search/findBySlackusername?username="+userId, User[].class);
    }
}

