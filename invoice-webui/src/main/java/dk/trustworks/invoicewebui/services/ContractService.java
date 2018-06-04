package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.exceptions.ContractValidationException;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.ContractRepository;
import dk.trustworks.invoicewebui.repositories.MainContractRepository;
import dk.trustworks.invoicewebui.repositories.SubContractRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.web.model.LocalDatePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContractService {

    private final ProjectService projectService;

    private final MainContractRepository mainContractRepository;

    private final SubContractRepository subContractRepository;

    private final WorkRepository workRepository;

    private final ContractRepository contractRepository;

    @Autowired
    public ContractService(ProjectService projectService, MainContractRepository mainContractRepository, SubContractRepository subContractRepository, WorkRepository workRepository, ContractRepository contractRepository) {
        this.projectService = projectService;
        this.mainContractRepository = mainContractRepository;
        this.subContractRepository = subContractRepository;
        this.workRepository = workRepository;
        this.contractRepository = contractRepository;
    }

    @Transactional
    public MainContract createContract(MainContract mainContract) {
        return mainContractRepository.save(mainContract);
    }

    @Transactional
    public MainContract updateContract(MainContract contract) {
        return mainContractRepository.save(contract);
    }

    @Transactional
    public MainContract reloadMainContract(MainContract mainContract) {
        return mainContractRepository.findOne(mainContract.getUuid());
    }

    @Transactional
    public SubContract updateContract(SubContract contract) {
        return subContractRepository.save(contract);
    }

    @Transactional
    public MainContract addProjects(MainContract mainContract, Set<Project> projects) throws ContractValidationException {
        // validate
        for (Project project : projects) {
            for (MainContract contract : project.getMainContracts()) {
                Set<Object> userUUIDs = contract.getConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet());
                if(isOverlapping(contract.getActiveFrom(), contract.getActiveTo(), mainContract.getActiveFrom(), mainContract.getActiveTo()) &&
                        userUUIDs.contains(mainContract.getConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet())))
                    throw new ContractValidationException("Overlapping another contract with same consultants");
            }
        }

        // execute
        for (Project project : projects) {
            project.addMainContract(mainContract);
            projectService.save(project);
        }
        mainContract.addProjects(projects);
        mainContractRepository.save(mainContract);
        return mainContract;
    }

    @Transactional
    public MainContract addProject(MainContract mainContract, Project project) throws ContractValidationException {
        System.out.println("ContractService.addProject");
        System.out.println("mainContract = [" + mainContract + "], project = [" + project + "]");
        // validate
        for (MainContract contract : project.getMainContracts()) {
            Set<Object> userUUIDs = contract.getConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet());
            if(isOverlapping(contract.getActiveFrom(), contract.getActiveTo(), mainContract.getActiveFrom(), mainContract.getActiveTo()) &&
                    userUUIDs.contains(mainContract.getConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet())))
                throw new ContractValidationException("Overlapping another contract with same consultants");
        }

        // execute
        mainContract = mainContractRepository.findOne(mainContract.getUuid());
        project = projectService.findOne(project.getUuid());
        project.addMainContract(mainContract);
        //project = projectRepository.save(project);
        mainContract.addProject(project);
        mainContractRepository.save(mainContract);
        return mainContract;
    }

    public Double findConsultantRateByWork(Work work) {
        if(work.getTask().getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        Double consultantRateByWork = contractRepository.findConsultantRateByWork(work.getYear() + "-" + (work.getMonth() + 1) + "-" + work.getDay(), work.getUser().getUuid(), work.getTask().getUuid());
        if(consultantRateByWork==null) {
            System.out.println("work = " + work);
            System.out.println("work.getTask().getProject() = " + work.getTask().getProject());
        }
        return consultantRateByWork;
    }

    public Double findConsultantRate(int year, int month, int day, User user, Task task) {
        System.out.println("ContractService.findConsultantRate");
        System.out.println("year = [" + year + "], month = [" + month + "], day = [" + day + "], user = [" + user.getUuid() + "], task = [" + task.getUuid() + "]");
        if(task.getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        return contractRepository.findConsultantRateByWork(year + "-" + month + "-" + day, user.getUuid(), task.getUuid());
    }

    public List<MainContract> findActiveMainContractsByDate(LocalDate activeDate) {
        return mainContractRepository.findByActiveFromBeforeAndActiveToAfter(activeDate, activeDate);
    }

    public List<MainContract> findActiveMainContractsByPeriod(LocalDate activeFrom, LocalDate activeTo) {
        return mainContractRepository.findByActiveFromBeforeAndActiveToAfter(activeTo, activeFrom);
    }

    private static boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    @Transactional
    public MainContract removeProject(MainContract mainContract, Project project) {
        mainContract = mainContractRepository.findOne(mainContract.getUuid());
        mainContract.getProjects().remove(project);
        return updateContract(mainContract);
    }

    public Map<String, Work> getWorkErrors(LocalDate errorDate, int months) {
        Map<String, Work> errors = new HashMap<>();
        for (Work work : workRepository.findByPeriod(
                errorDate.minusMonths(months).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                errorDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
            if(!(work.getWorkduration()>0)) continue;
            if(findConsultantRateByWork(work)==null)
                errors.put(work.getUser().getUuid()+work.getTask().getProject().getUuid(), work);
        }
        return errors;
    }

    public Map<String, Work> getWorkErrors(LocalDate errorDate, User user, int months) {
        Map<String, Work> errors = new HashMap<>();
        for (Work work : workRepository.findByPeriodAndUserUUID(
                errorDate.minusMonths(months).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                errorDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                user.getUuid())) {
            if(!(work.getWorkduration()>0)) continue;
            if(findConsultantRateByWork(work)==null)
                errors.put(work.getUser().getUuid()+work.getTask().getProject().getUuid(), work);
        }
        return errors;
    }

    public Set<User> getEmployeesWorkingOnProjectWithNoContract(Project project) {
        Set<User> users = new HashSet<>();
        if(project.getTasks().size() == 0) return users;
        List<String> strings = project.getTasks().stream().map(Task::getUuid).collect(Collectors.toList());
        //System.out.println(String.join(", ", strings));
        for (Work work : workRepository.findByTasks(strings)) {
            if(!(work.getWorkduration()>0)) continue;
            if(findConsultantRateByWork(work)==null)
                users.add(work.getUser());
        }
        return users;
    }

    public Set<Project> getClientProjectsNotUnderContract(Client client) {
        Set<Project> projects = new HashSet<>();
        for (Project project : client.getProjects()) {
            if(project.getMainContracts().size()==0) projects.add(project);
        }
        return projects;
    }

    public Set<Project> getProjectsWithUserWorkButNoContract(List<Project> projects, User user) {
        Set<Project> projectsResult = new HashSet<>();
        for (Project project : projects) {
            if(project.getTasks().size() == 0) continue;
            List<String> strings = project.getTasks().stream().map(Task::getUuid).collect(Collectors.toList());
            //System.out.println("tasks = ["+String.join(", ", strings)+"] | user = "+user.getUuid());
            List<Work> workList = workRepository.findByTasksAndUser(strings, user.getUuid());
            for (Work work : workList) {
                if(!(work.getWorkduration()>0)) continue;
                if(findConsultantRateByWork(work)==null) {
                    projectsResult.add(work.getTask().getProject());
                }
            }
        }
        return projectsResult;
    }

    public LocalDatePeriod getUsersFirstAndLastWorkOnProject(Project project, User user) {
        System.out.println("ContractService.getUsersFirstAndLastWorkOnProject");
        System.out.println("project = [" + project + "], user = [" + user + "]");
        if(project.getTasks().size() == 0) return null;
        List<String> strings = project.getTasks().stream().map(Task::getUuid).collect(Collectors.toList());
        List<Work> workList = workRepository.findByTasksAndUser(strings, user.getUuid());
        Optional<Work> workMin = workList.stream().min(Comparator.comparing(o -> LocalDate.of(o.getYear(), o.getMonth()+1, o.getDay())));
        Optional<Work> workMax = workList.stream().max(Comparator.comparing(o -> LocalDate.of(o.getYear(), o.getMonth()+1, o.getDay())));
        return workMin.map(work -> new LocalDatePeriod(
                LocalDate.of(work.getYear(), work.getMonth() + 1, work.getDay()),
                LocalDate.of(workMax.get().getYear(), workMax.get().getMonth() + 1, workMax.get().getDay())
        )).orElse(null);
    }

    public List<Work> getWorkOnContractByUser(MainContract mainContract) {
        System.out.println("ContractService.getWorkOnContractByUser");
        System.out.println("mainContract = [" + mainContract + "]");
        return workRepository.findByProjectsAndUsersAndDateRange(
                mainContract.getProjects().stream().map(Project::getUuid).collect(Collectors.toList()),
                mainContract.getConsultants().stream().map(consultant -> consultant.getUser().getUuid()).collect(Collectors.toList()),
                mainContract.getActiveFrom().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                mainContract.getActiveTo().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    public List<MainContract> findTimeActiveConsultantContracts(User user, LocalDate activeOn) {
        return mainContractRepository.findTimeActiveConsultantContracts(user.getUuid(), activeOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    public Collection<String> createErrorList(Map<String, Work> errors) {
        SortedMap<String, String> errorList = new TreeMap<>();
        for (Work work : errors.values().stream().filter(work -> work.getWorkduration()>0).sorted(Comparator.comparing(Work::getYear).thenComparing(Work::getMonth).reversed()).collect(Collectors.toList())) {
            String client = work.getTask().getProject().getClient().getName();
            String project = work.getTask().getProject().getName();
            String username = work.getUser().getUsername();
            String error = "There is no valid contract for " + username +
                    " work on " + client +
                    "'s project " + project +
                    " on date " + (work.getMonth() + 1) + "/" + work.getYear();
            errorList.put(client+project+username, error);
        }
        return errorList.values();
    }

    @Transactional
    public void deleteContract(MainContract mainContract) {
        System.out.println("ContractService.deleteContract");
        System.out.println("mainContract = [" + mainContract + "]");
        try {
            mainContractRepository.delete(mainContract.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}