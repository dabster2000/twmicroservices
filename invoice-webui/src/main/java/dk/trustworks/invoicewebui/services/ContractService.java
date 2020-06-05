package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.exceptions.ContractValidationException;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.TaskType;
import dk.trustworks.invoicewebui.repositories.ContractConsultantRepository;
import dk.trustworks.invoicewebui.repositories.ContractProjectRepository;
import dk.trustworks.invoicewebui.repositories.ContractRepository;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.web.model.LocalDatePeriod;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.utils.DateUtils.isBetween;
import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

@Service
public class ContractService implements InitializingBean {

    private static ContractService instance;

    private final ProjectService projectService;

    private final TaskService taskService;

    private final ContractRepository contractRepository;

    private final ContractProjectRepository contractProjectRepository;

    private final ContractConsultantRepository consultantRepository;

    private final ClientService clientService;

    private final WorkService workService;

    @Autowired
    public ContractService(ProjectService projectService, TaskService taskService, ContractRepository contractRepository, ContractProjectRepository contractProjectRepository, ContractConsultantRepository consultantRepository, ClientService clientService, WorkService workService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.contractRepository = contractRepository;
        this.contractProjectRepository = contractProjectRepository;
        this.consultantRepository = consultantRepository;
        this.clientService = clientService;
        this.workService = workService;
    }

    @Transactional
    @CacheEvict(value = {"contract", "rate"}, allEntries = true)
    public Contract createContract(Contract contract) throws ContractValidationException {
        if(!isValidContract(contract)) throw new ContractValidationException("Contract not valid");
        Contract savedContract = contractRepository.save(contract);
        Client client = savedContract.getClient();
        client.setActive(true);
        clientService.update(client);
        return contract;
    }

    private boolean isValidContract(Contract contract) {
        boolean isValid = true;
        for (Contract contractTest : contractRepository.findByClientuuid(contract.getClient().getUuid())) {
            boolean isOverlapped = false;
            if(contract.getUuid().equals(contractTest.getUuid())) continue;
            if((contract.getActiveFrom().isBefore(contractTest.getActiveTo()) || contract.getActiveFrom().isEqual(contractTest.getActiveTo())) &&
                    (contract.getActiveTo().isAfter(contractTest.getActiveFrom()) || contract.getActiveTo().isEqual(contractTest.getActiveFrom()))) {
                isOverlapped = true;
            }

            boolean hasProject = false;
            for (ContractProject contractProject : contract.getContractProjects()) {
                for (ContractProject contractTestProject : contractTest.getContractProjects()) {
                    if(contractProject.getProjectuuid().equals(contractTestProject.getProjectuuid())) {
                        hasProject = true;
                        break;
                    }
                }
                if(hasProject) break;
            }

            boolean hasConsultant = false;
            for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                for (ContractConsultant contractTestConsultant : contractTest.getContractConsultants()) {
                    if(contractConsultant.getUser().getUuid().equals(contractTestConsultant.getUser().getUuid())) {
                        hasConsultant = true;
                        break;
                    }
                }
                if(hasConsultant) break;
            }
            if(isOverlapped && hasProject && hasConsultant) isValid = false;
        }

