package dk.trustworks.invoicewebui.jobs;


import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.*;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.services.BudgetService;
import dk.trustworks.invoicewebui.services.RevenueService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.services.WorkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@Component
public class AchievementJob {

    private static final Logger log = LoggerFactory.getLogger(AchievementJob.class);

    private final UserService userService;
    private final WorkService workService;
    private final ReminderHistoryRepository reminderHistoryRepository;
    private final NotificationRepository notificationRepository;
    private final LogEventRepository logEventRepository;
    private final AmbitionRepository ambitionRepository;
    private final UserAmbitionDTORepository userAmbitionDTORepository;
    private final BudgetService budgetService;
    private final RevenueService revenueService;


    //@Autowired
    public AchievementJob(UserService userService, WorkService workService, ReminderHistoryRepository reminderHistoryRepository, NotificationRepository notificationRepository, LogEventRepository logEventRepository, AmbitionRepository ambitionRepository, UserAmbitionDTORepository userAmbitionDTORepository, BudgetService budgetService, RevenueService revenueService) {
        this.userService = userService;
        this.workService = workService;
        this.reminderHistoryRepository = reminderHistoryRepository;
        this.notificationRepository = notificationRepository;
        this.logEventRepository = logEventRepository;
        this.ambitionRepository = ambitionRepository;
        this.userAmbitionDTORepository = userAmbitionDTORepository;
        this.revenueService = revenueService;
        this.budgetService = budgetService;
    }

    @PostConstruct
    public void init() {
        //achievementCollector();
    }

    // TODO: Microservice
    //@Scheduled(cron = "0 0 23 * * ?")
    //@Scheduled(fixedRate = 10000)
    public void achievementCollector() {
        log.debug("AchievementJob.achievementCollector");

        for (User user : userService.findCurrentlyEmployedUsers(false, ConsultantType.CONSULTANT, ConsultantType.STAFF, ConsultantType.STUDENT)) {
            if(userService.isExternal(user)) continue;

            List<Achievement> achievementList = null;//achievementRepository.findByUseruuid(user.getUuid());
            /**
             * Disabled
             */
            /*
            testAchievement(user, achievementList, AchievementType.WORKWEEK40, isWortyOfWorkWeekAchievement(user, 40));
            testAchievement(user, achievementList, AchievementType.WORKWEEK50, isWortyOfWorkWeekAchievement(user, 50));
            testAchievement(user, achievementList, AchievementType.WORKWEEK60, isWortyOfWorkWeekAchievement(user, 60));
            */
            testAchievement(user, achievementList, AchievementType.VACATION3, isWorthyOfVacationAchievement(user, 3));
            testAchievement(user, achievementList, AchievementType.VACATION4, isWorthyOfVacationAchievement(user, 4));
            testAchievement(user, achievementList, AchievementType.VACATION5, isWorthyOfVacationAchievement(user, 5));
            System.out.println("1");
/*
            testAchievement(user, achievementList, AchievementType.INTRALOGIN14, isWorthyOfIntraLoginAchievement(user, 14));
            testAchievement(user, achievementList, AchievementType.INTRALOGIN21, isWorthyOfIntraLoginAchievement(user, 21));
            testAchievement(user, achievementList, AchievementType.INTRALOGIN28, isWorthyOfIntraLoginAchievement(user, 28));
*/
            testAchievement(user, achievementList, AchievementType.SPEEDDATES10, isWorthyOfSpeedDateAchievement(user, 10));
            testAchievement(user, achievementList, AchievementType.SPEEDDATES20, isWorthyOfSpeedDateAchievement(user, 20));
            testAchievement(user, achievementList, AchievementType.SPEEDDATES30, isWorthyOfSpeedDateAchievement(user, 30));
            System.out.println("2");

            testAchievement(user, achievementList, AchievementType.WEEKVACATION, isWorthyOfVacationAllWeeksAchievement(user));
            testAchievement(user, achievementList, AchievementType.MONTHVACATION, isWorthyOfVacationAllMonthsAchievement(user));
            System.out.println("3");

            testAchievement(user, achievementList, AchievementType.AMBITIONENTERED, isWorthyOfAmbitionCompleted(user));
            System.out.println("4");

            /*
            testAchievement(user, achievementList, AchievementType.CKOEXPENSE1, isWorthyOfCkoExpenseAchievement(user, 1));
            testAchievement(user, achievementList, AchievementType.CKOEXPENSE2, isWorthyOfCkoExpenseAchievement(user, 2));
            testAchievement(user, achievementList, AchievementType.CKOEXPENSE3, isWorthyOfCkoExpenseAchievement(user, 3));
            System.out.println("5");

             */

            testAchievement(user, achievementList, AchievementType.ANNIVERSARY3, isWorthyOfAnniversary(user, 3));
            testAchievement(user, achievementList, AchievementType.ANNIVERSARY5, isWorthyOfAnniversary(user, 5));
            testAchievement(user, achievementList, AchievementType.ANNIVERSARY10, isWorthyOfAnniversary(user, 10));
            System.out.println("6");

            testAchievement(user, achievementList, AchievementType.BUDGETBEATER5, isWorthyOfBudgetBeatersAchievement(user, 5));
            testAchievement(user, achievementList, AchievementType.BUDGETBEATER15, isWorthyOfBudgetBeatersAchievement(user, 15));
            testAchievement(user, achievementList, AchievementType.BUDGETBEATER30, isWorthyOfBudgetBeatersAchievement(user, 30));
            System.out.println("7");

            testAchievement(user, achievementList, AchievementType.BUBBLES3, isWorthyOfBubbleMemberAchievement(user, 3));
            testAchievement(user, achievementList, AchievementType.BUBBLES6, isWorthyOfBubbleMemberAchievement(user, 6));
            testAchievement(user, achievementList, AchievementType.BUBBLES9, isWorthyOfBubbleMemberAchievement(user, 9));
            System.out.println("8");

            testAchievement(user, achievementList, AchievementType.BUBBLELEADER, isWorthyOfBubbleLeaderAchievement(user));
            System.out.println("9");
        }
    }

