package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.TaskType;
import dk.trustworks.invoicewebui.network.rest.ContractRestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContractService implements InitializingBean {

    private static ContractService instance;

    private final ContractRestService contractRestService;

    private final ProjectService projectService;

    private final WorkService workService;

    @Autowired
    public ContractService(ContractRestService contractRestService, ProjectService projectService, TaskService taskService, WorkService workService) {
        this.contractRestService = contractRestService;
        this.projectService = projectService;
        this.workService = workService;
    }

    public Contract createContract(Contract contract) {
        return contractRestService.save(contract);
    }

    public void updateContract(Contract contract) {
        contractRestService.update(contract);
    }

    public Contract reloadContract(Contract contract) {
        return contractRestService.findByUuid(contract.getUuid());
    }

    public void addProject(Contract contract, Project project) {
        contractRestService.addProjectToContract(contract, project);
    }

    // TODO: Move to API Gateway when reciept and invoice are migrated.

    public Double findConsultantRateByWork(Work work, ContractStatus... statusList) {
        if(work.getRate()>0) {
            return work.getRate();
        }
        System.out.println("WORK UNKNOWN: "+work);
        return 0.0;
        /*
        if(work.getClientuuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        if(work.getTask().getType().equals(TaskType.SO)) return 0.0;
        if(work.getWorkasUser()==null) {
            //Optional<Contract> optionalContract = findContractByWorkAndUseruuid(work, work.getUseruuid());
            //return optionalContract.map(contract -> contract.findByUseruuid(work.getUseruuid()).getRate()).orElse(0.0);
            return projectService.findRateByProjectAndUserAndDate(work.getProjectuuid(), work.getUseruuid(), work.getRegistered());
        } else {
            return projectService.findRateByProjectAndUserAndDate(work.getProjectuuid(), work.getWorkas(), work.getRegistered());
            //Optional<Contract> optionalContract = findContractByWorkAndUseruuid(work, work.getWorkas());
            //return optionalContract.map(contract -> contract.findByUseruuid(work.getWorkas()).getRate()).orElse(0.0);
        }

         */
    }



    /*
    public List<Work> findBillableWorkByPeriod(LocalDate fromDate, LocalDate toDate) {
        return workService.findByPeriod(fromDate, toDate).stream().filter(work -> findConsultantRateByWork(work, ContractStatus.SIGNED, ContractStatus.TIME, ContractStatus.CLOSED, ContractStatus.BUDGET) > 0.0).collect(Collectors.toList());
    }
     */
/*
    private Optional<Contract> findContractByWorkAndUseruuid(Work work, String workas) {
        return getContractsByProject(taskService.findOne(work.getTaskuuid()).getProject()).stream().filter(contract -> isBetween(work.getRegistered(), contract.getActiveFrom(), contract.getActiveTo()) && contract.findByUseruuid(workas) != null).findFirst();
    }

 */

    /*
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

     */

    public Double findConsultantRate(LocalDate date, User user, Task task, ContractStatus... statusList) {
        if(task.getProject().getClient().getUuid().equals("40c93307-1dfa-405a-8211-37cbda75318b")) return 0.0;
        if(task.getType().equals(TaskType.SO)) return 0.0;
        return findConsultantRateByWork(new Work(date, 0, user, task));
        //Optional<Contract> optionalContract = getContractsByProject(taskService.findOne(task.getUuid()).getProject()).stream().filter(contract -> isBetween(LocalDate.of(year, month, day), contract.getActiveFrom(), contract.getActiveTo()) && contract.findByUseruuid(user.getUuid()) != null).findFirst();
        //return optionalContract.map(contract -> contract.findByUseruuid(user.getUuid()).getRate()).orElse(0.0);
        //return contractRepository.findConsultantRateByWork(year + "-" + month + "-" + day, user.getUuid(), task.getUuid(), Arrays.stream(statusList).map(Enum::name).toArray(String[]::new));
    }

    public List<Contract> findActiveContractsByDate(LocalDate activeDate, ContractStatus... statusList) {
        return contractRestService.findByActiveFromLessThanEqualAndActiveToGreaterThanEqualAndStatusIn(activeDate, activeDate, statusList);
    }

    public void removeProject(Contract contract, Project project) {
        contractRestService.removeProjectFromContract(contract, project);
    }
/*
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

 */

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
    // TODO: REINTRODUCE
    /*
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

     */

    public List<Contract> findTimeActiveConsultantContracts(User user, LocalDate activeOn) {
        return contractRestService.findTimeActiveConsultantContracts(user.getUuid(), activeOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
            contractRestService.delete(contract.getUuid());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<Contract> findAll() {
        return contractRestService.findAll();
    }

    public Contract findOne(String contractuuid) {
        return contractRestService.findByUuid(contractuuid);
    }

    public Contract addConsultant(Contract contract, ContractConsultant contractConsultant) {
        contract.getContractConsultants().add(contractConsultant);
        contractRestService.addConsultant(contract, contractConsultant);
        return contract;
    }

    public void updateConsultant(Contract contract, ContractConsultant contractConsultant) {
        contractRestService.updateConsultant(contract, contractConsultant);
    }

    public void deleteConsultant(Contract contract, ContractConsultant contractConsultant) {
        contractRestService.removeConsultant(contract, contractConsultant);
    }

    public List<Contract> findByClientuuid(String clientuuid) {
        return contractRestService.findByClientuuid(clientuuid);
    }

    /*
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
     */

    public List<Contract> getContractsByProject(Project project) {
        return contractRestService.findByProjectuuid(project.getUuid());
    }

    public List<Project> findProjectsByContractuuid(String contractuuid) {
        return contractRestService.findProjectsByContractuuid(contractuuid);
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static ContractService get() {
        return instance;
    }
}