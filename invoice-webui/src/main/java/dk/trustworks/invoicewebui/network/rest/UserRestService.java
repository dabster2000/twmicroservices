package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UserRestService {

    private final RestTemplate restTemplate;

    @Autowired
    public UserRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public User findOne(String uuid) {
        return null;
    }

    public List<User> findByOrderByUsername() {
        return null;
    }
    public User findByUsername(String username) {
        return null;
    }

    public User findBySlackusername(String slackusername) {
        return null;
    }

    public List<User> findUsersByDateAndStatusListAndTypes(String date, String[] consultantStatusList, String... consultantTypes) {
        return null;
    }

    public int calculateCapacityByMonthByUser(String useruuid, String statusdate) {
        return 0;
    }

    public User save(User user) {
        return null;
    }
}
