package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.model.News;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.UserStatus;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.repositories.NewsRepository;
import dk.trustworks.invoicewebui.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static com.google.common.hash.Hashing.sha512;

/**
 * Created by hans on 12/09/2017.
 */

@Component
public class AnniversaryManagerJob {

    private static final Logger log = LoggerFactory.getLogger(AnniversaryManagerJob.class);

    private final NewsRepository newsRepository;

    private final UserService userService;

    @Autowired
    public AnniversaryManagerJob(NewsRepository newsRepository, UserService userService) {
        this.newsRepository = newsRepository;
        this.userService = userService;
    }

    @PostConstruct
    public void onStartup() {
        //findAnniversaries();
    }

    @Transactional
    // TODO: Microservice
    //@Scheduled(cron = "0 1 1 * * ?")
    public void findAnniversaries() {
        for (User user : userService.findCurrentlyEmployedUsers()) {
            if(userService.isExternal(user)) continue;

            String sha512hex = sha512().hashString(user.getUuid()+LocalDate.now().withDayOfMonth(1), StandardCharsets.UTF_8).toString();
            if(newsRepository.findFirstBySha512(sha512hex).size()>0) continue;

            List<UserStatus> statuses = user.getStatuses();
            UserStatus firstStatus = userService.getStatus(user, true, StatusType.ACTIVE);

            if(firstStatus.getStatusdate().isAfter(LocalDate.now().minusWeeks(2)) && firstStatus.getStatus().equals(StatusType.ACTIVE)) {
                newsRepository.save(new News(
                        "A huge welcome to "+user.getFirstname()+" "+user.getLastname()+
                                " who "+ ((firstStatus.getStatusdate().isAfter(LocalDate.now()))?"is joining":"just joined") +" Trustworks!",
                        firstStatus.getStatusdate(),
                        "new_employee", null,
                        sha512hex
                ));
                continue;
            } else if (firstStatus.getStatusdate().isAfter(LocalDate.now().minusWeeks(2)) && statuses.size() >=3 &&
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
            }
        }
    }
}
