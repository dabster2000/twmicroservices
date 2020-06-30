package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.Work;
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
public class WorkRestService {

    @Value("#{environment.WORKSERVICE_URL}")
    private String workServiceUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public WorkRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    @Cacheable("work")
    public List<Work> findAll() {
        String url = workServiceUrl +"/work";
        ResponseEntity<Work[]> result = systemRestService.secureCall(url, GET, Work[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable("work")
    public Work findOne(long id) {
        String url = workServiceUrl +"/work/"+id;
        ResponseEntity<Work> result = systemRestService.secureCall(url, GET, Work.class);
        return result.getBody();
    }

    public List<Work> findByPeriodAndUserUUID(String fromDate, String toDate, String useruuid) {
        String url = workServiceUrl +"/users/"+useruuid+"/work?fromdate="+fromDate+"&todate="+toDate;
        ResponseEntity<Work[]> result = systemRestService.secureCall(url, GET, Work[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Work> findByTask(String uuid) {
        String url = workServiceUrl + "/tasks/"+uuid+"/work";
        ResponseEntity<Work[]> result = systemRestService.secureCall(url, GET, Work[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Work> findByRegisteredAndUseruuidAndTaskuuid() {
        return null;
    }

    @CacheEvict(value = "work", allEntries = true)
    public Work save(Work work) {
        String url = workServiceUrl+"/work";
        ResponseEntity<Work> result = systemRestService.secureCall(url, POST, Work.class, work);
        return result.getBody();
    }

    @CacheEvict(value = "work", allEntries = true)
    public void delete(Work work) {
        delete(work.getId());
    }

    @CacheEvict(value = "work", allEntries = true)
    public void delete(long id) {
        String url = workServiceUrl+"/work/"+id;
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
