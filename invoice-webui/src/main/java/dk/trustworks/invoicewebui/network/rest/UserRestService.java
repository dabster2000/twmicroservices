package dk.trustworks.invoicewebui.network.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.Capacity;
import dk.trustworks.invoicewebui.model.dto.LoginToken;
import dk.trustworks.invoicewebui.network.dto.IntegerJsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.*;

@Service
public class UserRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public UserRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public User findByUsername(String username) {
        String url = apiGatewayUrl+"/users?username="+username;
        return ((User[]) systemRestService.secureCall(url, GET, User[].class).getBody())[0]; //restTemplate.getForObject(url, User.class, createHeaders(systemToken.getToken()));
    }

    @Cacheable("users")
    public User findOne(String uuid, boolean shallow) {
        String url = apiGatewayUrl + "/users/" + uuid + "?shallow="+shallow;
        return (User) systemRestService.secureCall(url, GET, User.class).getBody();
    }

    @Cacheable("users")
    public List<User> findByOrderByUsername(boolean shallow) {
        String url = apiGatewayUrl+"/users?shallow="+shallow;
        ResponseEntity<User[]> result = systemRestService.secureCall(url, GET, User[].class);
        return Arrays.asList(result.getBody());
    }

    public User[] findBySlackusername(String userId) {
        ResponseEntity<User[]> result = systemRestService.secureCall(apiGatewayUrl+"/users/search/findBySlackusername?username="+userId, GET, User[].class);
        return result.getBody();
    }

    @Cacheable("users")
    public List<User> findUsersByDateAndStatusListAndTypes(String date, String[] consultantStatusList, boolean shallow, String... consultantTypes) {
        String url = apiGatewayUrl+"/users/search/findUsersByDateAndStatusListAndTypes?date="+date+"&consultantStatusList="+String.join(",",consultantStatusList)+"&consultantTypes="+String.join(",", consultantTypes)+"&shallow="+shallow;
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
        String url = apiGatewayUrl+"/users";
        return (User) systemRestService.secureCall(url, POST, User.class, user).getBody();
    }

    public void update(User user) {
        String url = apiGatewayUrl+"/users/"+user.getUuid();
        systemRestService.secureCall(url, PUT, User.class, user);
    }

    public void updateBirthday(User user) {
        String url = apiGatewayUrl+"/users/"+user.getUuid()+"/birthday";
        systemRestService.secureCall(url, PUT, User.class, user);
    }

    public UserContactinfo findUserContactinfo(String useruuid) {
        String url = apiGatewayUrl + "/users/"+useruuid+"/contactinfo";
        ResponseEntity<UserContactinfo> result = systemRestService.secureCall(url, GET, UserContactinfo.class);
        return result.getBody();
    }

    public void updateUserContactinfo(String useruuid, UserContactinfo userContactinfo) {
        String url = apiGatewayUrl + "/users/"+useruuid+"/contactinfo";
        systemRestService.secureCall(url, PUT, Void.class, userContactinfo);
    }

    public LoginToken login(String username, String password) {
        return systemRestService.login(username, password);
    }

    public void deleteSalaries(String useruuid, Set<Salary> salaries) {
        for (Salary salary : salaries) {
            String url = apiGatewayUrl+"/users/"+useruuid+"/salaries/"+salary.getUuid();
            systemRestService.secureCall(url, DELETE, Salary.class);
        }
    }

    public List<UserStatus> findUserStatusList(String useruuid) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/statuses";
        ResponseEntity<UserStatus[]> result = systemRestService.secureCall(url, GET, UserStatus[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Salary> findUserSalaries(String useruuid) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/salaries";
        ResponseEntity<Salary[]> result = systemRestService.secureCall(url, GET, Salary[].class);
        return Arrays.asList(result.getBody());
    }

    public void deleteUserStatuses(String useruuid, Set<UserStatus> userStatuses) {
        for (UserStatus userStatus : userStatuses) {
            String url = apiGatewayUrl+"/users/"+useruuid+"/statuses/"+userStatus.getUuid();
            systemRestService.secureCall(url, DELETE, UserStatus.class); //restTemplate.delete(url);
        }
    }

    public List<Role> findUserRoles(String useruuid) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/roles";
        ResponseEntity<Role[]> result = systemRestService.secureCall(url, GET, Role[].class);
        return Arrays.asList(result.getBody());
    }

    public void deleteRoles(User user, List<Role> roles) {
        String url = apiGatewayUrl+"/users/"+user.getUuid()+"/roles";
        systemRestService.secureCall(url, DELETE, Role.class);
    }

    public void create(String useruuid, Salary salary) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/salaries";
        systemRestService.secureCall(url, POST, String.class, salary); //restTemplate.postForObject(url, salary, String.class);
    }

    public void create(String useruuid, UserStatus userStatus) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/statuses";
        systemRestService.secureCall(url, POST, String.class, userStatus); //restTemplate.postForObject(url, userStatus, String.class);
    }

    public void create(String useruuid, Role role) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/roles";
        systemRestService.secureCall(url, POST, String.class, role); //restTemplate.postForObject(url, role, String.class);
    }
}
