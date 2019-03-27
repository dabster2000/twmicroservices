package dk.trustworks.invoicewebui.jobs;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Attachment;
import allbegray.slack.type.Field;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.UserBooking;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.services.WorkService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Component
public class CheckBudgetJob {

    static final Logger log = Logger.getLogger(CheckBudgetJob.class.getName());

    private final UserService userService;

    private final ClientRepository clientRepository;

    private final WorkService workService;

    private final StatisticsService statisticsService;

    @Value("${motherSlackBotToken}")
    private String motherSlackToken;

    @Autowired
    public CheckBudgetJob(UserService userService, ClientRepository clientRepository, WorkService workService, StatisticsService statisticsService) {
        this.userService = userService;
        this.clientRepository = clientRepository;
        this.workService = workService;
        this.statisticsService = statisticsService;
    }

    @PostConstruct
    public void startup() {
        //checkBudgetJob();
    }

    //@Scheduled(cron = "0 0 0 1 1/1 *")
    @Scheduled(cron = "0 30 10 8,18 * ?")
    public void checkBudgetJob() {
        SlackWebApiClient motherWebApiClient = SlackClientFactory.createWebApiClient(motherSlackToken);
        log.info("CheckBudgetJob.execute");

        LocalDate localDateStart = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        log.info("localDateStart = " + localDateStart);
        LocalDate localDateEnd = localDateStart.plusMonths(3);
        log.info("localDateEnd = " + localDateEnd);

        List<UserBooking> bookingList = statisticsService.getUserBooking(-1, 4);

        List<User> activeUsers = userService.findCurrentlyWorkingEmployees(ConsultantType.CONSULTANT);

        for (User user : activeUsers) {
            log.info("--- " + user + " ---");
            //if(!user.getUsername().equalsIgnoreCase("hans.lassen")) continue;

            int[] businessDaysInMonth = new int[3];

            for (int i = 0; i < 3; i++) {
                businessDaysInMonth[i] = workService.getWorkDaysInMonth(user.getUuid(), localDateStart.plusMonths(i));//DateUtils.countWeekDays(DateUtils.getFirstDayOfMonth(localDateStart.plusMonths(i)).minusDays(1), DateUtils.getLastDayOfMonth(localDateStart.plusMonths(i)));
            }


            String message = "*Here is a quick summary of your project allocation for the next three months*\n\n" +
                    "According to my calculations there is "+businessDaysInMonth[0]+" work days in "+localDateStart.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())+", " +
                    ""+businessDaysInMonth[1]+" days in "+localDateStart.plusMonths(1).getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())+", " +
                    "and "+businessDaysInMonth[2]+" in "+localDateStart.plusMonths(2).getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())+".\n\n";

            List<UserBooking> userBookingList = bookingList.stream().filter(userBooking -> userBooking.getUsername().equals(user.getUsername())).flatMap(userBooking -> userBooking.getSubProjects().stream()).collect(Collectors.toList());

            if(userBookingList.size() > 0 && userBookingList.stream().mapToDouble(userBooking -> Arrays.stream(userBooking.getAmountItemsPerProjects()).sum()).sum()>0) {
                message += "You have the following allocation in the next three months" + ":\n";
            } else {
                message += "You are not assigned to projects the upcoming 3 months.\n";
            }

            List<Attachment> attachments = new ArrayList<>();
            double[] totalBudgetMonth = new double[3];
            for (UserBooking userBooking : userBookingList) {
                log.info("*** creating attachments ***");
                if(!(Arrays.stream(userBooking.getAmountItemsPerProjects()).sum()>0)) continue;
                Attachment attachment = new Attachment();
                attachment.setTitle(clientRepository.findOne(userBooking.getUuid()).getName());
                attachment.setText(userBooking.getUsername());
                //if(task.getProject().getOwner()!=null) attachment.setFooter("Project lead: "+task.getProject().getOwner().getUsername());
                attachment.setColor("#fbb14d");
                attachments.add(attachment);



                for (int i = 0; i < 3; i++) {
                    attachment.addField(new Field("Budget for "+localDateStart.plusMonths(i).getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()), (NumberUtils.round(userBooking.getAmountItemsPerProjects(i), 2)+" hours"), true));
                    totalBudgetMonth[i] += userBooking.getAmountItemsPerProjects(i);
                }
            }

            ChatPostMessageMethod textMessage = new ChatPostMessageMethod(user.getSlackusername(), message);
            //ChatPostMessageMethod textMessage = new ChatPostMessageMethod(userService.findByUsername("hans.lassen").getSlackusername(), message);
            textMessage.setAs_user(true);
            textMessage.setAttachments(attachments);
            log.info("Sending message to "+user.getUsername());
            motherWebApiClient.postMessage(textMessage);


            int[] userCapacities = capacitypermonthbyuser(user.getUuid(), localDateStart, localDateEnd);

            log.info("*** creating allocation percent ***");
            long[] allocationPercent = new long[3];
            for (int j = 0; j < 3; j++) {
                log.info("month "+(j+1));
                if(totalBudgetMonth[j] <= 0) {
                    log.info("allocation is 0");
                    continue;
                }
                log.info(user.getUsername()+": "+totalBudgetMonth[j]+", "+userCapacities[j]+", "+businessDaysInMonth[j]);
                allocationPercent[j] = Math.round((totalBudgetMonth[j] / (((userCapacities[j]-2) / 5.0) * businessDaysInMonth[j])) * 100.0);
                log.info("allocation is "+allocationPercent[j]);
            }

            StringBuilder concludingMessage = new StringBuilder();

            concludingMessage.append("*Your total allocation is:* \n");
            for (int i = 0; i < 3; i++) {
                concludingMessage.append(localDateStart.plusMonths(i).getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())).append(": ").append(allocationPercent[i]).append("%\n");
            }

            concludingMessage.append("\nIf this seems ok, do nothing. If this seems incorrect due to vacation or similar, please contact your customer account manager and tell them to fix it!");

            //textMessage = new ChatPostMessageMethod(userService.findByUsername("hans.lassen").getSlackusername(), concludingMessage.toString());
            textMessage = new ChatPostMessageMethod(user.getSlackusername(), concludingMessage.toString());
            textMessage.setAs_user(true);

            log.info("Sending concluding message "+user.getUsername());
            motherWebApiClient.postMessage(textMessage);

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

    public int[] capacitypermonthbyuser(String userUUID, LocalDate periodStart, LocalDate periodEnd) {
        int[] capacities = new int[3];

        LocalDate currentDate = periodStart;
        int i = 0;
        while(currentDate.isBefore(periodEnd)) {
            int capacityByMonth = userService.calculateCapacityByMonthByUser(userUUID, currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            log.info("capacityByMonth for "+ currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +" is " + capacityByMonth);
            capacities[i++] = capacityByMonth;
            currentDate = currentDate.plusMonths(1);
        }
        return capacities;
    }

}
