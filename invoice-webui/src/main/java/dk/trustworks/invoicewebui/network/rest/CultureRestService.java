package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Lesson;
import dk.trustworks.invoicewebui.model.LessonRole;
import dk.trustworks.invoicewebui.model.PerformanceGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Service
public class CultureRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    @Autowired
    public CultureRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    private final SystemRestService systemRestService;

    public List<PerformanceGroups> findActivePerformanceGroups() {
        String url = apiGatewayUrl + "/culture/lessonframed/performancegroups/active";
        ResponseEntity<PerformanceGroups[]> result = systemRestService.secureCall(url, GET, PerformanceGroups[].class);
        return Arrays.asList(result.getBody());
    }

    public List<LessonRole> findAllRoles() {
        String url = apiGatewayUrl + "/culture/lessonframed/roles";
        ResponseEntity<LessonRole[]> result = systemRestService.secureCall(url, GET, LessonRole[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Lesson> findByUserAndProject(String useruuid, String projectuuid) {
        String url = apiGatewayUrl + "/culture/lessonframed/lessons?useruuid="+useruuid+"&projectuuid="+projectuuid;
        ResponseEntity<Lesson[]> result = systemRestService.secureCall(url, GET, Lesson[].class);
        return Arrays.asList(result.getBody());
    }

    public void save(Lesson lesson) {
        String url = apiGatewayUrl +"/culture/lessonframed";
        systemRestService.secureCall(url, POST, Lesson.class, lesson);
    }

}
