package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.exceptions.ContractValidationException;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.repositories.ContractRepository;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.web.model.LocalDatePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContractService {

    private final ProjectService projectService;

    private final ContractRepository contractRepository;

    private final WorkRepository workRepository;

    @Autowired
    public ContractService(ProjectService projectService, ContractRepository contractRepository, WorkRepository workRepository) {
        this.projectService = projectService;
        this.contractRepository = contractRepository;
        this.workRepository = workRepository;
    }

    @Transactional
    @CacheEvict({"contract", "rate"})
    public Contract createContract(Contract contract) {
        return contractRepository.save(contract);
    }

    @Transactional
    @CacheEvict({"contract", "rate"})
    public Contract updateContract(Contract contract) {
        return contractRepository.save(contract);
    }

    @Transactional
    public Contract reloadContract(Contract contract) {
        return contractRepository.findOne(contract.getUuid());
    }

    @Transactional
    @CacheEvict({"contract", "rate"})
    public Contract addProjects(Contract Contract, Set<Project> projects) throws ContractValidationException {
        // validate
        for (Project project : projects) {
            for (Contract contract : project.getContracts()) {
                Set<Object> userUUIDs = contract.getConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet());
                if(isOverlapping(contract.getActiveFrom(), contract.getActiveTo(), Contract.getActiveFrom(), Contract.getActiveTo()) &&
                        userUUIDs.contains(Contract.getConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet())))
                    throw new ContractValidationException("Overlapping another contract with same consultants");
            }
        }

        // execute
        for (Project project : projects) {
            project.addContract(Contract);
            projectService.save(project);
        }
        Contract.addProjects(projects);
        contractRepository.save(Contract);
        return Contract;
    }

    @Transactional
    @CacheEvict({"contract", "rate"})
    public Contract addProject(Contract Contract, Project project) throws ContractValidationException {
        // validate
        for (Contract contract : project.getContracts()) {
            Set<Object> userUUIDs = contract.getConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet());
            if(isOverlapping(contract.getActiveFrom(), contract.getActiveTo(), Contract.getActiveFrom(), Contract.getActiveTo()) &&
                    userUUIDs.contains(Contract.getConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet())))
                throw new ContractValidationException("Overlapping another contract with same consultants");
        }

        // execute
        Contract = contractRepository.findOne(Contract.getUuid());
        project = projectService.findOne(project.getUuid());
        project.addContract(Contract);
        //project = projectRepository.save(project);
        Contract.addProject(project);
        contractRepository.save(Contract);
        return Contract;
    }

    public Double findConsultantRateByWork(Work work) {
        if(work.getTask().getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        return contractRepository.findConsultantRateByWork(work.getYear() + "-" + (work.getMonth() + 1) + "-01", work.getUser().getUuid(), work.getTask().getUuid());
    }

    public Contract findContractByWork(Work work) {
        if(work.getTask().getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return null;
        return contractRepository.findContractByWork(work.getYear() + "-" + (work.getMonth() + 1) + "-01", work.getUser().getUuid(), work.getTask().getUuid());
    }

    public Double findConsultantRate(int year, int month, int day, User user, Task task) {
        if(task.getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        return contractRepository.findConsultantRateByWork(year + "-" + month + "-" + day, user.getUuid(), task.getUuid());
    }

    public Optional<Consultant> findConsultant(int year, int month, User user, Task task) {
        Project project = task.getProject();
        Consultant consultantResult = null;
        List<Contract> Contracts = findActiveContractsByDate(LocalDate.of(year, month, 1)).stream().filter(Contract -> Contract.getProjects().contains(project)).collect(Collectors.toList());
        for (Contract Contract : Contracts) {
            for (Consultant consultant : Contract.getConsultants()) {
                if(consultant.getUser().getUuid().equals(user.getUuid())) consultantResult = consultant;
            }
        }
        return Optional.ofNullable(consultantResult);
    }

    public List<Contract> findActiveContractsByDate(LocalDate activeDate) {
        return contractRepository.findByActiveFromBeforeAndActiveToAfter(activeDate, activeDate);
    }

    public List<Contract> findActiveContractsByPeriod(LocalDate activeFrom, LocalDate activeTo) {
        return contractRepository.findByActiveFromBeforeAndActiveToAfter(activeTo, activeFrom);
    }

    private static boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    @Transactional
    public Contract removeProject(Contract contract, Project project) {
        contract = contractRepository.findOne(contract.getUuid());
        contract.getProjects().remove(project);
        return updateContract(contract);
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
            if(project.getContracts().size()==0) projects.add(project);
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

    public List<Work> getWorkOnContractByUser(Contract Contract) {
        System.out.println("ContractService.getWorkOnContractByUser");
        System.out.println("Contract = [" + Contract + "]");
        return workRepository.findByProjectsAndUsersAndDateRange(
                Contract.getProjects().stream().map(Project::getUuid).collect(Collectors.toList()),
                Contract.getConsultants().stream().map(consultant -> consultant.getUser().getUuid()).collect(Collectors.toList()),
                Contract.getActiveFrom().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                Contract.getActiveTo().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    public List<Contract> findTimeActiveConsultantContracts(User user, LocalDate activeOn) {
        return contractRepository.findTimeActiveConsultantContracts(user.getUuid(), activeOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
    public void deleteContract(Contract contract) {
        System.out.println("ContractService.deleteContract");
        System.out.println("Contract = [" + contract + "]");
        //if(contract.getParent()==null) {
            try {
                contractRepository.delete(contract.getUuid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        /*} else {
            try {
                contract = contractRepository.findOne(contract.getUuid());
                Contract parentContract = contractRepository.findOne(contract.getParent().getUuid());
                parentContract.getChildren().remove(contract);
                contractRepository.delete(contract.getUuid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        */
    }

    public Iterable<Contract> findAll() {
        return contractRepository.findAll();
    }

    public Contract findOne(String contractuuid) {
        return contractRepository.findOne(contractuuid);
    }
}