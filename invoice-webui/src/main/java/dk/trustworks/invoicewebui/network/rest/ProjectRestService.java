package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.network.dto.KeyValueDTO;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Service
public class ProjectRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public ProjectRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Project> findAll() {
        String url = apiGatewayUrl + "/projects";
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    public Project findOne(String uuid) {
        String url = apiGatewayUrl + "/projects/" + uuid;
        ResponseEntity<Project> result = systemRestService.secureCall(url, GET, Project.class);
        return result.getBody();
    }

    public List<Project> findByActiveTrue() {
        String url = apiGatewayUrl + "/projects/active";
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    public KeyValueDTO findByWorkonCount(LocalDate fromDate, LocalDate toDate) {
        String url = apiGatewayUrl + "/projects/workedon/count?fromdate="+DateUtils.stringIt(fromDate)+"&todate="+DateUtils.stringIt(toDate);
        ResponseEntity<KeyValueDTO> result = systemRestService.secureCall(url, GET, KeyValueDTO.class);
        return result.getBody();
    }

    public List<Project> findByClientAndActiveTrue(String clientuuid) {
        String url = apiGatewayUrl + "/clients/" + clientuuid + "/projects/active";
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Project> findByClientuuid(String clientuuid) {
        String url = apiGatewayUrl + "/clients/" + clientuuid + "/projects";
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Project> findByClientdata(Clientdata clientdata) {
        String url = apiGatewayUrl + "/clientdata/" + clientdata.getUuid() + "/projects";
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Project> findByLocked(boolean isLocked) {
        String url = apiGatewayUrl + "/search/findByLocked?locked="+isLocked;
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    public double findRate(String projectuuid, String useruuid, LocalDate date) {
        String url = apiGatewayUrl + "/projects/"+projectuuid+"/users/"+useruuid+"/rates?date="+ DateUtils.stringIt(date);
        ResponseEntity<KeyValueDTO> result = systemRestService.secureCall(url, GET, KeyValueDTO.class);
        return Double.parseDouble(result.getBody().getValue());
    }

    public void save(List<Project> projects) {
        projects.forEach(this::save);
    }

    public Project save(Project project) {
        String url = apiGatewayUrl +"/projects";
        return (Project) systemRestService.secureCall(url, POST, Project.class, project).getBody();//restTemplate.postForObject(url, user, User.class);
    }

    public void update(Project project) {
        String url = apiGatewayUrl +"/projects";
        systemRestService.secureCall(url, PUT, Project.class, project).getBody(); //restTemplate.put(url, user);
    }

    public void delete(Project project) {
        delete(project.getUuid());
    }

    public void delete(String uuid) {
        String url = apiGatewayUrl +"/projects/"+uuid;
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
