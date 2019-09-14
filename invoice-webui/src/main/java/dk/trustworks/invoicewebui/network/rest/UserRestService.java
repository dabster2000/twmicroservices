package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Role;
import dk.trustworks.invoicewebui.model.Salary;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.dto.LoginToken;
import dk.trustworks.invoicewebui.network.dto.IntegerJsonResponse;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class UserRestService {

    @Value("#{environment.USERSERVICE_URL}")
    private String userServiceUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public UserRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable("usersbyusername")
    public User findByUsername(String username) {
        return restTemplate.getForObject(userServiceUrl+"/users/search/findByUsername?username="+username, User.class);
    }

    @Cacheable("users")
    public User findOne(String uuid) {
        return restTemplate.getForObject(userServiceUrl+"/users/"+uuid, User.class);
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
        String url = userServiceUrl+"/users/command/calculateCapacityByMonthByUser?useruuid="+useruuid+"&statusdate="+statusdate;
        return restTemplate.getForObject(url, IntegerJsonResponse.class).getResult();
    }

    @CacheEvict(value = "users", allEntries = true)
    public User create(User user) {
        String url = userServiceUrl+"/users";
        return restTemplate.postForObject(url, user, User.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void update(User user) {
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
        for (Salary salary : salaries) {
            String url = userServiceUrl+"/users/"+user.getUuid()+"/salaries/"+salary.getUuid();
            restTemplate.delete(url);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteUserStatuses(User user, Set<UserStatus> userStatuses) {
        for (UserStatus userStatus : userStatuses) {
            String url = userServiceUrl+"/users/"+user.getUuid()+"/userstatuses/"+userStatus.getUuid();
            restTemplate.delete(url);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteRoles(User user, List<Role> roles) {
        for (Role role : roles) {
            String url = userServiceUrl+"/users/"+user.getUuid()+"/roles/type/"+role.getRole().name();
            restTemplate.delete(url);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void create(User user, Salary salary) {
        user.getSalaries().add(salary);
        String url = userServiceUrl+"/users/"+user.getUuid()+"/salaries";
        System.out.println("url = " + url);
        restTemplate.postForObject(url, salary, String.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void create(User user, UserStatus userStatus) {
        user.getStatuses().add(userStatus);
        String url = userServiceUrl+"/users/"+user.getUuid()+"/statuses";
        restTemplate.postForObject(url, userStatus, String.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void create(User user, Role role) {
        Validate.matchesPattern(role.getUuid(), "", "UUID must be blank, when instance is created");
        user.getRoleList().add(role);
        String url = userServiceUrl+"/users/"+user.getUuid()+"/roles";
        restTemplate.postForObject(url, role, String.class);
    }
}
