package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Week;
import dk.trustworks.invoicewebui.model.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Service
public class WeekRestService {

    @Value("#{environment.APISERVICE_URL}")
    private String apiServiceUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public WeekRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Work> findAll() {
        String url = apiServiceUrl +"/week";
        ResponseEntity<Work[]> result = systemRestService.secureCall(url, GET, Work[].class);
        return Arrays.asList(result.getBody());
    }

    public Work findOne(long id) {
        String url = apiServiceUrl +"/week/"+id;
        ResponseEntity<Work> result = systemRestService.secureCall(url, GET, Work.class);
        return result.getBody();
    }

    public List<Week> findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(int i, int i1, String uuid) {
        // TODO: Implement
        return null;
    }

    public void save(Week week) {
        // TODO: Implement
    }

    public void delete(String uuid) {
        // TODO: Implement
    }
}
