package dk.trustworks.invoicewebui.jobs;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Attachment;
import allbegray.slack.type.Field;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.logging.Logger;


@Component
public class CheckBudgetJob {

    static final Logger log = Logger.getLogger(CheckBudgetJob.class.getName());

    private final UserRepository userRepository;

    @Value("${motherSlackBotToken}")
    private String motherSlackToken;

    @Autowired
    public CheckBudgetJob(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void startup() {
        //checkBudgetJob();
    }

    //@Scheduled(cron = "0 0 0 1 1/1 *")
    //@Scheduled(cron = "0 30 10 8,18 * ?")
    public void checkBudgetJob() {
        SlackWebApiClient motherWebApiClient = SlackClientFactory.createWebApiClient(motherSlackToken);
        log.info("CheckBudgetJob.execute");

        LocalDate localDateStart = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        log.info("localDateStart = " + localDateStart);
        LocalDate localDateEnd = localDateStart.plusMonths(3);
        log.info("localDateEnd = " + localDateEnd);

        int[] businessDaysInMonth = new int[3];

        for (int i = 0; i < 3; i++) {
            businessDaysInMonth[i] = getWorkingDaysBetweenTwoDates(localDateStart.plusMonths(i).toDate(), localDateStart.plusMonths(i+1).toDate());
        }

        List<User> activeUsers = userRepository.findByActiveTrue();

        for (User user : activeUsers) {
            log.info("--- " + user + " ---");
            //if(!user.getUsername().equalsIgnoreCase("hans.lassen")) continue;
            String message = "*Here is a quick summary of "+localDateStart.monthOfYear().getAsText()+"*\n\n" +
                    "According to my calculations there is "+businessDaysInMonth[0]+" work days in "+localDateStart.monthOfYear().getAsText()+", " +
                    ""+businessDaysInMonth[1]+" days in "+localDateStart.plusMonths(1).monthOfYear().getAsText()+" " +
                    ", and "+businessDaysInMonth[1]+" in "+localDateStart.plusMonths(2).monthOfYear().getAsText()+".\n\n";

            message += "You have the following tasks assigned in "+localDateStart.monthOfYear().getAsText()+":\n";

            //double[] totalBudget = new double[3];


            Map<Task, double[]> budgetMap = new HashMap<>();
            /*
            for (Budget budget : budgetRepository.findByPeriodAndUseruuid(
                    Integer.parseInt(localDateStart.toString("yyyyMMdd")),
                    Integer.parseInt(localDateEnd.minusDays(1).toString("yyyyMMdd")),
                    user.getUuid()).stream().filter(budget -> budget.getBudget()>0.0).collect(Collectors.toList())) {
                log.info("*************************");
                log.info("budget = " + budget);

                Double rate = contractService.findConsultantRate(budget.getYear(), budget.getMonth(), 1, budget.getUser(), budget.getTask());
                if (rate == null) continue;

                Task task = budget.getTask();

                if (!budgetMap.containsKey(task)) {
                    double[] budgetNumbers = {0.0, 0.0, 0.0};
                    budgetMap.put(task, budgetNumbers);
                }
                double[] doubles = budgetMap.get(task);
                double budgetHours = (budget.getBudget() / rate);
                if((budget.getMonth() - (localDateStart.getMonthOfYear() - 1))>3) continue;
                log.info("Month ("+(budget.getMonth() - (localDateStart.getMonthOfYear() - 1))+") " +
                        "now has "+budgetHours+" budget hours.");
                doubles[budget.getMonth() - (localDateStart.getMonthOfYear() - 1)] = budgetHours;

            }
            */

            List<Attachment> attachments = new ArrayList<>();
            for (Task task : budgetMap.keySet()) {
                log.info("*** creating attachments ***");
                Attachment attachment = new Attachment();
                attachment.setTitle(task.getProject().getName());
                attachment.setText(task.getName());
                if(task.getProject().getOwner()!=null) attachment.setFooter("Project lead: "+task.getProject().getOwner().getUsername());
                attachment.setColor("#fbb14d");
                attachments.add(attachment);

                for (int i = 0; i < budgetMap.get(task).length; i++) {
                    log.info("Budget for "+localDateStart.plusMonths(i).monthOfYear().getAsText());
                    log.info((Math.round(budgetMap.get(task)[i]*100.0)/100.0)+" hours");
                    attachment.addField(new Field("Budget for "+localDateStart.plusMonths(i).monthOfYear().getAsText(), (Math.round(budgetMap.get(task)[i]*100.0)/100.0)+" hours", true));
                }
            }
            ChatPostMessageMethod textMessage = new ChatPostMessageMethod(user.getSlackusername(), message);
            textMessage.setAs_user(true);
            textMessage.setAttachments(attachments);
            //if(user.getUsername().equalsIgnoreCase("hans.lassen")) {
                log.info("Sending message to "+user.getUsername());
                motherWebApiClient.postMessage(textMessage);
            //}

            int[] userCapacities = capacitypermonthbyuser(user.getUuid(), localDateStart, localDateEnd);

            log.info("*** creating totalbudget month ***");
            double[] totalBudgetMonth = new double[3];
            for (double[] doubles : budgetMap.values()) {
                for (int i = 0; i < 3; i++) {
                    log.info("month "+(i+1));
                    log.info("adding "+doubles[i]);
                    totalBudgetMonth[i] += doubles[i];
                    log.info("now totalBudgetMonth for month ("+(i+1)+") has " + totalBudgetMonth[i]);
                }
            }

            log.info("*** creating allocation percent ***");
            long[] allocationPercent = new long[3];
            for (int j = 0; j < 3; j++) {
                log.info("month "+(j+1));
                if(totalBudgetMonth[j] <= 0) {
                    log.info("allocation is 0");
                    allocationPercent[j] = 0;
                    continue;
                }
                log.info(user.getUsername()+": "+totalBudgetMonth[j]+", "+userCapacities[j]+", "+businessDaysInMonth[j]);
                allocationPercent[j] = Math.round((totalBudgetMonth[j] / ((userCapacities[j] / 5) * businessDaysInMonth[j])) * 100);
                log.info("allocation is "+allocationPercent[j]);
            }

            String concludingMessage = "";

            concludingMessage += "If this seems ok, do nothing. If this seems wrong, please contact your project leads and tell them to fix it!";

            //textMessage = new ChatPostMessageMethod("@"+slackUser.getName(), concludingMessage);
            textMessage = new ChatPostMessageMethod(user.getSlackusername(), concludingMessage);
            textMessage.setAs_user(true);

            //if(user.getUsername().equalsIgnoreCase("hans.lassen")) {
                log.info("Sending concluding message "+user.getUsername());
                motherWebApiClient.postMessage(textMessage);
            //}


            ChatPostMessageMethod textMessage2 = new ChatPostMessageMethod("@hans", "User "+user.getUsername()+" has "+allocationPercent[0]+"% and "+allocationPercent[1]+"% and "+allocationPercent[2]+"% allocation.");
            textMessage2.setAs_user(true);
            log.info("Sending message to admin");
            motherWebApiClient.postMessage(textMessage2);
/*
            if(allocationPercentMonthOne < 75.0 || allocationPercentMonthOne > 100.0 || allocationPercentMonthTwo < 75.0 || allocationPercentMonthTwo > 100.0) {
                ChatPostMessageMethod textMessage3 = new ChatPostMessageMethod("@tobias_kjoelsen", "User " + user.username + " has " + allocationPercentMonthOne + "% and " + allocationPercentMonthTwo + "% allocation.");
                textMessage3.setAs_user(true);
                System.out.println("Sending message");
                halWebApiClient.postMessage(textMessage3);

                ChatPostMessageMethod textMessage4 = new ChatPostMessageMethod("@peter", "User " + user.username + " has " + allocationPercentMonthOne + "% and " + allocationPercentMonthTwo + "% allocation.");
                textMessage4.setAs_user(true);
                System.out.println("Sending message");
                halWebApiClient.postMessage(textMessage4);

                ChatPostMessageMethod textMessage5 = new ChatPostMessageMethod("@thomasgammelvind", "User " + user.username + " has " + allocationPercentMonthOne + "% and " + allocationPercentMonthTwo + "% allocation.");
                textMessage5.setAs_user(true);
                System.out.println("Sending message");
                halWebApiClient.postMessage(textMessage5);
            }
            */
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }
/*
    public void execute() {

        int businessDaysInNextMonth = getWorkingDaysBetweenTwoDates(startDate, endDate);
        System.out.println("businessDaysInMonth = " + businessDaysInNextMonth);

        int businessDaysInNextNextMonth = getWorkingDaysBetweenTwoDates(LocalDate.now().plusMonths(2).withDayOfMonth(1).minusDays(1).toDate(), LocalDate.now().plusMonths(3).withDayOfMonth(1).minusDays(1).toDate());
        System.out.println("businessDaysInMonth = " + businessDaysInNextNextMonth);

        for (User user : restClient.getUsers()) {

            List<Capacity> userCapacities = restClient.getUserCapacities(user.uuid, LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(1).plusMonths(2));
            System.out.println("userCapacities.get(0).capacity = " + userCapacities.get(0).capacity);
            System.out.println("userCapacities.get(1).capacity = " + userCapacities.get(1).capacity);
            System.out.println("businessDaysInNextMonth = " + businessDaysInNextMonth);
            System.out.println("totalBudgetMonthOne = " + totalBudgetMonthOne);


            long allocationPercentMonthOne = Math.round((totalBudgetMonthOne / ((userCapacities.get(0).capacity / 5) * businessDaysInNextMonth)) * 100);
            long allocationPercentMonthTwo = Math.round((totalBudgetMonthTwo / ((userCapacities.get(1).capacity / 5) * businessDaysInNextNextMonth)) * 100);
            String concludingMessage = "";
            //String concludingMessage += "This means you have a *"+allocationPercent+"%* allocation this coming month\n\n";

            concludingMessage += "If this seems ok, do nothing. If this seems wrong, please contact your project leads and tell them to fix it!";

            //textMessage = new ChatPostMessageMethod("@"+slackUser.getName(), concludingMessage);
            textMessage = new ChatPostMessageMethod(user.slackusername, concludingMessage);
            textMessage.setAs_user(true);
            System.out.println("Sending concluding message");
            //halWebApiClient.postMessage(textMessage);

            ChatPostMessageMethod textMessage2 = new ChatPostMessageMethod("@hans", "User "+user.username+" has "+allocationPercentMonthOne+"% and "+allocationPercentMonthTwo+"% allocation.");
            textMessage2.setAs_user(true);
            System.out.println("Sending message");
            halWebApiClient.postMessage(textMessage2);

            if(allocationPercentMonthOne < 75.0 || allocationPercentMonthOne > 100.0 || allocationPercentMonthTwo < 75.0 || allocationPercentMonthTwo > 100.0) {
                ChatPostMessageMethod textMessage3 = new ChatPostMessageMethod("@tobias_kjoelsen", "User " + user.username + " has " + allocationPercentMonthOne + "% and " + allocationPercentMonthTwo + "% allocation.");
                textMessage3.setAs_user(true);
                System.out.println("Sending message");
                halWebApiClient.postMessage(textMessage3);

                ChatPostMessageMethod textMessage4 = new ChatPostMessageMethod("@peter", "User " + user.username + " has " + allocationPercentMonthOne + "% and " + allocationPercentMonthTwo + "% allocation.");
                textMessage4.setAs_user(true);
                System.out.println("Sending message");
                halWebApiClient.postMessage(textMessage4);

                ChatPostMessageMethod textMessage5 = new ChatPostMessageMethod("@thomasgammelvind", "User " + user.username + " has " + allocationPercentMonthOne + "% and " + allocationPercentMonthTwo + "% allocation.");
                textMessage5.setAs_user(true);
                System.out.println("Sending message");
                halWebApiClient.postMessage(textMessage5);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
*/

    public int[] capacitypermonthbyuser(String userUUID, LocalDate periodStart, LocalDate periodEnd) {
        int[] capacities = new int[3];

        LocalDate currentDate = periodStart;
        int i = 0;
        while(currentDate.isBefore(periodEnd)) {
            int capacityByMonth = userRepository.calculateCapacityByMonthByUser(userUUID, currentDate.toString("yyyy-MM-dd"));
            log.info("capacityByMonth for "+currentDate.toString("yyyy-MM-dd")+" is " + capacityByMonth);
            capacities[i++] = capacityByMonth;
            currentDate = currentDate.plusMonths(1);
        }
        return capacities;
    }

    public static int getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
        System.out.println("CheckTimeRegistrationJob.getWorkingDaysBetweenTwoDates");
        System.out.println("startDate = [" + startDate + "], endDate = [" + endDate + "]");
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        int workDays = 0;

        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 0;
        }

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {
            //excluding start date
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

        return workDays;
    }
}
