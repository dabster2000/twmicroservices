package dk.trustworks.invoicewebui.services;


import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.AvailabilityDocument;
import dk.trustworks.invoicewebui.model.dto.UserBooking;
import dk.trustworks.invoicewebui.network.rest.AvailabilityRestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityService implements InitializingBean {

    private static AvailabilityService instance;

    private final AvailabilityRestService availabilityRestService;

    @Autowired
    public AvailabilityService(AvailabilityRestService availabilityRestService) {
        this.availabilityRestService = availabilityRestService;
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static AvailabilityService get() {
        return instance;
    }


    public double getWorkdaysInMonth(String uuid, LocalDate currentDate) {
        return availabilityRestService.getWorkdaysInMonth(uuid, currentDate);
    }

    public AvailabilityDocument getConsultantAvailabilityByMonth(String useruuid, LocalDate month) {
        return availabilityRestService.getConsultantAvailabilityByMonth(useruuid, month);
    }

    public double countActiveConsultantsByMonth(LocalDate month) {
        return availabilityRestService.countActiveConsultantsByMonth(month);
    }

    public List<UserBooking> getUserBooking(int monthsInPast, int monthsInFuture) {
        return availabilityRestService.getUserBooking(monthsInPast, monthsInFuture);
    }
}