    private void testAchievement(User user, List<Achievement> achievementList, AchievementType achievementType, boolean worthyOfVacationAchievement) {
        if (achievementList.stream().noneMatch(achievement -> (achievement.getAchievement().equals(achievementType)))) {
            if (worthyOfVacationAchievement) {
                log.info("user = " + user.getUsername() + ", achivement = " + achievementType, user.getUuid());
                notificationRepository.save(new Notification(user, LocalDate.now(), LocalDate.now().plusMonths(3), "New Achievement", "You've made it: "+achievementType.toString(), achievementType.getDescription(), achievementType.getNumber()+"", NotificationType.ACHIEVEMENT));
                //achievementRepository.save(new Achievement(user, LocalDate.now(), achievementType));
            }
        }
    }

    /*
    private boolean isWorthyOfCkoExpenseAchievement(User user, int years) {
        List<CKOExpense> ckoExpenseList = ckoExpenseRepository.findCKOExpenseByUseruuid(user.getUuid());
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
            if(expensePerYearMap.get(key) >= 20400 && expensePerYearMap.get(key) <= 27600) {
                length++;
            } else {
                length = 0;
            }
            if(length >= years) found = true;
        }

        return found;
    }

     */

    private boolean isWorthyOfBubbleMemberAchievement(User user, int minBubbles) {
        /*
        int bubbleCount = 0;
        for (BubbleMember bubbleMember : bubbleMemberRepository.findByUseruuid(user.getUuid())) {
            if(bubbleMember.getBubble().isActive()) {
                bubbleCount++;
            }
        }
        return (bubbleCount>=minBubbles);

         */
        return false;
    }

    private boolean isWorthyOfBubbleLeaderAchievement(User user) {
        /*
        for (Bubble bubble : bubbleRepository.findBubblesByActiveTrue()) {
            if(bubble.getUser().getUuid().equals(user.getUuid())) return true;
        }
         */
        return false;
    }

    private boolean isWorthyOfVacationAllMonthsAchievement(User user) {
        List<Work> workList = workService.findVacationByUser(user.getUuid());
        int[] months = new int[12];
        for (Work work : workList) {
            if(work.getWorkduration() >= 7.4) months[work.getRegistered().getMonthValue()-1] += 1;
        }
        for (int month : months) {
            if(month == 0) return false;
        }
        return true;
    }

