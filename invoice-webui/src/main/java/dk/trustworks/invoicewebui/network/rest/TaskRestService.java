package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Service
public class TaskRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public TaskRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    @Cacheable("tasks")
    public List<Task> findAll() {
        String url = apiGatewayUrl +"/tasks";
        ResponseEntity<Task[]> result = systemRestService.secureCall(url, GET, Task[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable("tasks")
    public Task findOne(String uuid) {
        String url = apiGatewayUrl +"/tasks/"+uuid;
        ResponseEntity<Task> result = systemRestService.secureCall(url, GET, Task.class);
        return result.getBody();
    }

    @Cacheable("tasks")
    public List<Task> findByProject(String projectuuid) {
        String url = apiGatewayUrl +"/projects/"+projectuuid+"/tasks";
        ResponseEntity<Task[]> result = systemRestService.secureCall(url, GET, Task[].class);
        return Arrays.asList(result.getBody());
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task save(Task task) {
        String url = apiGatewayUrl +"/tasks";
        ResponseEntity<Task> result = systemRestService.secureCall(url, POST, Task.class, task);
        return result.getBody();
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void delete(Task task) {
        delete(task.getUuid());
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void delete(String uuid) {
        String url = apiGatewayUrl +"/tasks/"+uuid;
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
