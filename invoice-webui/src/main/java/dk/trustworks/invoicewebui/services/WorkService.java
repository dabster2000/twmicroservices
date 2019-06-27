package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Task;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.bus.Event;
import reactor.bus.EventBus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

@Service
public class WorkService {

    private final EventBus eventBus;

    private final WorkRepository workRepository;

    @Autowired
    public WorkService(EventBus eventBus, WorkRepository workRepository) {
        this.eventBus = eventBus;
        this.workRepository = workRepository;
    }

    public List<Work> findByPeriodAndUserUUID(org.joda.time.LocalDate startOfWeek, org.joda.time.LocalDate endOfWeek, String userUUID) {
        return findByPeriodAndUserUUID(DateUtils.convertJodaToJavaDate(startOfWeek), DateUtils.convertJodaToJavaDate(endOfWeek), userUUID);
    }

    //@Cacheable("work")
    public List<Work> findByPeriodAndUserUUID(LocalDate fromdate, LocalDate todate, String useruuid) {
        return workRepository.findByPeriodAndUserUUID(
                fromdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                todate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                useruuid);
    }

    //@Cacheable("work")
    public int getWorkDaysInMonth(String userUUID, LocalDate month) {
        int weekDays = DateUtils.getWeekdaysInPeriod(DateUtils.getFirstDayOfMonth(month), DateUtils.getLastDayOfMonth(month));
        List<Work> workList = workRepository.findByPeriodAndUserAndTasks(DateUtils.getFirstDayOfMonth(month.getYear(), month.getMonthValue()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), DateUtils.getLastDayOfMonth(month.getYear(), month.getMonthValue()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), userUUID, "02bf71c5-f588-46cf-9695-5864020eb1c4", "f585f46f-19c1-4a3a-9ebd-1a4f21007282");
        double vacationAndSickdays = workList.stream().mapToDouble(Work::getWorkduration).sum() / 7.4;
        weekDays -= vacationAndSickdays;
        return weekDays;
    }

    //@Cacheable("work")
    public List<Work> findVacationByUser(User user) {
        return workRepository.findByUserAndTasks(user.getUuid(), "f585f46f-19c1-4a3a-9ebd-1a4f21007282");
    }

    public List<Work> findSicknessByUser(User user) {
        return workRepository.findByUserAndTasks(user.getUuid(), "02bf71c5-f588-46cf-9695-5864020eb1c4");
    }

    public double countVacationByUser(User user) {
        return workRepository.countByUserAndTasks(user.getUuid(), "f585f46f-19c1-4a3a-9ebd-1a4f21007282");
    }

    //@Cacheable("work")
    public List<Work> findByPeriod(LocalDate fromDate, LocalDate toDate) {
        return workRepository.findByPeriod(fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    //@Cacheable(value = "work")
    public List<Work> findBillableWorkByPeriod(LocalDate fromDate, LocalDate toDate) {
        return workRepository.findBillableWorkByPeriod(fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    //@Cacheable("work")
    public List<Work> findByTasks(List<Task> tasks) {
        List<String> strings = tasks.stream().map(Task::getUuid).collect(Collectors.toList());
        return workRepository.findByTasks(strings);
    }

    //@Cacheable("work")
    public List<Work> findByUserAndTasks(String userUUID, List<Task> tasks) {
        String[] strings = tasks.stream().map(Task::getUuid).toArray(String[]::new);
        return workRepository.findByUserAndTasks(userUUID, strings);
    }

    //@Cacheable(value = "work")
    public List<Work> findByYearAndMonth(int year, int month) {
        return workRepository.findByPeriod(DateUtils.getFirstDayOfMonth(year, month).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), DateUtils.getLastDayOfMonth(year, month).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    //@Cacheable(value = "work")
    public List<Work> findByYearAndMonthAndProject(int year, int month, String projectuuid) {
        return workRepository.findByPeriodAndProject(DateUtils.getFirstDayOfMonth(year, month).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), DateUtils.getLastDayOfMonth(year, month).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), projectuuid);
    }

    //@Cacheable("work")
    public List<Work> findByProjectsAndUsersAndDateRange(List<String> projects, List<String> users, LocalDate fromDate, LocalDate toDate) {
        return workRepository.findByProjectsAndUsersAndDateRange(projects, users, stringIt(fromDate), stringIt(toDate));
    }

    //@Cacheable("work")
    public Double findAmountUsedByContract(String contractUUID) {
        return workRepository.findAmountUsedByContract(contractUUID);
    }

    public Double findHoursRegisteredOnContractByPeriod(String contractUUID, String useruuid, LocalDate fromdate, LocalDate todate) {
        return workRepository.findHoursRegisteredOnContractByPeriod(contractUUID, useruuid, stringIt(fromdate), stringIt(todate));
    }

    /*
    public UserWork getUserWorkHoursByPeriod(UserWork userWork) {
        workRepository.findBillableWorkByUserInPeriod(user.getUuid(), stringIt(fromDate), stringIt(toDate));

        return null;
    }
    */

    public double countBillableWorkByUserInPeriod(String useruuid, String fromdate, String todate) {
        return workRepository.countBillableWorkByUserInPeriod(useruuid, fromdate, todate);
    }

    public List<Work> findByActiveClients() {
        return workRepository.findByActiveClients();
    }

    @Transactional
    public Work save(Work work) {
        System.out.println("WorkService.save");
        System.out.println("work = [" + work + "]");
        Work savedWork = workRepository.save(work);
        eventBus.notify("notificationConsumer", Event.wrap(work));
        return savedWork;
    }

    @Transactional
    public Work saveWork(Work work) {
        System.out.println("WorkService.saveWork");
        System.out.println("work = [" + work + "]");
        Work existingWork = workRepository.findByRegisteredAndUseruuidAndTask(work.getRegistered(), work.getUseruuid(), work.getTask());
        if(existingWork!=null) {
            existingWork.setWorkduration(work.getWorkduration());
            work = existingWork;
        }
        Work savedWork = workRepository.save(work);
        eventBus.notify("notificationConsumer", Event.wrap(work));
        return savedWork;
    }

    public List<Work> findBillableWorkByUser(String uuid) {
        return workRepository.findBillableWorkByUser(uuid);
    }
}
