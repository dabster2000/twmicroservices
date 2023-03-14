package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.network.dto.KeyValueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Service
public class WorkRestService {

    protected static Logger logger = LoggerFactory.getLogger(WorkRestService.class.getName());

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public WorkRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Work> findAll() {
        String url = apiGatewayUrl +"/work";
        logger.debug(url);
        ResponseEntity<Work[]> result = systemRestService.secureCall(url, GET, Work[].class);
        return Arrays.asList(result.getBody());
    }

    public Work findOne(long id) {
        String url = apiGatewayUrl +"/work/"+id;
        logger.debug(url);
        ResponseEntity<Work> result = systemRestService.secureCall(url, GET, Work.class);
        return result.getBody();
    }

    public List<Work> findByPeriodAndUserUUID(String fromDate, String toDate, String useruuid) {
        String url = apiGatewayUrl +"/users/"+useruuid+"/work?fromdate="+fromDate+"&todate="+toDate;
        logger.debug(url);
        ResponseEntity<Work[]> result = systemRestService.secureCall(url, GET, Work[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Work> findByPeriod(LocalDate fromDate, LocalDate toDate) {
        String url = apiGatewayUrl +"/work/search/findByPeriod?fromdate="+stringIt(fromDate)+"&todate="+stringIt(toDate);
        logger.debug(url);
        ResponseEntity<Work[]> result = systemRestService.secureCall(url, GET, Work[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Work> findByTask(String uuid) {
        String url = apiGatewayUrl + "/tasks/"+uuid+"/work";
        logger.debug(url);
        ResponseEntity<Work[]> result = systemRestService.secureCall(url, GET, Work[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Work> findVacationByUser(String useruuid) {
        String url = apiGatewayUrl + "/users/"+useruuid+"/vacation";
        logger.debug(url);
        ResponseEntity<Work[]> result = systemRestService.secureCall(url, GET, Work[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Work> findByTasks(List<Task> tasks) {
        String url = apiGatewayUrl + "/work?taskuuids="+tasks.stream().map(Task::getUuid).collect(Collectors.joining(","));
        logger.debug(url);
        ResponseEntity<Work[]> result = systemRestService.secureCall(url, GET, Work[].class);
        return Arrays.asList(result.getBody());
    }

    public Double findAmountUsedByContract(Contract contract) {
        String url = apiGatewayUrl + "/contracts/"+contract.getUuid()+"/registeredamount";
        logger.debug(url);
        ResponseEntity<KeyValueDTO> result = systemRestService.secureCall(url, GET, KeyValueDTO.class);
        return Double.parseDouble(result.getBody().getValue());
    }

    public List<Work> findWorkOnContract(String contractuuid) {
        String url = apiGatewayUrl + "/contracts/" + contractuuid + "/work";
        logger.debug(url);
        ResponseEntity<Work[]> result = systemRestService.secureCall(url, GET, Work[].class);
        return Arrays.asList(result.getBody());
    }

    public void save(Work work) {
        String url = apiGatewayUrl +"/work";
        logger.debug(url);
        systemRestService.secureCall(url, POST, Void.class, work);
    }
}
