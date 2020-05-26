package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Clientdata;
import dk.trustworks.invoicewebui.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Service
public class ProjectRestService {

    @Value("#{environment.CRMSERVICE_URL}")
    private String crmServiceUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public ProjectRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Project> findAll() {
        String url = crmServiceUrl + "/projects";
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable("projects")
    public Project findOne(String uuid) {
        String url = crmServiceUrl + "/projects/" + uuid;
        ResponseEntity<Project> result = systemRestService.secureCall(url, GET, Project.class);
        return result.getBody();
    }

    @Cacheable("projects")
    public List<Project> findByActiveTrue() {
        String url = crmServiceUrl + "/projects/search/findByActiveTrue";
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable("projects")
    public List<Project> findByClientAndActiveTrue(Client client) {
        String url = crmServiceUrl + "/clients/" + client.getUuid() + "/projects/search/findByActiveTrue";
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable("projects")
    public List<Project> findByClientuuid(String clientuuid) {
        String url = crmServiceUrl + "/clients/" + clientuuid + "/projects";
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable("projects")
    public List<Project> findByClientdata(Clientdata clientdata) {
        String url = crmServiceUrl + "/clientdata/" + clientdata.getUuid() + "/projects";
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable("projects")
    public List<Project> findByLocked(boolean isLocked) {
        String url = crmServiceUrl + "/search/findByLocked?locked="+isLocked;
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    @CacheEvict(value = "projects", allEntries = true)
    public void save(List<Project> projects) {
        projects.forEach(this::save);
    }

    @CacheEvict(value = "projects", allEntries = true)
    public Project save(Project project) {
        String url = crmServiceUrl+"/projects";
        return (Project) systemRestService.secureCall(url, POST, Project.class, project).getBody();//restTemplate.postForObject(url, user, User.class);
    }

    public void update(Project project) {
        String url = crmServiceUrl+"/projects";
        systemRestService.secureCall(url, PUT, Project.class, project).getBody(); //restTemplate.put(url, user);
    }

    @CacheEvict(value = "projects", allEntries = true)
    public void delete(Project project) {
        delete(project.getUuid());
    }

    @CacheEvict(value = "projects", allEntries = true)
    public void delete(String uuid) {
        String url = crmServiceUrl+"/projects/"+uuid;
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
