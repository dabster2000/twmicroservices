package dk.trustworks.invoicewebui.network.clients;

import com.fasterxml.jackson.databind.SerializationFeature;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.network.clients.model.vacation.VacationDate;
import dk.trustworks.invoicewebui.network.clients.model.vacation.VacationPeriod;
import dk.trustworks.invoicewebui.network.clients.model.vacation.VacationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class VacationAPI {

    @Autowired
    private RestTemplate restTemplate;

    public List<VacationPeriod> getVacationPeriods(User user, List<Work> vacationList) {
        VacationRequest vacationRequest = new VacationRequest(user.getUsername(), user.getStatuses().get(0).getStatusdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), new ArrayList<>());
        for (Work work : vacationList) {
            vacationRequest.getVacationDates().add(new VacationDate(work.getWorkduration(), work.getRegistered().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");

        HttpEntity<VacationRequest> entity = new HttpEntity<>(vacationRequest, requestHeaders);

        ResponseEntity<VacationPeriod[]> exchange = restTemplate.exchange("https://trustworks.free.beeceptor.com/getvacation", HttpMethod.POST, entity, VacationPeriod[].class);

        return Arrays.asList(exchange.getBody());
    }

}
