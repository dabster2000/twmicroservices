package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.ContractConsultant;
import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.network.dto.KeyValueDTO;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.*;

@Service
public class ContractRestService {

    protected static Logger logger = LoggerFactory.getLogger(ContractRestService.class.getName());

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public ContractRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    // *** CONTRACT OPERATIONS ***

    public List<Contract> findAll() {
        String url = apiGatewayUrl + "/contracts";
        logger.debug(url);
        ResponseEntity<Contract[]> result = systemRestService.secureCall(url, GET, Contract[].class);
        return Arrays.asList(result.getBody());
    }

    public Contract findByUuid(String uuid) {
        String url = apiGatewayUrl + "/contracts/"+uuid;
        logger.debug(url);
        ResponseEntity<Contract> result = systemRestService.secureCall(url, GET, Contract.class);
        return result.getBody();
    }

    public List<Contract> findByClientuuid(String clientuuid) {
        String url = apiGatewayUrl + "/clients/"+clientuuid+"/contracts";
        logger.debug(url);
        ResponseEntity<Contract[]> result = systemRestService.secureCall(url, GET, Contract[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Contract> findByProjectuuid(String projectuuid) {
        String url = apiGatewayUrl + "/projects/"+projectuuid+"/contracts";
        logger.debug(url);
        ResponseEntity<Contract[]> result = systemRestService.secureCall(url, GET, Contract[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Project> findProjectsByContractuuid(String contractuuid) {
        String url = apiGatewayUrl + "/contracts/"+contractuuid+"/projects";
        logger.debug(url);
        ResponseEntity<Project[]> result = systemRestService.secureCall(url, GET, Project[].class);
        return Arrays.asList(result.getBody());
    }

    @Cacheable(cacheNames = "contracts")
    public List<Contract> findByActiveFromLessThanEqualAndActiveToGreaterThanEqualAndStatusIn(LocalDate activeTo, LocalDate activeFrom, ContractStatus... statusList) {
        String url = apiGatewayUrl + "/contracts?fromdate=" + activeFrom + "&todate=" + activeTo + "&statuslist=" + Arrays.stream(statusList).map(Enum::toString).collect(Collectors.joining(","));
        logger.info(url);
        ResponseEntity<Contract[]> result = systemRestService.secureCall(url, GET, Contract[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Contract> findTimeActiveConsultantContracts(String useruuid, LocalDate activeOn) {
        String url = apiGatewayUrl + "/users/"+useruuid+"/contracts?activedate="+DateUtils.stringIt(activeOn);
        logger.info(url);
        ResponseEntity<Contract[]> result = systemRestService.secureCall(url, GET, Contract[].class);
        return Arrays.asList(result.getBody());
    }

    public Double findRateByProjectuuidAndUseruuidAndDate(String projectuuid, String useruuid, LocalDate date) {
        String url = apiGatewayUrl + "/contracts/search/findRateByProjectuuidAndUseruuidAndDate?projectuuid="+projectuuid+"&useruuid="+useruuid+"&date="+DateUtils.stringIt(date);
        logger.info(url);
        ResponseEntity<KeyValueDTO> result = systemRestService.secureCall(url, GET, KeyValueDTO.class);
        return Double.parseDouble(result.getBody().getValue());
    }

    public Contract save(Contract contract) {
        String url = apiGatewayUrl +"/contracts";
        logger.debug(url);
        ResponseEntity<Contract> savedContract = systemRestService.secureCall(url, POST, Contract.class, contract);
        return savedContract.getBody();
    }

    public void update(Contract contract) {
        String url = apiGatewayUrl +"/contracts";
        systemRestService.secureCall(url, PUT, Void.class, contract);
    }

    public void delete(String uuid) {
        String url = apiGatewayUrl +"/contracts/"+uuid;
        systemRestService.secureCall(url, DELETE, Void.class);
    }

    // *** PROJECT OPERATIONS ***

    public void addProjectToContract(Contract contract, Project project) {
        String url = apiGatewayUrl +"/contracts/"+contract.getUuid()+"/projects/"+project.getUuid();
        logger.debug(url);
        systemRestService.secureCall(url, POST, Project.class, project);
    }

    public void removeProjectFromContract(Contract contract, Project project) {
        String url = apiGatewayUrl +"/contracts/"+contract.getUuid()+"/projects/"+project.getUuid();
        logger.debug(url);
        systemRestService.secureCall(url, DELETE, Void.class);
    }

    // *** CONSULTANT OPERATIONS ***

    public void addConsultant(Contract contract, ContractConsultant contractConsultant) {
        String url = apiGatewayUrl +"/contracts/"+contract.getUuid()+"/consultants/"+contractConsultant.getUuid();
        logger.info(url);
        systemRestService.secureCall(url, POST, Void.class, contractConsultant);
    }

    public void updateConsultant(Contract contract, ContractConsultant contractConsultant) {
        String url = apiGatewayUrl +"/contracts/"+contract.getUuid()+"/consultants/"+contractConsultant.getUuid();
        logger.info(url);
        systemRestService.secureCall(url, PUT, Void.class, contractConsultant);
    }

    public void removeConsultant(Contract contract, ContractConsultant contractConsultant) {
        String url = apiGatewayUrl +"/contracts/"+contract.getUuid()+"/consultants/"+contractConsultant.getUuid();
        logger.debug(url);
        systemRestService.secureCall(url, DELETE, Void.class);
    }
}
