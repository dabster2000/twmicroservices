package dk.trustworks.invoicewebui.jobs;

import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.ContractConsultant;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.Work;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.TaskType;
import dk.trustworks.invoicewebui.network.clients.SlackAPI;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.StatisticsService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.services.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChartCacheJob {

    private final WorkService workService;

    private final ContractService contractService;

    private final Map<String, Number> revenueMap;

    private final Map<String, Map<LocalDate, Double>> burndownCache;

    private final StatisticsService statisticsService;

    private final SlackAPI slackAPI;

    private final UserService userService;

    @Autowired
    public ChartCacheJob(WorkService workService, ContractService contractService, StatisticsService statisticsService, SlackAPI slackAPI, UserService userService) {
        this.workService = workService;
        this.contractService = contractService;
        this.statisticsService = statisticsService;
        this.slackAPI = slackAPI;
        this.userService = userService;
        revenueMap = new TreeMap<>();
        burndownCache = new HashMap<>();
    }

    @Scheduled(cron = "0 0 6,12,18 * * *")
    private void loadCachedData() {
        User user = userService.findByUsername("hans.lassen");
        slackAPI.sendSlackMessage(user, "Reloading cached data...");
        statisticsService.refreshCache();
        slackAPI.sendSlackMessage(user, "...done!");
    }

    @Scheduled(cron = "0 0 4 5 1/1 ?")
    private void loadRevenuePerClientMap() {
        revenueMap.clear();
        for (Work work : workService.findByActiveClients()) {
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

    public void refreshBurndownRateForSingleContract(Contract mainContract) {
        loadBurndownRateForSingleContract(mainContract);
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
        for (Work work : contractService.getWorkOnContractByUser(mainContract).stream().sorted(Comparator.comparing(Work::getRegistered)).collect(Collectors.toList())) {
            if(work.getTask().getType().equals(TaskType.SO)) continue;
            Optional<ContractConsultant> optionalConsultant = mainContract.getContractConsultants().stream().filter(consultant -> consultant.getUser().getUuid().equals(work.getUser().getUuid())).findFirst();
            if(!optionalConsultant.isPresent()) continue;
            budget -= (work.getWorkduration() * optionalConsultant.get().getRate());
            runningBudget.put(work.getRegistered(), budget);
        }
        burndownCache.put(mainContract.getUuid(), runningBudget);
    }

    public Map<LocalDate, Double> getBurndownDateForContract(Contract mainContract) {
        if(!burndownCache.containsKey(mainContract.getUuid())) loadBurndownRateForSingleContract(mainContract);
        return burndownCache.get(mainContract.getUuid());
    }
}