        return isValid;
    }

    @Transactional
    @CacheEvict(value = {"contract", "rate"}, allEntries = true)
    public Contract updateContract(Contract contract) throws ContractValidationException {
        if(!isValidContract(contract)) throw new ContractValidationException("Contract not valid");
        return contractRepository.save(contract);
    }

    @Transactional
    public Contract reloadContract(Contract contract) {
        return contractRepository.findOne(contract.getUuid());
    }

    /*
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
     */

    @Transactional
    @CacheEvict(value = {"contract", "rate"}, allEntries = true)
    public Contract addProject(Contract contract, Project project) throws ContractValidationException {
        contract.addProject(project);
        if(!isValidContract(contract)) {
            contract.removeProject(project); //.getProjects().remove(project);
            throw new ContractValidationException("Contract not valid");
        }

        // execute
        contract = contractRepository.findOne(contract.getUuid());
        project = projectService.findOne(project.getUuid());
        //project.addContract(contract);
        //project = projectRepository.create(project);
        contract.addProject(project);
        contractRepository.save(contract);
        return contract;
    }

    public Double findConsultantRateByWork(Work work, ContractStatus... statusList) {
        if(work.getTask().getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        if(work.getTask().getType().equals(TaskType.SO)) return 0.0;
        if(work.getWorkasUser()==null) {
            Optional<Contract> optionalContract = findContractByWorkAndUseruuid(work, work.getUseruuid());
            return optionalContract.map(contract -> contract.findByUseruuid(work.getUseruuid()).getRate()).orElse(0.0);
            //return contractRepository.findConsultantRateByWork(DateUtils.getFirstDayOfMonth(work.getRegistered()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), work.getUser().getUuid(), work.getTask().getUuid(), Arrays.stream(statusList).map(Enum::name).toArray(String[]::new));
        } else {
            Optional<Contract> optionalContract = findContractByWorkAndUseruuid(work, work.getWorkas());
            return optionalContract.map(contract -> contract.findByUseruuid(work.getWorkas()).getRate()).orElse(0.0);
            //return contractRepository.findConsultantRateByWork(DateUtils.getFirstDayOfMonth(work.getRegistered()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), work.getWorkasUser().getUuid(), work.getTask().getUuid(), Arrays.stream(statusList).map(Enum::name).toArray(String[]::new));
        }
    }

    //@Cacheable(value = "work")
    public List<Work> findBillableWorkByPeriod(LocalDate fromDate, LocalDate toDate) {
        return workService.findByPeriod(fromDate, toDate).stream().filter(work -> findConsultantRateByWork(work, ContractStatus.SIGNED, ContractStatus.TIME, ContractStatus.CLOSED, ContractStatus.BUDGET) > 0.0).collect(Collectors.toList());
        //return workRepository.findBillableWorkByPeriod(fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    private Optional<Contract> findContractByWorkAndUseruuid(Work work, String workas) {
        return getContractsByProject(taskService.findOne(work.getTaskuuid()).getProject()).stream().filter(contract -> isBetween(work.getRegistered(), contract.getActiveFrom(), contract.getActiveTo()) && contract.findByUseruuid(workas) != null).findFirst();
    }

    public Contract findContractByWork(Work work, ContractStatus... statusList) {
        if(work.getTask().getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return null;
        if(work.getWorkasUser()==null) {
            return findContractByWorkAndUseruuid(work, work.getUseruuid()).orElse(null);
            //return contractRepository.findContractByWork(DateUtils.getFirstDayOfMonth(work.getRegistered()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), work.getUser().getUuid(), work.getTask().getUuid(), Arrays.stream(statusList).map(Enum::name).collect(Collectors.toList()));
        } else {
            return findContractByWorkAndUseruuid(work, work.getWorkas()).orElse(null);
            //return contractRepository.findContractByWork(DateUtils.getFirstDayOfMonth(work.getRegistered()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), work.getWorkasUser().getUuid(), work.getTask().getUuid(), Arrays.stream(statusList).map(Enum::name).collect(Collectors.toList()));
        }
    }

    public Double findConsultantRate(int year, int month, int day, User user, Task task, ContractStatus... statusList) {
        if(task.getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        if(task.getType().equals(TaskType.SO)) return 0.0;
        Optional<Contract> optionalContract = getContractsByProject(taskService.findOne(task.getUuid()).getProject()).stream().filter(contract -> isBetween(LocalDate.of(year, month, day), contract.getActiveFrom(), contract.getActiveTo()) && contract.findByUseruuid(user.getUuid()) != null).findFirst();
        return optionalContract.map(contract -> contract.findByUseruuid(user.getUuid()).getRate()).orElse(0.0);
        //return contractRepository.findConsultantRateByWork(year + "-" + month + "-" + day, user.getUuid(), task.getUuid(), Arrays.stream(statusList).map(Enum::name).toArray(String[]::new));
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
    public Contract removeProject(Contract contract, Project project) throws ContractValidationException {
        contract = contractRepository.findOne(contract.getUuid());
        contract.removeProject(project);
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
        List<Contract> contracts = contractRepository.findByClientuuid(client.getUuid());
        List<String> projectUuids = contracts.stream().flatMap(contract -> contract.getContractProjects().stream().map(ContractProject::getProjectuuid)).collect(Collectors.toList());
        for (Project project : client.getProjects()) {
            if(!projectUuids.contains(project.getUuid())) projects.add(project);
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

    public List<Contract> findTimeActiveConsultantContracts(User user, LocalDate activeOn) {
        return contractRepository.findTimeActiveConsultantContracts(user.getUuid(), activeOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    public double findAmountUsedOnContract(Contract contract) {
        Double result = workService.findAmountUsedByContract(contract);
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

    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    public Contract findOne(String contractuuid) {
        return contractRepository.findOne(contractuuid);
    }

    public Contract addConsultant(Contract contract, ContractConsultant contractConsultant) throws ContractValidationException {
        contract.addConsultant(contractConsultant);
        if(!isValidContract(contract)) {
            contract.getContractConsultants().remove(contractConsultant);
            throw new ContractValidationException("Contract not valid");
        }

        consultantRepository.save(contractConsultant);

        return contract;
    }

    public static List<Contract> getContractsByDate(List<Contract> contracts, User user, LocalDate date) {
        return contracts.stream()
                .filter(contract -> isBetween(date, contract.getActiveFrom(), contract.getActiveTo()) &&
                        (
                                contract.getStatus().equals(ContractStatus.CLOSED) ||
                                        contract.getStatus().equals(ContractStatus.TIME) ||
                                        contract.getStatus().equals(ContractStatus.SIGNED) ||
                                        contract.getStatus().equals(ContractStatus.BUDGET)
                        ) && contract.findByUser(user)!=null)
                .collect(Collectors.toList());
    }

    public List<Contract> getContractsByProject(Project project) {
        return contractProjectRepository.findByProjectuuid(project.getUuid()).stream().map(ContractProject::getContract).collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static ContractService get() {
        return instance;
    }

    public List<Contract> findByClientuuid(String clientuuid) {
        return contractRepository.findByClientuuid(clientuuid);
    }
}