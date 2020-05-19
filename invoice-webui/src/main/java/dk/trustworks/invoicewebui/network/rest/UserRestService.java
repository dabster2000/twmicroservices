package dk.trustworks.invoicewebui.network.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.trustworks.invoicewebui.model.Role;
import dk.trustworks.invoicewebui.model.Salary;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.dto.Capacity;
import dk.trustworks.invoicewebui.model.dto.LoginToken;
import dk.trustworks.invoicewebui.network.dto.IntegerJsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.*;

@Service
public class UserRestService {

    @Value("#{environment.USERSERVICE_URL}")
    private String userServiceUrl;

    private final SystemRestService systemRestService;

    private final RestTemplate restTemplate;

    private final Map<String, User> userCache = new HashMap<>();

    @Autowired
    public UserRestService(SystemRestService systemRestService, RestTemplate restTemplate) {
        this.systemRestService = systemRestService;
        this.restTemplate = restTemplate;
    }

    public User findByUsername(String username) {
        String url = userServiceUrl+"/users/search/findByUsername?username="+username;
        return (User) systemRestService.secureCall(url, GET, User.class).getBody(); //restTemplate.getForObject(url, User.class, createHeaders(systemToken.getToken()));
    }

    public User findOne(String uuid) {
        if(userCache.containsKey(uuid)) return userCache.get(uuid);
        String url = userServiceUrl + "/users/" + uuid;
        User user = (User) systemRestService.secureCall(url, GET, User.class).getBody(); //restTemplate.getForObject(userServiceUrl + "/users/" + uuid, User.class);
        userCache.put(user.getUuid(), user);
        return user;
    }

    @Cacheable("users")
    public List<User> findByOrderByUsername() {
        String url = userServiceUrl+"/users";
        ResponseEntity<User[]> result = systemRestService.secureCall(url, GET, User[].class);
        return Arrays.asList(result.getBody());
    }

    public User[] findBySlackusername(String userId) {
        ResponseEntity<User[]> result = systemRestService.secureCall(userServiceUrl+"/users/search/findBySlackusername?username="+userId, GET, User[].class);
        return result.getBody();
    }

    @Cacheable("users")
    public List<User> findUsersByDateAndStatusListAndTypes(String date, String[] consultantStatusList, String... consultantTypes) {
        String url = userServiceUrl+"/users/search/findUsersByDateAndStatusListAndTypes?date="+date+"&consultantStatusList="+String.join(",",consultantStatusList)+"&consultantTypes="+String.join(",", consultantTypes);
        ResponseEntity<User[]> result = systemRestService.secureCall(url, GET, User[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable("users")
    public int calculateCapacityByMonthByUser(String useruuid, String statusdate) {
        String url = userServiceUrl+"/users/command/calculateCapacityByMonthByUser?useruuid="+useruuid+"&statusdate="+statusdate;
        ResponseEntity<IntegerJsonResponse> result = systemRestService.secureCall(url, GET, IntegerJsonResponse.class);
        return result.getBody().getResult(); //restTemplate.getForObject(url, IntegerJsonResponse.class).getResult();
    }

    public List<Capacity> calculateCapacityByPeriod(LocalDate fromDate, LocalDate toDate) {
        String url = userServiceUrl+"/users/command/calculateCapacityByPeriod?fromdate="+ stringIt(fromDate) +"&todate="+ stringIt(toDate);
        ResponseEntity<String> responseEntity = systemRestService.secureCall(url, GET, String.class);
        ObjectMapper mapper = new ObjectMapper();
        Capacity[] result = new Capacity[0];
        try {
            result = mapper.readValue(responseEntity.getBody(), Capacity[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.asList(result); //restTemplate.getForObject(url, IntegerJsonResponse.class).getResult();
    }

    @CacheEvict(value = "users", allEntries = true)
    public User create(User user) {
        userCache.clear();
        String url = userServiceUrl+"/users";
        return (User) systemRestService.secureCall(url, POST, User.class, user).getBody();//restTemplate.postForObject(url, user, User.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void update(User user) {
        userCache.clear();
        String url = userServiceUrl+"/users/"+user.getUuid();
        systemRestService.secureCall(url, PUT, User.class, user).getBody(); //restTemplate.put(url, user);
    }

    public LoginToken login(String username, String password) {
        String url = userServiceUrl+"/users/command/login?username="+username+"&password="+password;
        return restTemplate.getForObject(url, LoginToken.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteSalaries(User user, Set<Salary> salaries) {
        userCache.clear();
        for (Salary salary : salaries) {
            String url = userServiceUrl+"/users/"+user.getUuid()+"/salaries/"+salary.getUuid();
            systemRestService.secureCall(url, DELETE, Salary.class); //restTemplate.delete(url);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteUserStatuses(User user, Set<UserStatus> userStatuses) {
        userCache.clear();
        for (UserStatus userStatus : userStatuses) {
            String url = userServiceUrl+"/users/"+user.getUuid()+"/statuses/"+userStatus.getUuid();
            systemRestService.secureCall(url, DELETE, UserStatus.class); //restTemplate.delete(url);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteRoles(User user, List<Role> roles) {
        userCache.clear();
        String url = userServiceUrl+"/users/"+user.getUuid()+"/roles";
        systemRestService.secureCall(url, DELETE, Role.class);

    }

    @CacheEvict(value = "users", allEntries = true)
    public void create(User user, Salary salary) {
        userCache.clear();
        user.getSalaries().add(salary);
        String url = userServiceUrl+"/users/"+user.getUuid()+"/salaries";
        systemRestService.secureCall(url, POST, String.class, salary); //restTemplate.postForObject(url, salary, String.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void create(User user, UserStatus userStatus) {
        userCache.clear();
        user.getStatuses().add(userStatus);
        String url = userServiceUrl+"/users/"+user.getUuid()+"/statuses";
        systemRestService.secureCall(url, POST, String.class, userStatus); //restTemplate.postForObject(url, userStatus, String.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void create(User user, Role role) {
        userCache.clear();
        user.getRoleList().add(role);
        String url = userServiceUrl+"/users/"+user.getUuid()+"/roles";
        systemRestService.secureCall(url, POST, String.class, role); //restTemplate.postForObject(url, role, String.class);
    }
}
