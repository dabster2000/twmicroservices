package dk.trustworks.invoicewebui.jobs;


import dk.trustworks.invoicewebui.model.Achievement;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.model.enums.AchievementType;
import dk.trustworks.invoicewebui.repositories.AchievementRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AchievementJob {

    private static final Logger log = LoggerFactory.getLogger(AchievementJob.class);

    private final AchievementRepository achievementRepository;

    private final UserRepository userRepository;

    private final WorkRepository workRepository;

    @Autowired
    public AchievementJob(AchievementRepository achievementRepository, UserRepository userRepository, WorkRepository workRepository) {
        this.achievementRepository = achievementRepository;
        this.userRepository = userRepository;
        this.workRepository = workRepository;
    }

    @PostConstruct
    public void init() {
    }

    //@Scheduled(cron = "0 0 23 * * ?")
    @Scheduled(fixedRate = 10000)
    public void achievementCollector() {
        log.info("AchievementJob.achievementCollector");

        for (User user : userRepository.findAll()) {
            List<Achievement> achievementList = achievementRepository.findByUser(user);
            if(!achievementList.stream().anyMatch(achievement -> (achievement.getAchievement().equals(AchievementType.WORKWEEK40)))) {
                //log.info("user = " + user.getUsername());
                if (isWortyOfWorkWeekAchievement(user, 40)) {
                    log.info("user = " + user.getUsername() + ", achivement = " + AchievementType.WORKWEEK40);
                    achievementRepository.save(new Achievement(user, LocalDate.now(), AchievementType.WORKWEEK40));
                }
            }

            if(!achievementList.stream().anyMatch(achievement -> (achievement.getAchievement().equals(AchievementType.WORKWEEK50))))
                if(isWortyOfWorkWeekAchievement(user, 50)) {
                    log.info("user = " + user.getUsername() + ", achivement = " + AchievementType.WORKWEEK50);
                    achievementRepository.save(new Achievement(user, LocalDate.now(), AchievementType.WORKWEEK50));
                }

            if(!achievementList.stream().anyMatch(achievement -> (achievement.getAchievement().equals(AchievementType.WORKWEEK60))))
                if(isWortyOfWorkWeekAchievement(user, 60)) {
                    log.info("user = " + user.getUsername() + ", achivement = " + AchievementType.WORKWEEK60);
                    achievementRepository.save(new Achievement(user, LocalDate.now(), AchievementType.WORKWEEK60));
                }
        }
    }

    private boolean isWortyOfWorkWeekAchievement(User user, int minWork) {
        Map<String, Double> hoursPerWeekMap = new HashMap<>();
        for (Work work : workRepository.findBillableWorkByUser(user.getUuid())) {
            LocalDate date = LocalDate.of(work.getYear(), work.getMonth()+1, work.getDay());
            String key = date.getYear()+""+date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
            hoursPerWeekMap.putIfAbsent(key, 0.0);
            double hours = hoursPerWeekMap.get(key);
            hours += work.getWorkduration();
            hoursPerWeekMap.replace(key, hours);
        }
        if(hoursPerWeekMap.size()==0) return false;

        Double aDouble = hoursPerWeekMap.values().stream().max(Double::compareTo).get();
        return aDouble >= minWork;
    }
}
