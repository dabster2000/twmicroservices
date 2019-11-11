package dk.trustworks.invoicewebui.network.rest;

import com.gs.collections.impl.map.mutable.SynchronizedMutableMap;
import dk.trustworks.invoicewebui.model.Role;
import dk.trustworks.invoicewebui.model.Salary;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.dto.LoginToken;
import dk.trustworks.invoicewebui.network.dto.IntegerJsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UserRestService {

    @Value("#{environment.USERSERVICE_URL}")
    private String userServiceUrl;

    private final RestTemplate restTemplate;

    private final Map<String, User> userCache = new HashMap<>();

    @Autowired
    public UserRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable("usersbyusername")
    public User findByUsername(String username) {
        return restTemplate.getForObject(userServiceUrl+"/users/search/findByUsername?username="+username, User.class);
    }

    public User findOne(String uuid) {
        if(userCache.containsKey(uuid)) return userCache.get(uuid);
        User user = restTemplate.getForObject(userServiceUrl + "/users/" + uuid, User.class);
        userCache.put(user.getUuid(), user);
        return user;
    }

    @Cacheable("users")
    public List<User> findByOrderByUsername() {
        return Arrays.asList(restTemplate.getForObject(userServiceUrl+"/users", User[].class));
    }

    public User[] findBySlackusername(String userId) {
        return restTemplate.getForObject(userServiceUrl+"/users/search/findBySlackusername?username="+userId, User[].class);
    }

    @Cacheable("users")
    public List<User> findUsersByDateAndStatusListAndTypes(String date, String[] consultantStatusList, String... consultantTypes) {
        String url = userServiceUrl+"/users/search/findUsersByDateAndStatusListAndTypes?date="+date+"&consultantStatusList="+String.join(",",consultantStatusList)+"&consultantTypes="+String.join(",", consultantTypes);
        return Arrays.asList(restTemplate.getForObject(url, User[].class));
    }

    @Cacheable("users")
    public int calculateCapacityByMonthByUser(String useruuid, String statusdate) {
        // System.out.println("----- UserRestService.calculateCapacityByMonthByUser");
        String url = userServiceUrl+"/users/command/calculateCapacityByMonthByUser?useruuid="+useruuid+"&statusdate="+statusdate;
        return restTemplate.getForObject(url, IntegerJsonResponse.class).getResult();
    }

    @CacheEvict(value = "users", allEntries = true)
    public User create(User user) {
        userCache.clear();
        String url = userServiceUrl+"/users";
        return restTemplate.postForObject(url, user, User.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void update(User user) {
        userCache.clear();
        String url = userServiceUrl+"/users/"+user.getUuid();
        restTemplate.put(url, user);
    }

    public boolean login(String username, String password) {
        String url = userServiceUrl+"/users/command/login?username="+username+"&password="+password;
        System.out.println(restTemplate.getForObject(url, LoginToken.class));
        return restTemplate.getForObject(url, LoginToken.class).isSuccess();
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteSalaries(User user, Set<Salary> salaries) {
        userCache.clear();
        for (Salary salary : salaries) {
            String url = userServiceUrl+"/users/"+user.getUuid()+"/salaries/"+salary.getUuid();
            restTemplate.delete(url);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteUserStatuses(User user, Set<UserStatus> userStatuses) {
        userCache.clear();
        for (UserStatus userStatus : userStatuses) {
            String url = userServiceUrl+"/users/"+user.getUuid()+"/statuses/"+userStatus.getUuid();
            restTemplate.delete(url);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteRoles(User user, List<Role> roles) {
        userCache.clear();
        String url = userServiceUrl+"/users/"+user.getUuid()+"/roles";
        restTemplate.delete(url);
        /*
        for (Role role : roles) {
            String url = userServiceUrl+"/users/"+user.getUuid()+"/roles/type/"+role.getRole().name();
            restTemplate.delete(url);
        }
         */
    }

    @CacheEvict(value = "users", allEntries = true)
    public void create(User user, Salary salary) {
        userCache.clear();
        user.getSalaries().add(salary);
        String url = userServiceUrl+"/users/"+user.getUuid()+"/salaries";
        restTemplate.postForObject(url, salary, String.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void create(User user, UserStatus userStatus) {
        userCache.clear();
        user.getStatuses().add(userStatus);
        String url = userServiceUrl+"/users/"+user.getUuid()+"/statuses";
        restTemplate.postForObject(url, userStatus, String.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void create(User user, Role role) {
        userCache.clear();
        //Validate.matchesPattern(role.getUuid(), "", "UUID must be blank, when instance is created");
        user.getRoleList().add(role);
        String url = userServiceUrl+"/users/"+user.getUuid()+"/roles";
        restTemplate.postForObject(url, role, String.class);
    }
}
