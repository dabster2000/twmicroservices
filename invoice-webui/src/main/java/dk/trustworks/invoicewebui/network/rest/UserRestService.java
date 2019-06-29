package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.network.dto.IntegerJsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class UserRestService {

    private final RestTemplate restTemplate;

    @Autowired
    public UserRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public User findByUsername(String username) {
        return restTemplate.getForObject("http://localhost:5460/users/search/findByUsername?username="+username, User.class);
    }

    @Cacheable("users")
    public User findOne(String uuid) {
        return restTemplate.getForObject("http://localhost:5460/users/"+uuid, User.class);
    }

    @Cacheable("users")
    public List<User> findByOrderByUsername() {
        return Arrays.asList(restTemplate.getForObject("http://localhost:5460/users", User[].class));
    }

    public User[] findBySlackusername(String userId) {
        return restTemplate.getForObject("http://localhost:5460/users/search/findBySlackusername?username="+userId, User[].class);
    }

    @Cacheable("users")
    public List<User> findUsersByDateAndStatusListAndTypes(String date, String[] consultantStatusList, String... consultantTypes) {
        String url = "http://localhost:5460/users/search/findUsersByDateAndStatusListAndTypes?date="+date+"&consultantStatusList="+String.join(",",consultantStatusList)+"&consultantTypes="+String.join(",", consultantTypes);
        return Arrays.asList(restTemplate.getForObject(url, User[].class));
    }

    @Cacheable("users")
    public int calculateCapacityByMonthByUser(String useruuid, String statusdate) {
        String url = "http://localhost:5460/users/command/calculateCapacityByMonthByUser?useruuid="+useruuid+"&statusdate="+statusdate;
        return restTemplate.getForObject(url, IntegerJsonResponse.class).getResult();
    }

    public User save(User user) {
        return null;
    }

    public boolean login(String username, String password) {
        String url = "http://localhost:5460/users/command/login?username="+username+"&password="+password;
        System.out.println(restTemplate.getForObject(url, String.class));
        return restTemplate.getForObject(url, String.class).equals("{\"result\":true}");
    }
}
