package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.bus.Event;
import reactor.bus.EventBus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

@Service
public class WorkService {

    private final EventBus eventBus;

    private final WorkRepository workRepository;

    private final TaskService taskService;

    private final ClientService clientService;

    @Autowired
    public WorkService(EventBus eventBus, WorkRepository workRepository, TaskService taskService, ClientService clientService) {
        this.eventBus = eventBus;
        this.workRepository = workRepository;
        this.taskService = taskService;
        this.clientService = clientService;
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
        int weekDays = DateUtils.getWeekdaysInPeriod(DateUtils.getFirstDayOfMonth(month), DateUtils.getFirstDayOfMonth(month).plusMonths(1));
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

    public List<Work> findMaternityLeaveByUser(User user) {
        return workRepository.findByUserAndTasks(user.getUuid(), "da2f89fc-9aef-4029-8ac2-7486be60e9b9");
    }

    public double countVacationByUser(User user) {
        return workRepository.countByUserAndTasks(user.getUuid(), "f585f46f-19c1-4a3a-9ebd-1a4f21007282");
    }

    //@Cacheable("work")
    public List<Work> findByPeriod(LocalDate fromDate, LocalDate toDate) {
        return workRepository.findByPeriod(fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    //@Cacheable("work")
    public List<Work> findByTasks(List<Task> tasks) {
        List<String> strings = tasks.stream().map(Task::getUuid).collect(Collectors.toList());
        return workRepository.findByTasks(strings);
    }

    public List<Work> findByTask(Task task) {
        return workRepository.findByTask(task.getUuid());
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

    public List<Work> getWorkOnContractByUser(Contract contract) {
        return findByProjectsAndUsersAndDateRange(
                contract.getProjectUuids(), //.getProjects().stream().map(Project::getUuid).collect(Collectors.toList()),
                contract.getContractConsultants().stream().map(consultant -> consultant.getUser().getUuid()).collect(Collectors.toList()),
                contract.getActiveFrom(),
                contract.getActiveTo());
    }

    //@Cacheable("work")
    public List<Work> findByProjectsAndUsersAndDateRange(Set<String> projects, List<String> users, LocalDate fromDate, LocalDate toDate) {
        List<Task> taskList = projects.stream().flatMap(s -> taskService.findByProject(s).stream()).collect(Collectors.toList());
        return users.stream().flatMap(s -> workRepository.findByPeriodAndUserAndTasks(stringIt(fromDate), stringIt(toDate), s, taskList.stream().map(Task::getUuid).toArray(String[]::new)).stream()).collect(Collectors.toList());
    }

    //@Cacheable("work")
    public Double findAmountUsedByContract(Contract contract) {
        List<String> useruuids = contract.getContractConsultants().stream().map(ContractConsultant::getUseruuid).collect(Collectors.toList());
        Set<String> tasks = new TreeSet<>();
        for (Project project : contract.getProjects()) {
            for (Task task : project.getTasks()) {
                tasks.add(task.getUuid());
            }
        }
        String[] taskArray = new String[tasks.size()];
        tasks.toArray(taskArray);
        double sum = 0.0;
        for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
             sum += workRepository.findByPeriodAndUserAndTasks(stringIt(contract.getActiveFrom()), stringIt(contract.getActiveTo()), contractConsultant.getUseruuid(), taskArray).stream().mapToDouble(value -> value.getWorkduration()*contractConsultant.getRate()).sum();
        }
        return sum;
    }

    public Double findHoursRegisteredOnContractByPeriod(Contract contract, String useruuid, LocalDate fromdate, LocalDate todate) {
        Set<String> tasks = new TreeSet<>();
        for (Project project : contract.getProjects()) {
            for (Task task : project.getTasks()) {
                tasks.add(task.getUuid());
            }
        }
        if(tasks.size()==0) return 0.0;
        String[] taskArray = new String[tasks.size()];
        tasks.toArray(taskArray);
        return workRepository.findByPeriodAndUserAndTasks(stringIt(fromdate), stringIt(todate), useruuid, taskArray).stream().mapToDouble(value -> value.getWorkduration()*contract.findByUseruuid(useruuid).getRate()).sum();

    }

    /*
    public UserWork getUserWorkHoursByPeriod(UserWork userWork) {
        workRepository.findBillableWorkByUserInPeriod(user.getUuid(), stringIt(fromDate), stringIt(toDate));

        return null;
    }
    */
/*
    public double countBillableWorkByUserInPeriod(String useruuid, String fromdate, String todate) {
        return workRepository.countBillableWorkByUserInPeriod(useruuid, fromdate, todate);
    }

 */

    public List<Work> findByActiveClients() {
        return workRepository.findByTasks(
                clientService.findByActiveTrue().stream()
                        .flatMap(client -> client.getProjects().stream())
                        .flatMap(project -> project.getTasks().stream())
                        .map(Task::getUuid)
                        .collect(Collectors.toList()));
    }

    @Transactional
    public Work save(Work work) {
        System.out.println("WorkService.create");
        System.out.println("work = [" + work + "]");
        Work savedWork = workRepository.save(work);
        eventBus.notify("notificationConsumer", Event.wrap(work));
        return savedWork;
    }

    @Transactional
    public Work saveWork(Work work) {
        System.out.println("WorkService.saveWork");
        System.out.println("work = [" + work + "]");
        Work existingWork = workRepository.findByRegisteredAndUseruuidAndTaskuuid(work.getRegistered(), work.getUseruuid(), work.getTask().getUuid());
        if(existingWork!=null) {
            existingWork.setWorkduration(work.getWorkduration());
            work = existingWork;
        }
        Work savedWork = workRepository.save(work);
        eventBus.notify("notificationConsumer", Event.wrap(work));
        return savedWork;
    }
/*
    public List<Work> findBillableWorkByUser(String uuid) {
        return workRepository.findBillableWorkByUser(uuid);
    }

 */


}
