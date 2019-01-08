package dk.trustworks.invoicewebui.jobs;


import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.AchievementType;
import dk.trustworks.invoicewebui.model.enums.ReminderType;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.services.WorkService;
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
import java.util.stream.Collectors;

@Component
public class AchievementJob {

    private static final Logger log = LoggerFactory.getLogger(AchievementJob.class);

    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;
    private final WorkService workService;
    private final ReminderHistoryRepository reminderHistoryRepository;
    private final CKOExpenseRepository ckoExpenseRepository;
    private final NotificationRepository notificationRepository;


    @Autowired
    public AchievementJob(AchievementRepository achievementRepository, UserRepository userRepository, WorkRepository workRepository, WorkService workService, ReminderHistoryRepository reminderHistoryRepository, CKOExpenseRepository ckoExpenseRepository, NotificationRepository notificationRepository) {
        this.achievementRepository = achievementRepository;
        this.userRepository = userRepository;
        this.workRepository = workRepository;
        this.workService = workService;
        this.reminderHistoryRepository = reminderHistoryRepository;
        this.ckoExpenseRepository = ckoExpenseRepository;
        this.notificationRepository = notificationRepository;
    }

    @PostConstruct
    public void init() {
    }

    @Scheduled(cron = "0 0 23 * * ?")
    //@Scheduled(fixedRate = 10000)
    public void achievementCollector() {
        log.info("AchievementJob.achievementCollector");

        for (User user : userRepository.findAll()) {
            List<Achievement> achievementList = achievementRepository.findByUser(user);
            textAchievement(user, achievementList, AchievementType.WORKWEEK40, isWortyOfWorkWeekAchievement(user, 40));
            textAchievement(user, achievementList, AchievementType.WORKWEEK50, isWortyOfWorkWeekAchievement(user, 50));
            textAchievement(user, achievementList, AchievementType.WORKWEEK60, isWortyOfWorkWeekAchievement(user, 60));

            textAchievement(user, achievementList, AchievementType.VACATION3, isWorthyOfVacationAchievement(user, 3));
            textAchievement(user, achievementList, AchievementType.VACATION4, isWorthyOfVacationAchievement(user, 4));
            textAchievement(user, achievementList, AchievementType.VACATION5, isWorthyOfVacationAchievement(user, 5));

            textAchievement(user, achievementList, AchievementType.SPEEDDATES10, isWorthyOfSpeedDateAchievement(user, 10));
            textAchievement(user, achievementList, AchievementType.SPEEDDATES20, isWorthyOfSpeedDateAchievement(user, 20));
            textAchievement(user, achievementList, AchievementType.SPEEDDATES30, isWorthyOfSpeedDateAchievement(user, 30));

            textAchievement(user, achievementList, AchievementType.WEEKVACATION, isWorthyOfVacationAllWeeksAchievement(user));
            textAchievement(user, achievementList, AchievementType.MONTHVACATION, isWorthyOfVacationAllMonthsAchievement(user));

            textAchievement(user, achievementList, AchievementType.CKOEXPENSE1, isWorthyOfCkoExpenseAchievement(user, 1));
            textAchievement(user, achievementList, AchievementType.CKOEXPENSE2, isWorthyOfCkoExpenseAchievement(user, 2));
            textAchievement(user, achievementList, AchievementType.CKOEXPENSE3, isWorthyOfCkoExpenseAchievement(user, 3));
        }
    }

    private void textAchievement(User user, List<Achievement> achievementList, AchievementType achievementType, boolean worthyOfVacationAchievement) {
        if (achievementList.stream().noneMatch(achievement -> (achievement.getAchievement().equals(achievementType)))) {
            if (worthyOfVacationAchievement) {
                log.info("user = " + user.getUsername() + ", achivement = " + achievementType);
                notificationRepository.save(new Notification(user, LocalDate.now(), LocalDate.now().plusMonths(3), "New Achievement", "You've made it: "+achievementType.toString(), "", achievementType.getNumber()+""));
                achievementRepository.save(new Achievement(user, LocalDate.now(), achievementType));
            }
        }
    }

