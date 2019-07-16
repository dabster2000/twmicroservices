package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;


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
        ArrayList<String> list = new ArrayList<>();
        list.add("f585f46f-19c1-4a3a-9ebd-1a4f21007282");
        List<Work> workList = workRepository.findByTasks(list);

        Set<String> dates = new TreeSet<>();
        Map<String, Map<String, Double>> userVacationByMonth = new HashMap<>();

        for (Work work : workList) {
            LocalDate month = DateUtils.getFirstDayOfMonth(work.getRegistered());
            dates.add(month.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            userVacationByMonth.putIfAbsent(work.getUser().getUuid(), new TreeMap<>());
            Map<String, Double> vacationByMonth = userVacationByMonth.get(work.getUser().getUuid());
            vacationByMonth.putIfAbsent(month.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 0.0);
            Double vacation = vacationByMonth.get(month.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            Double newVacation = vacation + work.getWorkduration();
            vacationByMonth.replace(month.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), newVacation);
        }
    }
}
