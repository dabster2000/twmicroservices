package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Week;
import dk.trustworks.invoicewebui.repositories.WeekRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 09/09/2017.
 */

@Service
public class TimeService {

    @Autowired
    WeekRepository weekRepository;

    public TimeService() {
    }

    @Transactional
    public void cloneTaskToWeek(int weekNumber, int year, User user) {
        List<Week> weeks = weekRepository.findByWeeknumberAndYearAndUserOrderBySortingAsc(weekNumber - 1, year, user);
        for (Week week : weeks) {
            weekRepository.save(new Week(UUID.randomUUID().toString(), weekNumber, year, week.getUser(), week.getTask()));
        }
    }

}