    private boolean isWorthyOfCkoExpenseAchievement(User user, int years) {
        List<CKOExpense> ckoExpenseList = ckoExpenseRepository.findCKOExpenseByUser(user);
        Map<String, Double> expensePerYearMap = new HashMap<>();
        for (CKOExpense ckoExpense : ckoExpenseList) {
            LocalDate localDate = ckoExpense.getEventdate();
            String key = localDate.getYear() + "";
            expensePerYearMap.putIfAbsent(key, 0.0);
            double expense = expensePerYearMap.get(key);
            expense += ckoExpense.getPrice();
            expensePerYearMap.replace(key, expense);
        }

        if(expensePerYearMap.size()==0) return false;

        int length = 0;
        boolean found = false;
        for (String key : expensePerYearMap.keySet().stream().sorted().collect(Collectors.toList())) {
            if(expensePerYearMap.get(key) >= 29000 && expensePerYearMap.get(key) <= 30000) {
                length++;
            } else {
                length = 0;
            }
            if(length >= years) found = true;
        }

        return found;
    }

    private boolean isWorthyOfVacationAllMonthsAchievement(User user) {
        List<Work> workList = workService.findVacationByUser(user);
        int[] months = new int[12];
        for (Work work : workList) {
            if(work.getWorkduration() >= 7.4) months[work.getMonth()] += 1;
        }
        for (int month : months) {
            if(month == 0) return false;
        }
        return true;
    }

    private boolean isWorthyOfVacationAllWeeksAchievement(User user) {
        List<Work> workList = workService.findVacationByUser(user);
        int[] weeks = new int[53];
        for (Work work : workList) {
            LocalDate date = LocalDate.of(work.getYear(), work.getMonth() + 1, work.getDay());
            int week = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
            if(work.getWorkduration() > 7.4) weeks[week-1] += 1;
        }
        for (int week : weeks) {
            if(week == 0) return false;
        }
        return true;
    }

    private boolean isWorthyOfSpeedDateAchievement(User user, int minDates) {
        int count = 0;
        List<ReminderHistory> others = reminderHistoryRepository.findByTargetuuidAndType(user.getUuid(), ReminderType.SPEEDDATE);
        for (ReminderHistory reminderHistory : reminderHistoryRepository.findByTypeAndUserOrderByTransmissionDateDesc(ReminderType.SPEEDDATE, user)) {
            if(others.stream().anyMatch(reminderHistory1 -> reminderHistory1.getUser().getUuid().equals(reminderHistory.getTargetuuid()))) count++;
        }
        return count >= minDates;
    }

    private boolean isWorthyOfVacationAchievement(User user, int minWork) {
        List<Work> workList = workService.findVacationByUser(user);
        Map<String, Double> hoursPerWeekMap = getHoursPerWeek(workList);
        if(hoursPerWeekMap.size()==0) return false;

        int length = 0;
        boolean found = false;
        for (String key : hoursPerWeekMap.keySet().stream().sorted().collect(Collectors.toList())) {
            if(hoursPerWeekMap.get(key) >= 37) {
                length++;
            } else {
                length = 0;
            }
            if(length>=minWork) found = true;
        }

        return found;
    }

    private boolean isWortyOfWorkWeekAchievement(User user, int minWork) {
        List<Work> workList = workRepository.findBillableWorkByUser(user.getUuid());
        Map<String, Double> hoursPerWeekMap = getHoursPerWeek(workList);
        if(hoursPerWeekMap.size()==0) return false;

        Double aDouble = hoursPerWeekMap.values().stream().max(Double::compareTo).get();
        return aDouble >= minWork;
    }

    private Map<String, Double> getHoursPerWeek(List<Work> workList) {
        Map<String, Double> hoursPerWeekMap = new HashMap<>();
        for (Work work : workList) {
            LocalDate date = LocalDate.of(work.getYear(), work.getMonth() + 1, work.getDay());
            String key = date.getYear() + "" + date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
            hoursPerWeekMap.putIfAbsent(key, 0.0);
            double hours = hoursPerWeekMap.get(key);
            hours += work.getWorkduration();
            hoursPerWeekMap.replace(key, hours);
        }
        return hoursPerWeekMap;
    }
}