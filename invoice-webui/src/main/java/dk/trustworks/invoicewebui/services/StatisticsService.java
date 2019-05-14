package dk.trustworks.invoicewebui.services;

import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsArea;
import com.vaadin.addon.charts.model.style.SolidColor;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.*;
import dk.trustworks.invoicewebui.model.enums.*;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.services.ContractService.getContractsByDate;
import static dk.trustworks.invoicewebui.utils.DateUtils.*;

@Service
public class StatisticsService {

    private final static Logger log = LoggerFactory.getLogger(StatisticsService.class.getName());

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final ContractService contractService;

    private final BudgetNewRepository budgetNewRepository;

    private final ExpenseRepository expenseRepository;

    private final WorkService workService;

    private final InvoiceService invoiceService;

    private final UserService userService;

    @Autowired
    public StatisticsService(GraphKeyValueRepository graphKeyValueRepository, ContractService contractService, BudgetNewRepository budgetNewRepository, ExpenseRepository expenseRepository, WorkService workService, InvoiceService invoiceService, UserService userService) {
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.contractService = contractService;
        this.budgetNewRepository = budgetNewRepository;
        this.expenseRepository = expenseRepository;
        this.workService = workService;
        this.invoiceService = invoiceService;
        this.userService = userService;
    }

    @Getter(lazy=true) private final List<BudgetDocument> cachedBudgedData = createBudgetData();

