package dk.trustworks.invoicewebui.jobs;

//@Component
public class BudgetCleanupJob {
/*
    private static final Logger log = LoggerFactory.getLogger(BudgetCleanupJob.class);

    private final WorkRepository workRepository;

    private final ContractService contractService;

    private final BudgetRepository budgetRepository;

    @Autowired
    public BudgetCleanupJob(WorkRepository workRepository, ContractService contractService, BudgetRepository budgetRepository) {
        this.workRepository = workRepository;
        this.contractService = contractService;
        this.budgetRepository = budgetRepository;
    }

    //@PostConstruct
    public void init() {
        LocalDate localDate = LocalDate.now().minusMonths(22);
        while(localDate.isBefore(LocalDate.now())) {
            int month = localDate.getMonthOfYear();
            int year = localDate.getYear();
            log.debug("ProjectBudgetHandler.budgetcleanup");
            log.debug("year = " + year);
            log.debug("month = " + month);
        }
    }

    @Transactional
    //@Scheduled(cron = "0 0 4 5 1/1 ?")
    public void job()  {
        int month = LocalDate.now().minusMonths(1).getMonthOfYear();
        int year = LocalDate.now().minusMonths(1).getYear();
        log.debug("ProjectBudgetHandler.budgetcleanup");
        log.debug("year = " + year);
        log.debug("month = " + month);

        List<Budget> workBudgets = new ArrayList<>();
        Map<String, Budget> workBudgetMap = new HashMap<>();
        for (Work work : workRepository.findByPeriod(year+"-"+month+"-01", year+"-"+month+"-31")) {
            Double rate = contractService.findConsultantRateByWork(work);
            if(rate == null) continue;
            if (!workBudgetMap.containsKey(work.getUser().getUuid()+work.getTask().getUuid())) {
                Budget budget = new Budget(month - 1, year, 0.0, work.getUser(), work.getTask());
                workBudgetMap.put(work.getUser().getUuid()+work.getTask().getUuid(), budget);
                log.debug("budget = " + budget);
            }
            Budget currentBudget = workBudgetMap.get(work.getUser().getUuid()+work.getTask().getUuid());
            currentBudget.setBudget((currentBudget.getBudget() + (work.getWorkduration() * rate)));
        }
        workBudgets.addAll(workBudgetMap.values());
        log.debug("workBudgets.size() = " + workBudgets.size());

        log.debug("---");
        List<Budget> newBudgets = new ArrayList<>(); // budgetter der skal gemmes
        List<Budget> existingRemoveList = new ArrayList<>(); // budgetter der skal fjernes
        List<Budget> existingBudgets = budgetRepository.findByMonthAndYear(month - 1, year);
        log.debug("existingBudgets = " + existingBudgets.size());
        for (Budget existingBudget : existingBudgets) {
            log.debug("existingBudget = " + existingBudget);
        }

        for (Budget workBudget : workBudgets) {
            for (Budget existingbudget : existingBudgets) {
                // find dem der er lavet nye og som er i eksisterende
                if(workBudget.getTask().getUuid().equals(existingbudget.getTask().getUuid()) &&
                        workBudget.getUser().getUuid().equals(existingbudget.getUser().getUuid())) {
                    log.debug("found match!!!");
                    //newBudgets.add(workBudget); // Tilføj
                    existingRemoveList.add(existingbudget); // Fjern budgets, hvor der er nye og opdaterede
                }
            }
            log.debug("existingBudgets.size() = " + existingBudgets.size());
            log.debug("existingRemoveList.size() = " + existingRemoveList.size());
            existingBudgets.removeAll(existingRemoveList);
            log.debug("existingBudgets.size() = " + existingBudgets.size());
            existingRemoveList.clear();
            log.debug("existingRemoveList.size() = " + existingRemoveList.size());
            newBudgets.add(workBudget);
        }
        log.debug("newBudgets.size() = " + newBudgets.size());

        for (Budget existingBudget : existingBudgets) {
            Budget saveBudget = new Budget(existingBudget.getMonth(), existingBudget.getYear(), 0.0, existingBudget.getUser(), existingBudget.getTask());
            newBudgets.add(saveBudget);
        }
        log.debug("newBudgets.size() = " + newBudgets.size());

        budgetRepository.create(newBudgets);
        log.debug("done");
    }
*/
}
