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

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    private final Map<String, User> userCache = new HashMap<>();

    @Autowired
    public UserRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public User findByUsername(String username) {
        String url = apiGatewayUrl+"/users?username="+username;
        return ((User[]) systemRestService.secureCall(url, GET, User[].class).getBody())[0]; //restTemplate.getForObject(url, User.class, createHeaders(systemToken.getToken()));
    }

    public User findOne(String uuid) {
        if(userCache.containsKey(uuid)) return userCache.get(uuid);
        String url = apiGatewayUrl + "/users/" + uuid;
        User user = (User) systemRestService.secureCall(url, GET, User.class).getBody(); //restTemplate.getForObject(apiGatewayUrl + "/users/" + uuid, User.class);
        userCache.put(user.getUuid(), user);
        return user;
    }

    public List<User> findByOrderByUsername() {
        String url = apiGatewayUrl+"/users";
        ResponseEntity<User[]> result = systemRestService.secureCall(url, GET, User[].class);
        return Arrays.asList(result.getBody());
    }

    public User[] findBySlackusername(String userId) {
        ResponseEntity<User[]> result = systemRestService.secureCall(apiGatewayUrl+"/users/search/findBySlackusername?username="+userId, GET, User[].class);
        return result.getBody();
    }

    public List<User> findUsersByDateAndStatusListAndTypes(String date, String[] consultantStatusList, String... consultantTypes) {
        String url = apiGatewayUrl+"/users/search/findUsersByDateAndStatusListAndTypes?date="+date+"&consultantStatusList="+String.join(",",consultantStatusList)+"&consultantTypes="+String.join(",", consultantTypes)+"&shallow=false";
        System.out.println("url = " + url);
        ResponseEntity<User[]> result = systemRestService.secureCall(url, GET, User[].class);
        return Arrays.asList(result.getBody());
    }

    public int calculateCapacityByMonthByUser(String useruuid, String statusdate) {
        String url = apiGatewayUrl+"/users/command/calculateCapacityByMonthByUser?useruuid="+useruuid+"&statusdate="+statusdate;
        ResponseEntity<IntegerJsonResponse> result = systemRestService.secureCall(url, GET, IntegerJsonResponse.class);
        return result.getBody().getResult(); //restTemplate.getForObject(url, IntegerJsonResponse.class).getResult();
    }

    public List<Capacity> calculateCapacityByPeriod(LocalDate fromDate, LocalDate toDate) {
        String url = apiGatewayUrl+"/users/command/calculateCapacityByPeriod?fromdate="+ stringIt(fromDate) +"&todate="+ stringIt(toDate);
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

    public User create(User user) {
        userCache.clear();
        String url = apiGatewayUrl+"/users";
        return (User) systemRestService.secureCall(url, POST, User.class, user).getBody();//restTemplate.postForObject(url, user, User.class);
    }

    public void update(User user) {
        userCache.clear();
        String url = apiGatewayUrl+"/users/"+user.getUuid();
        systemRestService.secureCall(url, PUT, User.class, user).getBody(); //restTemplate.put(url, user);
    }

    public LoginToken login(String username, String password) {
        return systemRestService.login(username, password);
    }

    public void deleteSalaries(User user, Set<Salary> salaries) {
        userCache.clear();
        for (Salary salary : salaries) {
            String url = apiGatewayUrl+"/users/"+user.getUuid()+"/salaries/"+salary.getUuid();
            systemRestService.secureCall(url, DELETE, Salary.class); //restTemplate.delete(url);
        }
    }

    @Cacheable(cacheNames = "userstatuses")
    public List<UserStatus> findUserStatusList(String useruuid) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/statuses";
        ResponseEntity<UserStatus[]> result = systemRestService.secureCall(url, GET, UserStatus[].class);
        return Arrays.asList(result.getBody());
    }

    public void deleteUserStatuses(User user, Set<UserStatus> userStatuses) {
        for (UserStatus userStatus : userStatuses) {
            String url = apiGatewayUrl+"/users/"+user.getUuid()+"/statuses/"+userStatus.getUuid();
            systemRestService.secureCall(url, DELETE, UserStatus.class); //restTemplate.delete(url);
        }
    }

    public List<Role> findUserRoles(String useruuid) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/roles";
        ResponseEntity<Role[]> result = systemRestService.secureCall(url, GET, Role[].class);
        return Arrays.asList(result.getBody());
    }

    public void deleteRoles(User user, List<Role> roles) {
        userCache.clear();
        String url = apiGatewayUrl+"/users/"+user.getUuid()+"/roles";
        systemRestService.secureCall(url, DELETE, Role.class);
    }

    public void create(User user, Salary salary) {
        userCache.clear();
        user.getSalaries().add(salary);
        String url = apiGatewayUrl+"/users/"+user.getUuid()+"/salaries";
        systemRestService.secureCall(url, POST, String.class, salary); //restTemplate.postForObject(url, salary, String.class);
    }

    public void create(User user, UserStatus userStatus) {
        userCache.clear();
        user.getStatuses().add(userStatus);
        String url = apiGatewayUrl+"/users/"+user.getUuid()+"/statuses";
        systemRestService.secureCall(url, POST, String.class, userStatus); //restTemplate.postForObject(url, userStatus, String.class);
    }

    public void create(User user, Role role) {
        userCache.clear();
        user.getRoleList().add(role);
        String url = apiGatewayUrl+"/users/"+user.getUuid()+"/roles";
        systemRestService.secureCall(url, POST, String.class, role); //restTemplate.postForObject(url, role, String.class);
    }
}
