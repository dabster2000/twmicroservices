package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Week;
import dk.trustworks.invoicewebui.network.rest.WeekRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by hans on 09/09/2017.
 */

@Service
public class TimeService {

    @Autowired
    WeekRestService weekRestService;

    public TimeService() {
    }

    public void cloneTaskToWeek(LocalDate currentDate, User user) {
        List<Week> weeks = weekRestService.findByWeeknumberAndYearAndUseruuidOrderBySortingAsc(currentDate.minusWeeks(1).get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()), currentDate.minusWeeks(1).get(WeekFields.of(Locale.getDefault()).weekBasedYear()), user.getUuid());
        for (Week week : weeks) {
            weekRestService.save(new Week(UUID.randomUUID().toString(), currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()), currentDate.get(WeekFields.of(Locale.getDefault()).weekBasedYear()), week.getUser(), week.getTask(), week.getWorkasUser()));
        }
    }
}
