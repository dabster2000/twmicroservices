package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Component
public class GetVacationJob {

    static final Logger log = Logger.getLogger(GetVacationJob.class.getName());

    private final WorkRepository workRepository;
    private final UserService userService;

    @Autowired
    public GetVacationJob(WorkRepository workRepository, UserService userService) {
        this.workRepository = workRepository;
        this.userService = userService;
    }

    @PostConstruct
    public void startup() {
        checkBudgetJob();
    }

    public void checkBudgetJob() {
        System.out.println("******************************************************");
        System.out.println("******************************************************");
        System.out.println("******************************************************");
        ArrayList<String> list = new ArrayList<>();
        list.add("f585f46f-19c1-4a3a-9ebd-1a4f21007282");
        List<Work> workList = workRepository.findByTasks(list);

        Set<String> dates = new TreeSet<>();
        Map<String, Map<String, Double>> userVacationByMonth = new HashMap<>();

        for (Work work : workList) {
            LocalDate month = LocalDate.of(work.getYear(), work.getMonth() + 1, work.getDay()).withDayOfMonth(1);
            dates.add(month.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            userVacationByMonth.putIfAbsent(work.getUser().getUuid(), new TreeMap<>());
            Map<String, Double> vacationByMonth = userVacationByMonth.get(work.getUser().getUuid());
            vacationByMonth.putIfAbsent(month.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 0.0);
            Double vacation = vacationByMonth.get(month.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            Double newVacation = vacation + work.getWorkduration();
            vacationByMonth.replace(month.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), newVacation);
        }

        System.out.print("konsulent;");
        String collect = dates.stream().collect(Collectors.joining(";"));
        System.out.println(collect);

        for (String useruuid : userVacationByMonth.keySet()) {
            User user = userService.findByUUID(useruuid);
            System.out.print(user.getFirstname()+" "+user.getLastname()+";");

            Map<String, Double> vacationByMonth = userVacationByMonth.get(useruuid);
            int i = dates.size();
            for (String date : dates) {
                Double aDouble = vacationByMonth.get(date);
                if(aDouble!=null) System.out.print(aDouble);
                else System.out.print("0.0");
                if(i!=0) System.out.print(";");
                i=-1;
            }
            System.out.println();
        }
        System.out.println("******************************************************");
        System.out.println("******************************************************");
        System.out.println("******************************************************");

    }
}