    private List<BudgetDocument> createBudgetData() {
        List<BudgetDocument> budgetDocumentList = new ArrayList<>();

        List<Contract> contracts = contractService.findAll();

        for (User user : userService.findAll()) {
            LocalDate startDate = LocalDate.of(2014, 7, 1);

            do {
                List<Contract> activeContracts = getContractsByDate(contracts, user, startDate);
                for (Contract contract : activeContracts) {
                    if(contract.getContractType().equals(ContractType.PERIOD)) {

                        ContractConsultant userContract = contract.findByUser(user);
                        if(userContract == null) continue;

                        double budget = userContract.getHours();
                        if(budget == 0.0) continue;

                        AvailabilityDocument availability = getConsultantAvailabilityByMonth(user, startDate);
                        double monthBudget = (budget * availability.getWeeks()) - availability.getVacation();

                        BudgetDocument budgetDocument = new BudgetDocument(startDate, contract.getClient(), user, contract, monthBudget, userContract.getRate());
                        budgetDocumentList.add(budgetDocument);
                    } else {
                        LocalDate finalStartDate = startDate;

                        ContractConsultant userContract = contract.findByUser(user);
                        if(userContract == null) continue;

                        double budget = userContract.getBudgets().stream()
                                .filter(budgetNew -> budgetNew.getYear() == finalStartDate.getYear() &&
                                        budgetNew.getMonth() == finalStartDate.getMonthValue())
                                .mapToDouble(budgetNew -> budgetNew.getBudget() / userContract.getRate()).sum();

                        BudgetDocument budgetDocument = new BudgetDocument(startDate, contract.getClient(), user, contract, budget, contract.findByUser(user).getRate());
                        budgetDocumentList.add(budgetDocument);
                    }
                }
                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));
        }
        return budgetDocumentList;
    }

    @Getter(lazy=true) private final List<WorkDocument> cachedIncomeData = createIncomeData();

    private List<WorkDocument> createIncomeData() {
        List<WorkDocument> workDocumentList = new ArrayList<>();

        List<Contract> contracts = contractService.findAll();

        for (User user : userService.findAll()) {
            LocalDate startDate = LocalDate.of(2014, 7, 1);

            do {
                List<Contract> activeContracts = ContractService.getContractsByDate(contracts, user, startDate);
                for (Contract contract : activeContracts) {
                    Double hoursRegisteredOnContractByPeriod = workService.findHoursRegisteredOnContractByPeriod(contract.getUuid(), user.getUuid(), startDate, startDate.plusMonths(1).minusDays(1));
                    double hours = (hoursRegisteredOnContractByPeriod==null)?0.0:hoursRegisteredOnContractByPeriod;
                    if(contract.findByUser(user)==null) continue;
                    WorkDocument workDocument = new WorkDocument(startDate, contract.getClient(), user, contract, hours, contract.findByUser(user).getRate());
                    workDocumentList.add(workDocument);
                }
                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));
        }
        return workDocumentList;
    }

    @Getter(lazy=true) private final List<AvailabilityDocument> cachedAvailabilityData = createAvailabilityData();

    private List<AvailabilityDocument> createAvailabilityData() {
        List<AvailabilityDocument> availabilityDocumentList = new ArrayList<>();

        for (User user : userService.findAll()) {
            List<Work> vacationByUser = workService.findVacationByUser(user);
            List<Work> sicknessByUser = workService.findSicknessByUser(user);
            LocalDate startDate = LocalDate.of(2014, 7, 1);
            do {
                LocalDate finalStartDate = startDate;
                double vacation = vacationByUser.stream()
                        .filter(work -> work.getRegistered().withDayOfMonth(1).isEqual(finalStartDate))
                        .mapToDouble(Work::getWorkduration).sum();
                double sickness = sicknessByUser.stream()
                        .filter(work -> work.getRegistered().withDayOfMonth(1).isEqual(finalStartDate))
                        .mapToDouble(Work::getWorkduration).sum();
                int capacity = userService.calculateCapacityByMonthByUser(user.getUuid(), stringIt(finalStartDate));
                UserStatus userStatus = userService.getUserStatus(user, finalStartDate);

                availabilityDocumentList.add(new AvailabilityDocument(user, finalStartDate, capacity, vacation, sickness, userStatus.getType(), userStatus.getStatus()));

                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));
        }
        return availabilityDocumentList;
    }

    @Getter(lazy=true) private final List<ExpenseDocument> cachedExpenseData = createExpenseData();

    private List<ExpenseDocument> createExpenseData() {
        List<ExpenseDocument> expenseDocumentList = new ArrayList<>();

        LocalDate startDate = LocalDate.of(2014, 7, 1);
        do {
            LocalDate finalStartDate = startDate;
            int consultantSalaries = userService.getMonthSalaries(finalStartDate, ConsultantType.CONSULTANT.toString());
            double expenseSalaries = expenseRepository.findByPeriod(
                    finalStartDate.withDayOfMonth(1)).stream()
                    .filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER))
                    .mapToDouble(Expense::getAmount)
                    .sum();
            long consultantCount = getActiveConsultantCountByMonth(finalStartDate);
            double staffSalaries = (expenseSalaries - consultantSalaries) / consultantCount;
            double sharedExpense = expenseRepository.findByPeriod(finalStartDate.withDayOfMonth(1)).stream().filter(expense1 -> !expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum() / consultantCount;

            if(expenseSalaries <= 0) {
                startDate = startDate.plusMonths(1);
                continue;
            }

            for (User user : userService.findAll()) {
                UserStatus userStatus = userService.getUserStatus(user, finalStartDate);
                if(userStatus.getType().equals(ConsultantType.CONSULTANT) && userStatus.getStatus().equals(StatusType.ACTIVE)) {
                    AvailabilityDocument availability = getConsultantAvailabilityByMonth(user, finalStartDate);
                    if (availability == null || availability.getAvailableHours() <= 0.0) continue;
                    int salary = userService.getUserSalary(user, finalStartDate);
                    ExpenseDocument expenseDocument = new ExpenseDocument(finalStartDate, user, sharedExpense, salary, staffSalaries);
                    expenseDocumentList.add(expenseDocument);
                }
            }
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));

        return expenseDocumentList;
    }

    public double getConsultantRevenueByMonth(User user, LocalDate month) {
        List<WorkDocument> incomeData = getCachedIncomeData();
        return incomeData.stream()
                .filter(workDocument -> (workDocument.getUser().getUuid().equals(user.getUuid()) && workDocument.getMonth().isEqual(month.withDayOfMonth(1))))
                .mapToDouble(workDocument -> workDocument.getRate() * workDocument.getWorkHours()).sum();
    }

    public double getConsultantRevenueHoursByMonth(User user, LocalDate month) {
        List<WorkDocument> incomeData = getCachedIncomeData();
        return incomeData.stream()
                .filter(workDocument -> (workDocument.getUser().getUuid().equals(user.getUuid()) && workDocument.getMonth().isEqual(month.withDayOfMonth(1))))
                .mapToDouble(WorkDocument::getWorkHours).sum();
    }

    public double getConsultantBudgetByMonth(User user, LocalDate month) {
        List<BudgetDocument> budgetData = getCachedBudgedData();
        return budgetData.stream()
                .filter(budgetDocument -> budgetDocument.getUser().getUuid().equals(user.getUuid()) && budgetDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(budgetDocument -> budgetDocument.getBudgetHours() * budgetDocument.getRate()).sum();
    }

    public double getConsultantBudgetHoursByMonth(User user, LocalDate month) {
        List<BudgetDocument> budgetData = getCachedBudgedData();
        return budgetData.stream()
                .filter(budgetDocument -> budgetDocument.getUser().getUuid().equals(user.getUuid()) && budgetDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(BudgetDocument::getBudgetHours).sum();
    }

    public AvailabilityDocument getConsultantAvailabilityByMonth(User user, LocalDate month) {
        List<AvailabilityDocument> availabilityData = getCachedAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getUser().getUuid().equals(user.getUuid()) &&
                                availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .findAny().orElse(null);
    }

    public long getActiveEmployeeCountByMonth(LocalDate month) {
        List<AvailabilityDocument> availabilityData = getCachedAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)) &&
                        availabilityDocument.getAvailableHours()>0.0)
                .count();
    }

    public long getActiveConsultantCountByMonth(LocalDate month) {
        List<AvailabilityDocument> availabilityData = getCachedAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)) &&
                                availabilityDocument.getAvailableHours()>0.0 &&
                                availabilityDocument.getConsultantType().equals(ConsultantType.CONSULTANT) &&
                                availabilityDocument.getStatusType().equals(StatusType.ACTIVE))
                .count();
    }

    public double getExpensesByMonth(LocalDate month) {
        List<ExpenseDocument> expenseData = getCachedExpenseData();
        return expenseData.stream()
                .filter(expenseDocument -> expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(ExpenseDocument::getExpenseSum).sum();
    }

    public ExpenseDocument getConsultantExpensesByMonth(User user, LocalDate month) {
        List<ExpenseDocument> expenceData = getCachedExpenseData();
        return expenceData.stream()
                .filter(
                        expenseDocument -> expenseDocument.getUser().getUuid().equals(user.getUuid())
                                && expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .findAny().orElse(new ExpenseDocument(month, user, 0.0, 0.0, 0.0));
    }

    public Map<String, double[]> getBudgetsAndRevenue(User user) {
        LocalDate fromDate = LocalDate.of(2014, 7, 1);
        LocalDate toDate = LocalDate.now().withDayOfMonth(1);

        int months = (int) ChronoUnit.MONTHS.between(fromDate, toDate);

        double[] revenue = new double[months];
        double[] budget = new double[months];
        double[] expenses = new double[months];

        for (int m = 0; m < months; m++) {
            revenue[m] = getConsultantRevenueByMonth(user, fromDate.plusMonths(m));
            budget[m] = getConsultantBudgetByMonth(user, fromDate.plusMonths(m));
            expenses[m] = getConsultantExpensesByMonth(user, fromDate.plusMonths(m)).getExpenseSum();
        }

        HashMap<String, double[]> result = new HashMap<>();
        result.put("revenue", revenue);
        result.put("budget", budget);
        result.put("expenses", expenses);

        return result;
    }

    public Map<String, double[]> getBudgetsAndRevenueHours(User user) {
        LocalDate fromDate = LocalDate.of(2014, 7, 1);
        LocalDate toDate = LocalDate.now().withDayOfMonth(1);

        int months = (int) ChronoUnit.MONTHS.between(fromDate, toDate);

        double[] revenue = new double[months];
        double[] budget = new double[months];

        for (int m = 0; m < months; m++) {
            revenue[m] = getConsultantRevenueHoursByMonth(user, fromDate.plusMonths(m));
            budget[m] = getConsultantBudgetHoursByMonth(user, fromDate.plusMonths(m));
            if(budget[m]>200) System.out.println("budget[m] = " + budget[m]);
        }

        HashMap<String, double[]> result = new HashMap<>();
        result.put("revenue", revenue);
        result.put("budget", budget);

        return result;
    }

    public double getMonthRevenue(LocalDate month) {
        double result = 0.0;
        for (User user : userService.findAll()) {
            result += getConsultantRevenueByMonth(user, month);
        }
        return result;
    }

    public double getMonthBudget(LocalDate month) {
        double result = 0.0;
        for (User user : userService.findAll()) {
            result += getConsultantBudgetByMonth(user, month);
        }
        return result;
    }

    public void run() {
        Map<String, double[]> result = getBudgetsAndRevenue(userService.findByUsername("hans.lassen"));

        System.out.println("revenue = " + Arrays.toString(result.get("revenue")));
        System.out.println("budget = " + Arrays.toString(result.get("budget")));
        System.out.println("expenses = " + Arrays.toString(result.get("expenses")));

        result = getBudgetsAndRevenueHours(userService.findByUsername("hans.lassen"));

        System.out.println("revenue = " + Arrays.toString(result.get("revenue")));
        System.out.println("budget = " + Arrays.toString(result.get("budget")));
    }

    public DataSeries calcRevenuePerMonth(LocalDate periodStart, LocalDate periodEnd) {
        DataSeries revenueSeries = new DataSeries("Revenue");
        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            double invoicedAmountByMonth = invoiceService.invoicedAmountByMonth(currentDate);
            if(invoicedAmountByMonth > 0.0) {
                revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), invoicedAmountByMonth));
            } else {
                revenueSeries.add(new DataSeriesItem(stringIt(currentDate, "MMM-yyyy"), getMonthRevenue(currentDate)));
            }
        }
        return revenueSeries;
    }

    public DataSeries calcBillableHoursRevenuePerMonth(LocalDate periodStart, LocalDate periodEnd) {
        DataSeries revenueSeries = new DataSeries("Billable Hours Revenue");

        PlotOptionsArea plotOptionsArea = new PlotOptionsArea();
        plotOptionsArea.setColor(new SolidColor(84, 214, 158));
        revenueSeries.setPlotOptions(plotOptionsArea);

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            revenueSeries.add(new DataSeriesItem(stringIt(currentDate, "MMM-yyyy"), getMonthRevenue(currentDate)));
        }
        return revenueSeries;
    }

    public DataSeries calcBudgetPerMonth(LocalDate periodStart, LocalDate periodEnd) {
        DataSeries budgetSeries = new DataSeries("Budget");

        PlotOptionsArea plotOptionsArea = new PlotOptionsArea();
        plotOptionsArea.setColor(new SolidColor(18, 51, 117));
        budgetSeries.setPlotOptions(plotOptionsArea);

        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            budgetSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(getMonthBudget(currentDate))));
        }

        return budgetSeries;
    }

    public DataSeries calcEarningsPerMonth(LocalDate periodStart, LocalDate periodEnd) {
        DataSeries earningsSeries = new DataSeries("Earnings");

        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            double invoicedAmountByMonth = invoiceService.invoicedAmountByMonth(currentDate);
            double expense = expenseRepository.findByPeriod(currentDate.withDayOfMonth(1)).stream().mapToDouble(Expense::getAmount).sum();
            earningsSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), invoicedAmountByMonth-expense));
        }
        return earningsSeries;
    }

    public List<UserBooking> getUserBooking(int monthsInPast, int monthsInFuture) {
        List<UserBooking> userBookings = new ArrayList<>();
        Map<String, UserProjectBooking> userProjectBookingMap = new HashMap<>();
        LocalDate currentDate;
        for (User user : userService.findCurrentlyEmployedUsers(ConsultantType.CONSULTANT)) {
            currentDate = LocalDate.now().withDayOfMonth(1).minusMonths(monthsInPast);
            UserBooking userBooking = new UserBooking(user.getUsername(),user.getUuid(), monthsInFuture, true);
            userBookings.add(userBooking);

            for (int i = 0; i < monthsInFuture; i++) {
                List<Contract> contracts = contractService.findActiveContractsByDate(currentDate, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
                for (Contract contract : contracts) {
                    if(contract.getContractType().equals(ContractType.PERIOD)) {
                        for (ContractConsultant contractConsultant : contract.getContractConsultants().stream().filter(c -> c.getUser().getUsername().equals(user.getUsername())).collect(Collectors.toList())) {
                            String key = contractConsultant.getUser().getUuid()+contractConsultant.getContract().getClient().getUuid();
                            if(!userProjectBookingMap.containsKey(key)) {
                                UserProjectBooking newUserProjectBooking = new UserProjectBooking(contractConsultant.getContract().getClient().getName(), contractConsultant.getContract().getClient().getUuid(), monthsInFuture, false);
                                userProjectBookingMap.put(key, newUserProjectBooking);
                                userBooking.addSubProject(newUserProjectBooking);
                            }
                            UserProjectBooking userProjectBooking = userProjectBookingMap.get(key);
                            log.info("currentDate = " + currentDate);
                            double workDaysInMonth = workService.getWorkDaysInMonth(contractConsultant.getUser().getUuid(), currentDate);
                            double weeks = (workDaysInMonth / 5.0);
                            double preBooking = 0.0;
                            double budget = 0.0;
                            double booking;
                            if(i < monthsInPast) {
                                budget = NumberUtils.round((contractConsultant.getHours() * weeks), 2);
                                Double preBookingObj = workService.findHoursRegisteredOnContractByPeriod(contract.getUuid(), user.getUuid(), getFirstDayOfMonth(currentDate), getLastDayOfMonth(currentDate));
                                if(preBookingObj != null) preBooking = preBookingObj;
                                booking = NumberUtils.round((preBooking / budget) * 100.0, 2);
                            } else {
                                if (contract.getStatus().equals(ContractStatus.BUDGET)) {
                                    preBooking = NumberUtils.round((contractConsultant.getHours() * weeks), 2);
                                } else {
                                    budget = NumberUtils.round((contractConsultant.getHours() * weeks), 2);
                                }
                                booking = NumberUtils.round((budget / (workDaysInMonth * 7)) * 100.0, 2);
                            }
                            userProjectBooking.setAmountItemsPerProjects(budget, i);
                            userProjectBooking.setAmountItemsPerPrebooking(preBooking, i);
                            userProjectBooking.setBookingPercentage(booking, i);
                        }
                    }
                }

                List<BudgetNew> budgets = budgetNewRepository.findByMonthAndYear(currentDate.getMonthValue() - 1, currentDate.getYear());
                for (BudgetNew budget : budgets) {
                    if(!budget.getContractConsultant().getUser().getUsername().equals(user.getUsername())) continue;

                    String key = budget.getContractConsultant().getUser().getUuid()+budget.getProject().getUuid();
                    if(!userProjectBookingMap.containsKey(key)) {
                        UserProjectBooking newUserProjectBooking = new UserProjectBooking(budget.getProject().getName() + " / " + budget.getProject().getClient().getName(), budget.getProject().getClient().getUuid(), monthsInFuture, false);
                        userProjectBookingMap.put(key, newUserProjectBooking);
                        userBooking.addSubProject(newUserProjectBooking);
                    }
                    UserProjectBooking userProjectBooking = userProjectBookingMap.get(key);

                    double workDaysInMonth = workService.getWorkDaysInMonth(user.getUuid(), currentDate);
                    double preBooking = 0.0;
                    double hourBudget = 0.0;
                    double booking;

                    if(i < monthsInPast) {
                        hourBudget = NumberUtils.round(budget.getBudget() / budget.getContractConsultant().getRate(), 2);
                        preBooking = Optional.ofNullable(workService.findHoursRegisteredOnContractByPeriod(budget.getContractConsultant().getContract().getUuid(), budget.getContractConsultant().getUser().getUuid(), getFirstDayOfMonth(currentDate), getLastDayOfMonth(currentDate))).orElse(0.0);
                        booking = NumberUtils.round((preBooking / hourBudget) * 100.0, 2);
                    } else {
                        if (budget.getContractConsultant().getContract().getStatus().equals(ContractStatus.BUDGET)) {
                            preBooking = NumberUtils.round(budget.getBudget() / budget.getContractConsultant().getRate(), 2);
                        } else {
                            hourBudget = NumberUtils.round(budget.getBudget() / budget.getContractConsultant().getRate(), 2);
                        }
                        booking = NumberUtils.round(((hourBudget) / (workDaysInMonth * 7)) * 100.0, 2);
                    }

                    userProjectBooking.setAmountItemsPerProjects(hourBudget, i);
                    userProjectBooking.setAmountItemsPerPrebooking(preBooking, i);
                    userProjectBooking.setBookingPercentage(booking, i);
                    //userProjectBooking.setMonthNorm(NumberUtils.round(workDaysInMonth * 7,2), i);
                }

                currentDate = currentDate.plusMonths(1);
            }
        }

        for(UserBooking userBooking : userBookings) {
            if(userBooking.getSubProjects().size() == 0) continue;
            boolean debug = (userBooking.getUsername().equals("hans.lassen"));
            for (UserBooking subProject : userBooking.getSubProjects()) {
                currentDate = LocalDate.now().withDayOfMonth(1).minusMonths(monthsInPast);
                for (int i = 0; i < monthsInFuture; i++) {
                    if(debug) log.info("i = " + i);
                    userBooking.addAmountItemsPerProjects(subProject.getAmountItemsPerProjects(i), i);
                    userBooking.addAmountItemsPerPrebooking(subProject.getAmountItemsPerPrebooking(i), i);
                    int workDaysInMonth = workService.getWorkDaysInMonth(userService.findByUsername(userBooking.getUsername()).getUuid(), currentDate);
                    userBooking.setMonthNorm(NumberUtils.round(workDaysInMonth * 7, 2), i);
                    subProject.setMonthNorm(NumberUtils.round(workDaysInMonth * 7, 2), i);
                    currentDate = currentDate.plusMonths(1);
                }
            }

            for (int i = 0; i < monthsInFuture; i++) {
                if(i < monthsInPast) {
                    userBooking.setBookingPercentage(NumberUtils.round((userBooking.getAmountItemsPerPrebooking(i) / userBooking.getAmountItemsPerProjects(i)) * 100.0, 2), i);
                } else {
                    if (userBooking.getMonthNorm(i) > 0.0)
                        userBooking.setBookingPercentage(NumberUtils.round((userBooking.getAmountItemsPerProjects(i) / (userBooking.getMonthNorm(i))) * 100.0, 2), i);
                }
            }
        }
        return userBookings;
    }

    @Cacheable("calculateConsultantRevenue")
    public Map<LocalDate, Double> calculateConsultantRevenue(User user, LocalDate periodStart, LocalDate periodEnd, int interval) {
        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        double revenueSum = 0.0;
        int count = 1;
        Map<LocalDate, Double> resultMap = new HashMap<>();
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            if(userService.isActive(user, currentDate, ConsultantType.CONSULTANT)) {
                int consultantCount = userService.findWorkingUsersByDate(currentDate, ConsultantType.CONSULTANT).size();
                double expense = expenseRepository.findByPeriod(currentDate.withDayOfMonth(1)).stream().filter(expense1 -> !expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum() / consultantCount;

                if (expense == 0) {
                    count = 1;
                    revenueSum = 0.0;
                    continue;
                }

                double revenue = graphKeyValueRepository.findConsultantRevenueByPeriod(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), currentDate.withDayOfMonth(currentDate.getMonth().length(currentDate.isLeapYear())).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).stream().filter(graphKeyValue -> graphKeyValue.getUuid().equals(user.getUuid())).mapToDouble(GraphKeyValue::getValue).sum();
                int userSalary = userService.getUserSalary(user, currentDate);
                int consultantSalaries = userService.getMonthSalaries(currentDate, ConsultantType.CONSULTANT.toString());
                double expenseSalaries = expenseRepository.findByPeriod(currentDate.withDayOfMonth(1)).stream().filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum();
                double staffSalaries = (expenseSalaries - consultantSalaries) / consultantCount;

                revenueSum += (revenue - userSalary - expense - staffSalaries);
            }

            if(count == interval) {
                resultMap.put(currentDate, revenueSum / interval);
                revenueSum = 0.0;
                count = 1;
                continue;
            }

            count++;
        }
        return resultMap;
    }

    public String[] getCategories(LocalDate periodStart, LocalDate periodEnd) {
        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);
        String[] categories = new String[months];
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            categories[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }
        return categories;
    }
}
