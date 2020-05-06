package dk.trustworks.invoicewebui.network.rest;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.*;

@Service
public class UserRestService {

    @Value("#{environment.USERSERVICE_URL}")
    private String userServiceUrl;

    @Value("#{environment.USERSERVICE_USERNAME}")
    private String userserviceUsername;

    @Value("#{environment.USERSERVICE_PASSWORD}")
    private String userservicePassword;

    private final RestTemplate restTemplate;

    private final Map<String, User> userCache = new HashMap<>();

    private LoginToken systemToken;

    @Autowired
    public UserRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void construct() {
        systemToken = login(userserviceUsername, userservicePassword);
    }

    //@Cacheable("usersbyusername")
    public User findByUsername(String username) {
        String url = userServiceUrl+"/users/search/findByUsername?username="+username;
        return (User) secureCall(url, GET, User.class).getBody(); //restTemplate.getForObject(url, User.class, createHeaders(systemToken.getToken()));
    }

    public User findOne(String uuid) {
        if(userCache.containsKey(uuid)) return userCache.get(uuid);
        String url = userServiceUrl + "/users/" + uuid;
        User user = (User) secureCall(url, GET, User.class).getBody(); //restTemplate.getForObject(userServiceUrl + "/users/" + uuid, User.class);
        userCache.put(user.getUuid(), user);
        return user;
    }

    @Cacheable("users")
    public List<User> findByOrderByUsername() {
        String url = userServiceUrl+"/users";
        ResponseEntity<User[]> result = secureCall(url, GET, User[].class);
        return Arrays.asList(result.getBody());
    }

    public User[] findBySlackusername(String userId) {
        ResponseEntity<User[]> result = secureCall(userServiceUrl+"/users/search/findBySlackusername?username="+userId, GET, User[].class);
        return result.getBody();
    }

    @Cacheable("users")
    public List<User> findUsersByDateAndStatusListAndTypes(String date, String[] consultantStatusList, String... consultantTypes) {
        String url = userServiceUrl+"/users/search/findUsersByDateAndStatusListAndTypes?date="+date+"&consultantStatusList="+String.join(",",consultantStatusList)+"&consultantTypes="+String.join(",", consultantTypes);
        ResponseEntity<User[]> result = secureCall(url, GET, User[].class);
        return Arrays.asList(result.getBody());
    }

    //@Cacheable("users")
    public int calculateCapacityByMonthByUser(String useruuid, String statusdate) {
        String url = userServiceUrl+"/users/command/calculateCapacityByMonthByUser?useruuid="+useruuid+"&statusdate="+statusdate;
        ResponseEntity<IntegerJsonResponse> result = secureCall(url, GET, IntegerJsonResponse.class);
        return result.getBody().getResult(); //restTemplate.getForObject(url, IntegerJsonResponse.class).getResult();
    }

    public List<Capacity> calculateCapacityByPeriod(LocalDate fromDate, LocalDate toDate) {
        String url = userServiceUrl+"/users/command/calculateCapacityByPeriod?fromdate="+ stringIt(fromDate) +"&todate="+ stringIt(toDate);
        ResponseEntity<Capacity[]> result = secureCall(url, GET, Capacity[].class);
        return Arrays.asList(result.getBody()); //restTemplate.getForObject(url, IntegerJsonResponse.class).getResult();
    }

    @CacheEvict(value = "users", allEntries = true)
    public User create(User user) {
        userCache.clear();
        String url = userServiceUrl+"/users";
        return (User) secureCall(url, POST, User.class, user).getBody();//restTemplate.postForObject(url, user, User.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void update(User user) {
        userCache.clear();
        String url = userServiceUrl+"/users/"+user.getUuid();
        secureCall(url, PUT, User.class, user).getBody(); //restTemplate.put(url, user);
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
            secureCall(url, DELETE, Salary.class); //restTemplate.delete(url);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteUserStatuses(User user, Set<UserStatus> userStatuses) {
        userCache.clear();
        for (UserStatus userStatus : userStatuses) {
            String url = userServiceUrl+"/users/"+user.getUuid()+"/statuses/"+userStatus.getUuid();
            secureCall(url, DELETE, UserStatus.class); //restTemplate.delete(url);
        }
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteRoles(User user, List<Role> roles) {
        userCache.clear();
        String url = userServiceUrl+"/users/"+user.getUuid()+"/roles";
        secureCall(url, DELETE, Role.class); //restTemplate.delete(url);
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
        secureCall(url, POST, String.class, salary); //restTemplate.postForObject(url, salary, String.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void create(User user, UserStatus userStatus) {
        userCache.clear();
        user.getStatuses().add(userStatus);
        String url = userServiceUrl+"/users/"+user.getUuid()+"/statuses";
        secureCall(url, POST, String.class, userStatus); //restTemplate.postForObject(url, userStatus, String.class);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void create(User user, Role role) {
        userCache.clear();
        //Validate.matchesPattern(role.getUuid(), "", "UUID must be blank, when instance is created");
        user.getRoleList().add(role);
        String url = userServiceUrl+"/users/"+user.getUuid()+"/roles";
        secureCall(url, POST, String.class, role); //restTemplate.postForObject(url, role, String.class);
    }

    private ResponseEntity secureCall(String url, HttpMethod method, Class c) {
        try {
            HttpEntity entity = new HttpEntity(createHeaders(systemToken.getToken()));
            return restTemplate.exchange(url, method, entity, c);
        } catch (RestClientException e) {
            systemToken = login(userserviceUsername, userservicePassword);
            HttpEntity entity = new HttpEntity(createHeaders(systemToken.getToken()));
            return restTemplate.exchange(url, method, entity, c);
        }
    }

    private ResponseEntity secureCall(String url, HttpMethod method, Class c, Object payload) {
        try {
            HttpEntity entity = new HttpEntity(payload, createHeaders(systemToken.getToken()));
            return restTemplate.exchange(url, method, entity, c);
        } catch (RestClientException e) {
            systemToken = login(userserviceUsername, userservicePassword);
            HttpEntity entity = new HttpEntity(payload, createHeaders(systemToken.getToken()));
            return restTemplate.exchange(url, method, entity, c);
        }
    }

    private HttpHeaders createHeaders(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.set( "Authorization", "Bearer " + token);
        return headers;
    }
}
