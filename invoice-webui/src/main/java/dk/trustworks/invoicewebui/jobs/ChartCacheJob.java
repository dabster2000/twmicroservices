package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.model.Consultant;
import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.TaskType;
import dk.trustworks.invoicewebui.repositories.WorkRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChartCacheJob {

    private final WorkRepository workRepository;

    private final ContractService contractService;

    private final Map<String, Number> revenueMap;

    private final Map<String, Map<LocalDate, Double>> burndownCache;

    @Autowired
    public ChartCacheJob(WorkRepository workRepository, ContractService contractService) {
        this.workRepository = workRepository;
        this.contractService = contractService;
        revenueMap = new TreeMap<>();
        burndownCache = new HashMap<>();
    }

    @Scheduled(cron = "0 0 4 5 1/1 ?")
    private void loadRevenuePerClientMap() {
        revenueMap.clear();
        for (Work work : workRepository.findByActiveClients()) {
            String clientName = work.getTask().getProject().getClient().getName();
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate == null || rate == 0.0) continue;
            revenueMap.putIfAbsent(clientName, 0);
            revenueMap.put(clientName, revenueMap.get(clientName).doubleValue()+(work.getWorkduration() * rate));
        }
    }

    public Map<String, Number> getRevenuePerClientMap() {
        if(revenueMap.isEmpty()) loadRevenuePerClientMap();
        return revenueMap;
    }

    @Scheduled(cron = "0 0 4 5 1/1 ?")
    private void loadBurndownDateForAllContract() {
        for (Contract contract : contractService.findAll()) {
            loadBurndownRateForSingleContract(contract);
        }
    }

    private void loadBurndownRateForSingleContract(Contract mainContract) {
        Map<LocalDate, Double> runningBudget = new TreeMap<>();
        double budget = mainContract.getAmount();
        for (Work work : contractService.getWorkOnContractByUser(mainContract).stream().sorted(Comparator.comparing(Work::getYear).thenComparing(Work::getMonth).thenComparing(Work::getDay)).collect(Collectors.toList())) {
            if(work.getTask().getType().equals(TaskType.SO)) continue;
            Optional<Consultant> optionalConsultant = mainContract.getConsultants().stream().filter(consultant -> consultant.getUser().getUuid().equals(work.getUser().getUuid())).findFirst();
            if(!optionalConsultant.isPresent()) continue;
            budget -= (work.getWorkduration() * optionalConsultant.get().getRate());
            LocalDate workDate = LocalDate.of(work.getYear(), work.getMonth()+1, work.getDay());
            runningBudget.put(workDate, budget);
        }
        burndownCache.put(mainContract.getUuid(), runningBudget);
    }

    public Map<LocalDate, Double> getBurndownDateForContract(Contract mainContract) {
        if(!burndownCache.containsKey(mainContract.getUuid())) loadBurndownRateForSingleContract(mainContract);
        return burndownCache.get(mainContract.getUuid());
    }
}
