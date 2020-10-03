package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.dto.AvailabilityDocument;
import dk.trustworks.invoicewebui.model.dto.UserBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;
import static org.springframework.http.HttpMethod.GET;

@Service
public class AvailabilityRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public AvailabilityRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public double getWorkdaysInMonth(String useruuid, LocalDate currentDate) {
        String url = apiGatewayUrl +"/cached/availabilities/months/"+stringIt(currentDate)+"/users/"+useruuid+"/workdays";
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody().getValue();
    }

    public AvailabilityDocument getConsultantAvailabilityByMonth(String useruuid, LocalDate month) {
        String url = apiGatewayUrl +"/cached/availabilities/months/"+stringIt(month)+"/users/"+useruuid+"";
        ResponseEntity<AvailabilityDocument> result = systemRestService.secureCall(url, GET, AvailabilityDocument.class);
        return result.getBody();
    }

    public double countActiveConsultantsByMonth(LocalDate month) {
        String url = apiGatewayUrl +"/cached/availabilities/months/"+stringIt(month)+"/users/count?statustype=ACTIVE&consultanttype=CONSULTANT";
        ResponseEntity<GraphKeyValue> result = systemRestService.secureCall(url, GET, GraphKeyValue.class);
        return result.getBody().getValue();
    }

    public List<UserBooking> getUserBooking(int monthsInPast, int monthsInFuture) {
        String url = apiGatewayUrl +"/cached/availabilities/booking?monthsinpast="+monthsInPast+"&monthsinfuture="+monthsInFuture;
        ResponseEntity<UserBooking[]> result = systemRestService.secureCall(url, GET, UserBooking[].class);
        return Arrays.asList(result.getBody());
    }
}
