package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.TeamMemberType;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.*;

@JBossLog
@Service
public class TeamRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public TeamRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Team> getAllTeams() {
        String url = apiGatewayUrl +"/teams";
        ResponseEntity<Team[]> result = systemRestService.secureCall(url, GET, Team[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Team> findByRoles(String useruuid, LocalDate date, String... roles) {
        String url = apiGatewayUrl +"/teams/search/findByRoles?useruuid="+useruuid+"&date="+stringIt(date)+"&roles="+String.join(",", roles);
        ResponseEntity<Team[]> result = systemRestService.secureCall(url, GET, Team[].class);
        return Arrays.asList(result.getBody());
    }

    public List<TeamRole> findUserTeamRoles(String useruuid) {
        String url = apiGatewayUrl+"/users/"+useruuid+"/teamroles";
        ResponseEntity<TeamRole[]> result = systemRestService.secureCall(url, GET, TeamRole[].class);
        return Arrays.asList(result.getBody());
    }

    public List<User> getUsersByTeamByMonth(String teamuuid, LocalDate month) {
        String url = apiGatewayUrl +"/teams/"+teamuuid+"/users/search/findByMonth?month="+stringIt(month);
        ResponseEntity<User[]> result = systemRestService.secureCall(url, GET, User[].class);
        return Arrays.asList(result.getBody());
    }

    public List<User> findTeamleadersByMonth(String teamuuid, LocalDate month) {
        String url = apiGatewayUrl +"/teams/"+teamuuid+"/users/search/findTeamleadersByMonth?month="+stringIt(month);
        ResponseEntity<User[]> result = systemRestService.secureCall(url, GET, User[].class);
        return Arrays.asList(result.getBody());
    }

    public List<User> getUsersByTeamByFiscalYear(String teamuuid, int fiscalYear) {
        String url = apiGatewayUrl +"/teams/"+teamuuid+"/users/search/findByFiscalYear?fiscalyear="+fiscalYear;
        ResponseEntity<User[]> result = systemRestService.secureCall(url, GET, User[].class);
        return Arrays.asList(result.getBody());
    }

    public void addUserToTeam(String teamuuid, TeamRole teamrole) {
        String url = apiGatewayUrl+"/teams/"+teamuuid+"/users";
        systemRestService.secureCall(url, POST, String.class, teamrole); //restTemplate.postForObject(url, salary, String.class);
    }

    @CacheEvict(cacheNames = "user", allEntries = true)
    public void deleteTeamRoles(String teamuuid, Set<TeamRole> teamRoles) {
        for (TeamRole teamrole : teamRoles) {
            String url = apiGatewayUrl+"/teams/"+teamuuid+"/users?teamroleuuid="+teamrole.getUuid();
            systemRestService.secureCall(url, DELETE, TeamRole.class);
        }
    }

    public String getTeamuuidsAsMember(User user) {
        String teamuuid = null;
        for (TeamRole userTeamRole : findUserTeamRoles(user.getUuid()).stream()
                .filter(teamRole -> teamRole.getEnddate()==null || teamRole.getEnddate().isAfter(LocalDate.now()))
                .collect(Collectors.toList())) {
            teamuuid = userTeamRole.getTeamuuid();
            if(userTeamRole.getTeammembertype().equals(TeamMemberType.MEMBER)) break;
        }
        return teamuuid;
    }

    public String getTeamuuidsAsLeader(User user) {
        String teamuuid = null;
        for (TeamRole userTeamRole : findUserTeamRoles(user.getUuid()).stream()
                .filter(teamRole -> teamRole.getEnddate()==null || teamRole.getEnddate().isAfter(LocalDate.now()))
                .collect(Collectors.toList())) {
            teamuuid = userTeamRole.getTeamuuid();
            if(userTeamRole.getTeammembertype().equals(TeamMemberType.LEADER)) break;
        }
        return teamuuid;
    }

    public List<User> getUniqueUsersFromTeamsByFiscalYear(int fiscalyear, String... teamuuids) {
        Map<String, User> usersMap = new HashMap<>();
        for (String teamuuid : teamuuids) {
            getUsersByTeamByFiscalYear(teamuuid, fiscalyear).forEach(user -> usersMap.putIfAbsent(user.getUuid(), user));
        }
        return new ArrayList<>(usersMap.values());
    }

    public List<User> getUniqueUsersFromTeamsByMonth(LocalDate periodStart, String... teamuuids) {
        Map<String, User> usersMap = new HashMap<>();
        for (String teamuuid : teamuuids) {
            getUsersByTeamByMonth(teamuuid, periodStart).forEach(user -> usersMap.putIfAbsent(user.getUuid(), user));
        }
        return new ArrayList<>(usersMap.values());
    }

    public float getAvgGoodPeopleByPeriod(String teamuuid, LocalDate startDate, LocalDate endDate) {
        float avgGoodPeople = 0f;
        int count = 0;
        do {
            avgGoodPeople += getUsersByTeamByMonth(teamuuid, startDate).size();
            count++;
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(endDate));
        avgGoodPeople = avgGoodPeople / count;
        return avgGoodPeople;
    }
}
