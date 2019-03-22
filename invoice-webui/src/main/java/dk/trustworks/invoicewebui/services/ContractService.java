package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.exceptions.ContractValidationException;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.TaskType;
import dk.trustworks.invoicewebui.repositories.ClientRepository;
import dk.trustworks.invoicewebui.repositories.ContractRepository;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.model.LocalDatePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    private final ClientRepository clientRepository;

    private final WorkService workService;

    @Autowired
    public ContractService(ProjectService projectService, ContractRepository contractRepository, ClientRepository clientRepository, WorkService workService) {
        this.projectService = projectService;
        this.contractRepository = contractRepository;
        this.clientRepository = clientRepository;
        this.workService = workService;
    }

    @Transactional
    @CacheEvict(value = {"contract", "rate"}, allEntries = true)
    public Contract createContract(Contract contract) {
        Contract savedContract = contractRepository.save(contract);
        Client client = savedContract.getClient();
        client.setActive(true);
        clientRepository.save(client);
        return contract;
    }

    @Transactional
    @CacheEvict(value = {"contract", "rate"}, allEntries = true)
    public Contract updateContract(Contract contract) {
        return contractRepository.save(contract);
    }

    @Transactional
    public Contract reloadContract(Contract contract) {
        return contractRepository.findOne(contract.getUuid());
    }

    @Transactional
    @CacheEvict(value = {"contract", "rate"}, allEntries = true)
    public Contract addProjects(Contract Contract, Set<Project> projects) throws ContractValidationException {
        // validate
        for (Project project : projects) {
            for (Contract contract : project.getContracts()) {
                Set<Object> userUUIDs = contract.getContractConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet());
                if(isOverlapping(contract.getActiveFrom(), contract.getActiveTo(), Contract.getActiveFrom(), Contract.getActiveTo()) &&
                        userUUIDs.contains(Contract.getContractConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet())))
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
    @CacheEvict(value = {"contract", "rate"}, allEntries = true)
    public Contract addProject(Contract Contract, Project project) throws ContractValidationException {
        // validate
        for (Contract contract : project.getContracts()) {
            Set<Object> userUUIDs = contract.getContractConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet());
            if(isOverlapping(contract.getActiveFrom(), contract.getActiveTo(), Contract.getActiveFrom(), Contract.getActiveTo()) &&
                    userUUIDs.contains(Contract.getContractConsultants().stream().map(c -> c.getUser().getUuid()).collect(Collectors.toSet())))
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

    public Double findConsultantRateByWork(Work work, ContractStatus... statusList) {
        if(work.getTask().getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        if(work.getTask().getType().equals(TaskType.SO)) return 0.0;
        if(work.getWorkas()==null) {
            return contractRepository.findConsultantRateByWork(DateUtils.getFirstDayOfMonth(work.getRegistered()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), work.getUser().getUuid(), work.getTask().getUuid(), Arrays.stream(statusList).map(Enum::name).toArray(String[]::new));
        } else {
            return contractRepository.findConsultantRateByWork(DateUtils.getFirstDayOfMonth(work.getRegistered()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), work.getWorkas().getUuid(), work.getTask().getUuid(), Arrays.stream(statusList).map(Enum::name).toArray(String[]::new));
        }
    }

    public Contract findContractByWork(Work work, ContractStatus... statusList) {
        if(work.getTask().getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return null;
        if(work.getWorkas()==null) {
            return contractRepository.findContractByWork(DateUtils.getFirstDayOfMonth(work.getRegistered()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), work.getUser().getUuid(), work.getTask().getUuid(), Arrays.stream(statusList).map(Enum::name).collect(Collectors.toList()));
        } else {
            return contractRepository.findContractByWork(DateUtils.getFirstDayOfMonth(work.getRegistered()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), work.getWorkas().getUuid(), work.getTask().getUuid(), Arrays.stream(statusList).map(Enum::name).collect(Collectors.toList()));
        }
    }

    public Double findConsultantRate(int year, int month, int day, User user, Task task, ContractStatus... statusList) {
        if(task.getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        if(task.getType().equals(TaskType.SO)) return 0.0;
        return contractRepository.findConsultantRateByWork(year + "-" + month + "-" + day, user.getUuid(), task.getUuid(), Arrays.stream(statusList).map(Enum::name).toArray(String[]::new));
    }

    @Cacheable("contract")
    public List<Contract> findActiveContractsByDate(LocalDate activeDate, ContractStatus... statusList) {
        return contractRepository.findByActiveFromLessThanEqualAndActiveToGreaterThanEqualAndStatusIn(activeDate, activeDate, statusList);
        //return contractRepository.findByActiveFromBeforeAndActiveToAfterAndStatusIn(activeDate, activeDate, statusList);
    }

    public List<Contract> findActiveContractsByPeriod(LocalDate activeFrom, LocalDate activeTo, ContractStatus... statusList) {
        return contractRepository.findByActiveFromBeforeAndActiveToAfterAndStatusIn(activeTo, activeFrom, statusList);
    }

    private static boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    @Transactional
    @CacheEvict(value = {"contract", "rate"}, allEntries = true)
    public Contract removeProject(Contract contract, Project project) {
        contract = contractRepository.findOne(contract.getUuid());
        contract.getProjects().remove(project);
        return updateContract(contract);
    }

    public Map<String, Work> getWorkErrors(LocalDate errorDate, int months) {
        Map<String, Work> errors = new HashMap<>();
        for (Work work : workService.findByPeriod(errorDate.minusMonths(months), errorDate)) {
            if(!(work.getWorkduration()>0)) continue;
            if(work.getTask().getType().equals(TaskType.SO)) continue;
            if(findConsultantRateByWork(work, ContractStatus.values())==null)
                errors.put(work.getUser().getUuid()+work.getTask().getProject().getUuid(), work);
        }
        return errors;
    }

    public Map<String, Work> getWorkErrors(LocalDate errorDate, User user, int months) {
        Map<String, Work> errors = new HashMap<>();
        for (Work work : workService.findByPeriodAndUserUUID(errorDate.minusMonths(months), errorDate, user.getUuid())) {
            if(!(work.getWorkduration()>0)) continue;
            if(findConsultantRateByWork(work)==null)
                errors.put(work.getUser().getUuid()+work.getTask().getProject().getUuid(), work);
        }
        return errors;
    }

    public Set<User> getEmployeesWorkingOnProjectWithNoContract(Project project) {
        Set<User> users = new HashSet<>();
        if(project.getTasks().size() == 0) return users;
        for (Work work : workService.findByTasks(project.getTasks())) {
            if(!(work.getWorkduration()>0)) continue;
            if(findConsultantRateByWork(work, ContractStatus.values())==null)
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
            List<Work> workList = workService.findByUserAndTasks(user.getUuid(), project.getTasks());
            for (Work work : workList) {
                if(!(work.getWorkduration()>0)) continue;
                if(findConsultantRateByWork(work, ContractStatus.values())==null) {
                    projectsResult.add(work.getTask().getProject());
                }
            }
        }
        return projectsResult;
    }

    public LocalDatePeriod getUsersFirstAndLastWorkOnProject(Project project, User user) {
        if(project.getTasks().size() == 0) return null;
        List<Work> workList = workService.findByUserAndTasks(user.getUuid(), project.getTasks());
        Optional<Work> workMin = workList.stream().min(Comparator.comparing(Work::getRegistered));
        Optional<Work> workMax = workList.stream().max(Comparator.comparing(Work::getRegistered));
        return workMin.map(work -> new LocalDatePeriod(
                work.getRegistered(),
                workMax.get().getRegistered()
        )).orElse(null);
    }

    public List<Work> getWorkOnContractByUser(Contract Contract) {
        return workService.findByProjectsAndUsersAndDateRange(
                Contract.getProjects().stream().map(Project::getUuid).collect(Collectors.toList()),
                Contract.getContractConsultants().stream().map(consultant -> consultant.getUser().getUuid()).collect(Collectors.toList()),
                Contract.getActiveFrom(),
                Contract.getActiveTo());
    }

    public List<Contract> findTimeActiveConsultantContracts(User user, LocalDate activeOn) {
        return contractRepository.findTimeActiveConsultantContracts(user.getUuid(), activeOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    public double findAmountUsedOnContract(Contract contract) {
        Double result = workService.findAmountUsedByContract(contract.getUuid());
        return result==null?0.0:result;
    }

    public Collection<String> createErrorList(Map<String, Work> errors) {
        SortedMap<String, String> errorList = new TreeMap<>();
        for (Work work : errors.values().stream().filter(work -> work.getWorkduration()>0).sorted(Comparator.comparing(Work::getRegistered).reversed()).collect(Collectors.toList())) {
            String client = work.getTask().getProject().getClient().getName();
            String project = work.getTask().getProject().getName();
            String username = work.getUser().getUsername();
            String error = "There is no valid contract for " + username +
                    " work on " + client +
                    "'s project " + project +
                    " on date " + (work.getRegistered().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            errorList.put(client+project+username, error);
        }
        return errorList.values();
    }

    @Transactional
    @CacheEvict(value = {"contract", "rate"}, allEntries = true)
    public void deleteContract(Contract contract) {
        try {
            contractRepository.delete(contract.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Iterable<Contract> findAll() {
        return contractRepository.findAll();
    }

    public Contract findOne(String contractuuid) {
        return contractRepository.findOne(contractuuid);
    }
}