    private boolean isWorthyOfBudgetBeatersAchievement(User user, int minMonths) {
        LocalDate employedDate = userService.findEmployedDate(user).orElse(LocalDate.now());
        System.out.println("A");
        int count = 0;
        do {
            double budgetHoursByMonth = budgetService.getConsultantBudgetHoursByMonth(user.getUuid(), employedDate);
            System.out.println("K");
            double revenueHoursByMonth = revenueService.getRegisteredHoursForSingleMonthAndSingleConsultant(user.getUuid(), employedDate);
            System.out.println("L");
            if(revenueHoursByMonth>budgetHoursByMonth) count++;
            employedDate = employedDate.plusMonths(1);
            System.out.println("B"+count);
        } while (employedDate.isBefore(LocalDate.now().withDayOfMonth(1)));
        System.out.println("C");
        return count >= minMonths;
    }

    private boolean isWorthyOfAnniversary(User user, int years) {
        System.out.println("AchievementJob.isWorthyOfAnniversary");
        System.out.println("user = " + user.getUsername() + ", years = " + years);
        UserStatus status = userService.getStatus(user, false, StatusType.ACTIVE);
        System.out.println("status = " + status);
        System.out.println("status.getStatusdate().isBefore(LocalDate.now().minusYears(years)); = " + status.getStatusdate().isBefore(LocalDate.now().minusYears(years)));
        return status.getStatusdate().isBefore(LocalDate.now().minusYears(years));
    }

    private boolean isWorthyOfAmbitionCompleted(User user) {
        int ambitionCount = ambitionRepository.findAmbitionByActiveIsTrue().size();
        int userAmbitionCount = userAmbitionDTORepository.findUserAmbitionByUseruuidAndActiveTrue(user.getUuid()).size();
        return userAmbitionCount>=ambitionCount;
    }

    private boolean isWorthyOfIntraLoginAchievement(User user, int daysInARow) {
        int consequtiveDays = 0;
        LocalDate foundDate = LocalDate.now().withYear(2014);
        for (LogEvent logEvent : logEventRepository.findByType(LogType.LOGIN)) {
            LocalDate localDate = Instant.ofEpochMilli(logEvent.getDateTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if(!localDate.minusDays(1).isEqual(foundDate)) {
                consequtiveDays = 0;
            } else {
                consequtiveDays++;
            }
            if(consequtiveDays>=daysInARow) return true;
            foundDate = localDate;
        }
        return false;
    }

    private boolean isWorthyOfVacationAllWeeksAchievement(User user) {
        // TODO:
        /*
        List<Work> workList = workService.findVacationByUser(user.getUuid());
        int[] weeks = new int[53];
        for (Work work : workList) {
            int week = work.getRegistered().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
            if(work.getWorkduration() > 7.4) weeks[week-1] += 1;
        }
        for (int week : weeks) {
            if(week == 0) return false;
        }

         */
        return true;
    }

    private boolean isWorthyOfSpeedDateAchievement(User user, int minDates) {
        int count = 0;
        List<ReminderHistory> others = reminderHistoryRepository.findByTargetuuidAndType(user.getUuid(), ReminderType.SPEEDDATE);
        for (ReminderHistory reminderHistory : reminderHistoryRepository.findByTypeAndUseruuidOrderByTransmissionDateDesc(ReminderType.SPEEDDATE, user.getUuid())) {
            if(others.stream().anyMatch(reminderHistory1 -> reminderHistory1.getUser().getUuid().equals(reminderHistory.getTargetuuid()))) count++;
        }
        return count >= minDates;
    }

    private boolean isWorthyOfVacationAchievement(User user, int minWork) {
        List<Work> workList = workService.findVacationByUser(user.getUuid());
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
/*
    private boolean isWortyOfWorkWeekAchievement(User user, int minWork) {
        List<Work> workList = workService.findBillableWorkByUser(user.getUuid());
        Map<String, Double> hoursPerWeekMap = getHoursPerWeek(workList);
        if(hoursPerWeekMap.size()==0) return false;

        Double aDouble = hoursPerWeekMap.values().stream().max(Double::compareTo).get();
        return aDouble >= minWork;
    }

 */

    private Map<String, Double> getHoursPerWeek(List<Work> workList) {
        Map<String, Double> hoursPerWeekMap = new HashMap<>();
        for (Work work : workList) {
            String key = work.getRegistered().getYear() + "" + work.getRegistered().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
            hoursPerWeekMap.putIfAbsent(key, 0.0);
            double hours = hoursPerWeekMap.get(key);
            hours += work.getWorkduration();
            hoursPerWeekMap.replace(key, hours);
        }
        return hoursPerWeekMap;
    }
}
