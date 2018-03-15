package dk.trustworks.invoicewebui.jobs;

import com.google.common.hash.Hashing;
import dk.trustworks.invoicewebui.model.News;
import dk.trustworks.invoicewebui.model.StatusType;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.repositories.NewsRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hans on 12/09/2017.
 */

@Component
public class AnniversaryManagerJob {

    private static final Logger log = LoggerFactory.getLogger(AnniversaryManagerJob.class);

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    public AnniversaryManagerJob() {
    }

    @PostConstruct
    public void onStartup() {
        findAnniversaries();
    }

    @Transactional
    @Scheduled(cron = "0 1 1 * * ?")
    public void findAnniversaries() {
        for (User user : userRepository.findByActiveTrue()) {
            String sha512hex = Hashing.sha512().hashString(user.getUuid()+LocalDate.now().withDayOfMonth(1), StandardCharsets.UTF_8).toString();
            if(newsRepository.findFirstBySha512(sha512hex).size()>0) continue;
            List<UserStatus> statuses = user.getStatuses();
            statuses.sort(Comparator.comparing(UserStatus::getStatusdate));
            UserStatus firstStatus = statuses.get(0);

            if(firstStatus.getStatusdate().isAfter(LocalDate.now().minusMonths(1)) && firstStatus.getStatus().equals(StatusType.ACTIVE)) {
                newsRepository.save(new News(
                        "A huge welcome to "+user.getFirstname()+" "+user.getLastname()+
                                " who "+ ((firstStatus.getStatusdate().isAfter(LocalDate.now()))?"is joining":"just joined") +" Trustworks!",
                        firstStatus.getStatusdate(),
                        "new_employee", null,
                        sha512hex
                ));
                continue;
            } else if (statuses.size() >=3 &&
                    statuses.get(statuses.size()-1).getStatus().equals(StatusType.ACTIVE) &&
                    statuses.get(statuses.size()-2).getStatus().equals(StatusType.TERMINATED)) {
                newsRepository.save(new News(
                        "A huge welcome BACK to "+user.getFirstname()+" "+user.getLastname()+
                                " who "+ ((statuses.get(statuses.size()-1).getStatusdate().isAfter(LocalDate.now()))?"is rejoining":"just rejoined") +" Trustworks!",
                        statuses.get(statuses.size()-1).getStatusdate(),
                        "new_employee", null,
                        sha512hex
                ));
                continue;
            }

            LocalDate dateWithCurrentYear = firstStatus.getStatusdate().withYear(LocalDate.now().getYear());
            if(dateWithCurrentYear.isAfter(LocalDate.now().minusWeeks(1)) &&
                    dateWithCurrentYear.isBefore(LocalDate.now().plusWeeks(2))) {
                log.debug("user.getUsername() = " + user.getUsername());
                log.debug("firstStatus = " + firstStatus.getStatusdate());
                log.debug("dateWithCurrentYear = " + dateWithCurrentYear);
                if(user.getUsername().equalsIgnoreCase("hans.lassen") ||
                        user.getUsername().equalsIgnoreCase("tobias.kjoelsen") ||
                        user.getUsername().equalsIgnoreCase("thomas.gammelvind") ||
                        user.getUsername().equalsIgnoreCase("peter.gaarde")) continue;
                newsRepository.save(new News(
                        user.getFirstname()+" "+user.getLastname()+
                                ", You are... terrifically tireless, exceptionally, excellent, abundantly appreciated and..." +
                                "magnificient beyond words! So glad you're a part of our team! Happy "+
                                (LocalDate.now().getYear()-firstStatus.getStatusdate().getYear())+
                                ". year anniversary.",
                        dateWithCurrentYear,
                        "anniversary", null,
                        sha512hex
                ));
                continue;
            }
        }
    }
}
