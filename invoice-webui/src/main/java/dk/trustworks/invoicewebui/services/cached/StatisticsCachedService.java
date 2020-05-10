package dk.trustworks.invoicewebui.services.cached;

import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.*;
import dk.trustworks.invoicewebui.model.enums.*;
import dk.trustworks.invoicewebui.services.*;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.services.ContractService.getContractsByDate;
import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

@Service
public class StatisticsCachedService {

    private final static Logger log = LoggerFactory.getLogger(StatisticsCachedService.class.getName());

    private final ContractService contractService;
    private final WorkService workService;
    private final UserService userService;
    private final InvoiceService invoiceService;
    private final ExpenseService expenseService;

    public StatisticsCachedService(ContractService contractService, WorkService workService, UserService userService, InvoiceService invoiceService, ExpenseService expenseService) {
        this.contractService = contractService;
        this.workService = workService;
        this.userService = userService;
        this.invoiceService = invoiceService;
        this.expenseService = expenseService;
    }

    private List<BudgetDocument> cachedBudgetData = new ArrayList<>();

    public List<BudgetDocument> getBudgetData() {
        if(cachedBudgetData.isEmpty()) cachedBudgetData = createBudgetData();
        return cachedBudgetData;
    }

    private List<AvailabilityDocument> cachedAvailabilityData = new ArrayList<>();

    public List<AvailabilityDocument> getAvailabilityData() {
        if(cachedAvailabilityData.isEmpty()) cachedAvailabilityData = createAvailabilityData();
        return cachedAvailabilityData;
    }

    private List<UserExpenseDocument> cachedUserExpenseData = new ArrayList<>();

    public List<UserExpenseDocument> getUserExpenseData() {
        if(cachedUserExpenseData.isEmpty()) cachedUserExpenseData = createUserExpenseData();
        return cachedUserExpenseData;
    }

    private List<ExpenseDocument> cachedExpenseData = new ArrayList<>();

    public List<ExpenseDocument> getExpenseData() {
        if(cachedExpenseData.isEmpty()) cachedExpenseData = createExpenseData();
        return cachedExpenseData;
    }

    private List<WorkDocument> cachedIncomeData = new ArrayList<>();

    public List<WorkDocument> getIncomeData() {
        if(cachedIncomeData.isEmpty()) cachedIncomeData = createIncomeData();
        return cachedIncomeData;
    }

    private List<InvoicedDocument> cachedInvoiceData = new ArrayList<>();

    public List<InvoicedDocument> getInvoiceData() {
        if(cachedInvoiceData.isEmpty()) cachedInvoiceData = createInvoiceData();
        return cachedInvoiceData;
    }

    public void refreshCache() {
        cachedBudgetData = new ArrayList<>();
        cachedAvailabilityData = new ArrayList<>();
        cachedUserExpenseData = new ArrayList<>();
        cachedIncomeData = new ArrayList<>();
        cachedInvoiceData = new ArrayList<>();
        cachedExpenseData = new ArrayList<>();

        cachedBudgetData = createBudgetData();
        cachedAvailabilityData = createAvailabilityData();
        cachedUserExpenseData = createUserExpenseData();
        cachedIncomeData = createIncomeData();
        cachedInvoiceData = createInvoiceData();
        cachedExpenseData = createExpenseData();
    }

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

                        BudgetDocument budgetDocument = createBudgetDocument(user, startDate, contract, userContract);
                        if (budgetDocument == null) continue;
                        budgetDocumentList.add(budgetDocument);
                    } else {
                        LocalDate finalStartDate = startDate;

                        ContractConsultant userContract = contract.findByUser(user);
                        if(userContract == null || userContract.getRate() == 0.0) continue;

                        double budget = userContract.getBudgets().stream()
                                .filter(budgetNew -> budgetNew.getYear() == finalStartDate.getYear() &&
                                        (budgetNew.getMonth()+1) == finalStartDate.getMonthValue())
                                .mapToDouble(budgetNew -> budgetNew.getBudget() / userContract.getRate()).sum();

                        BudgetDocument budgetDocument = new BudgetDocument(startDate, contract.getClient(), user, contract, budget, contract.findByUser(user).getRate());
                        budgetDocumentList.add(budgetDocument);
                    }
                }
                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(DateUtils.getCurrentFiscalStartDate().plusYears(2))); //LocalDate.now().withDayOfMonth(1).plusYears(1)
        }

        // Adjust for availability
        for (User user : userService.findAll()) {
            LocalDate startDate = LocalDate.of(2014, 7, 1);
            do {
                LocalDate finalStartDate = startDate;
                List<BudgetDocument> budgetDocuments = budgetDocumentList.stream()
                        .filter(budgetDocument -> budgetDocument.getUser().getUuid().equals(user.getUuid()) && budgetDocument.getMonth().isEqual(finalStartDate.withDayOfMonth(1)))
                        .collect(Collectors.toList());

                AvailabilityDocument availability = getConsultantAvailabilityByMonth(user, startDate);

                double sum = budgetDocuments.stream().mapToDouble(BudgetDocument::getGrossBudgetHours).sum();

                if(sum > availability.getNetAvailableHours()) {
                    for (BudgetDocument budgetDocument : budgetDocuments) {
                        double factor = budgetDocument.getGrossBudgetHours() / sum;

                        budgetDocument.setGrossBudgetHours(factor * availability.getNetAvailableHours());
                    }
                }

                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(DateUtils.getCurrentFiscalStartDate().plusYears(2)));
        }

        return budgetDocumentList;
    }

    BudgetDocument createBudgetDocument(User user, LocalDate startDate, Contract contract, ContractConsultant userContract) {
        BudgetDocument result = null;
        double budget = userContract.getHours(); // (f.eks. 35 timer)
        if (budget != 0.0) {
            AvailabilityDocument availability = getConsultantAvailabilityByMonth(user, startDate);
            double monthBudget = budget * availability.getWeeks(); // f.eks. 2019-12-01, 18 days / 5 = 3,6 weeks * 35 (budget) = 126 hours
            result = new BudgetDocument(startDate, contract.getClient(), user, contract, monthBudget, userContract.getRate());
        }

        return result;
    }

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
            } while (startDate.isBefore(DateUtils.getCurrentFiscalStartDate().plusYears(2)));
        }
        return workDocumentList;
    }

    private List<InvoicedDocument> createInvoiceData() {
        List<Invoice> invoices = invoiceService.findByStatuses(InvoiceStatus.CREATED, InvoiceStatus.CREDIT_NOTE, InvoiceStatus.SUBMITTED, InvoiceStatus.PAID);
        List<InvoicedDocument> invoicedDocumentList = new ArrayList<>();
        for (Invoice invoice : invoices) {
            double sum = invoice.getInvoiceitems().stream().mapToDouble(value -> value.hours * value.rate).sum();
            sum -= sum * (invoice.discount / 100.0);

            if(invoice.getBookingdate().isEqual(LocalDate.of(1900,1,1))) {
                invoicedDocumentList.add(new InvoicedDocument(invoice.invoicenumber, invoice.getType(), invoice.getInvoicedate(), sum));
            } else {
                invoicedDocumentList.add(new InvoicedDocument(invoice.invoicenumber, invoice.getType(), invoice.getBookingdate(), sum));
            }
        }
        return invoicedDocumentList;
    }

    private List<AvailabilityDocument> createAvailabilityData() {
        List<AvailabilityDocument> availabilityDocumentList = new ArrayList<>();
        Map<String, Capacity> capacityMap = new HashMap<>();
        for (Capacity capacity : userService.calculateCapacityByPeriod(LocalDate.of(2014, 7, 1), DateUtils.getCurrentFiscalStartDate().plusYears(2))) {
            capacityMap.put(capacity.getUseruuid()+":"+stringIt(capacity.getMonth().withDayOfMonth(1)), capacity);
        }
        for (User user : userService.findAll()) {
            List<Work> vacationByUser = workService.findVacationByUser(user);
            List<Work> sicknessByUser = workService.findSicknessByUser(user);
            LocalDate startDate = LocalDate.of(2014, 7, 1);
            do {
                LocalDate finalStartDate = startDate;
                double vacation = vacationByUser.stream()
                        .filter(work -> work.getRegistered().withDayOfMonth(1).isEqual(finalStartDate))
                        .mapToDouble(work -> {
                            if(work.getRegistered().getDayOfWeek().getValue() == DayOfWeek.FRIDAY.getValue()) return work.getWorkduration()-2.0;
                            return work.getWorkduration();
                        }).sum(); // Her regnes med 7,4 timer per dag, med en sum over hele måneden.
                double sickness = sicknessByUser.stream()
                        .filter(work -> work.getRegistered().withDayOfMonth(1).isEqual(finalStartDate))
                        .mapToDouble(work -> {
                            if(work.getRegistered().getDayOfWeek().getValue() == DayOfWeek.FRIDAY.getValue()) return work.getWorkduration()-2.0;
                            return work.getWorkduration();
                        }).sum(); // Her regnes med 7,4 timer per dag, med en sum over hele måneden.
                //int capacity = userService.calculateCapacityByMonthByUser(user.getUuid(), stringIt(finalStartDate)); // Ofte 37 timer på en uge
                int capacity = capacityMap.getOrDefault(user.getUuid()+":"+stringIt(finalStartDate.withDayOfMonth(1)), new Capacity(user.getUuid(), finalStartDate, 0)).getTotalAllocation();
                UserStatus userStatus = userService.getUserStatus(user, finalStartDate);

                availabilityDocumentList.add(new AvailabilityDocument(user, finalStartDate, capacity, vacation, sickness, userStatus.getType(), userStatus.getStatus()));

                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(DateUtils.getCurrentFiscalStartDate().plusYears(2)));
        }
        return availabilityDocumentList;
    }


    private List<UserExpenseDocument> createUserExpenseData() {
        List<UserExpenseDocument> userExpenseDocumentList = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2014, 7, 1);
        do {
            LocalDate finalStartDate = startDate;
            int consultantNetSalaries = userService.calcMonthSalaries(finalStartDate, ConsultantType.CONSULTANT.toString());
            int staffNetSalaries = userService.calcMonthSalaries(finalStartDate, ConsultantType.STAFF.toString());
            final List<Expense> expenseList = expenseService.findByMonth(finalStartDate.withDayOfMonth(1));
            final double expenseSalaries = expenseList.stream()
                    .filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER))
                    .mapToDouble(Expense::getAmount)
                    .sum();

            final long consultantCount = countActiveConsultantCountByMonth(finalStartDate);

            double totalSalaries = consultantNetSalaries + staffNetSalaries;

            double forholdstal = expenseSalaries / totalSalaries;

            final double staffSalaries = (staffNetSalaries * forholdstal) / consultantCount;//(expenseSalaries - consultantSalaries) / consultantCount;

            final double consultantSalaries = (consultantNetSalaries * forholdstal) / consultantCount;

            final double sharedExpense = expenseList.stream().filter(expense1 -> !expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER) && !expense1.getExpensetype().equals(ExcelExpenseType.PERSONALE)).mapToDouble(Expense::getAmount).sum() / consultantCount + consultantSalaries;

            final double personaleExpenses = expenseList.stream().filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.PERSONALE)).mapToDouble(Expense::getAmount).sum() / consultantCount;

            if(expenseSalaries <= 0) {
                startDate = startDate.plusMonths(1);
                continue;
            }

            for (User user : userService.findAll()) {
                UserStatus userStatus = userService.getUserStatus(user, finalStartDate);
                if(userStatus.getType().equals(ConsultantType.CONSULTANT) && userStatus.getStatus().equals(StatusType.ACTIVE)) {
                    AvailabilityDocument availability = getConsultantAvailabilityByMonth(user, finalStartDate);
                    if (availability == null || availability.getGrossAvailableHours() <= 0.0) continue;
                    int salary = userService.getUserSalary(user, finalStartDate);
                    UserExpenseDocument userExpenseDocument = new UserExpenseDocument(finalStartDate, user, sharedExpense, salary, staffSalaries, personaleExpenses);
                    userExpenseDocumentList.add(userExpenseDocument);
                }
            }
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1)));

        return userExpenseDocumentList;
    }

    private List<ExpenseDocument> createExpenseData() {
        List<ExpenseDocument> expenseDocumentList = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2014, 7, 1);
        do {
            LocalDate finalStartDate = startDate;
            final List<Expense> expenseList = expenseService.findByMonth(finalStartDate.withDayOfMonth(1));
            final double expenseSalaries = expenseList.stream()
                    .filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER))
                    .mapToDouble(Expense::getAmount)
                    .sum();
            final double expensePersonale = expenseList.stream()
                    .filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.PERSONALE))
                    .mapToDouble(Expense::getAmount)
                    .sum();
            final double expenseAdministration = expenseList.stream()
                    .filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.ADMINISTRATION))
                    .mapToDouble(Expense::getAmount)
                    .sum();
            final double expenseLokale = expenseList.stream()
                    .filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.LOKALE))
                    .mapToDouble(Expense::getAmount)
                    .sum();
            final double expenseProduktion = expenseList.stream()
                    .filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.PRODUKTION))
                    .mapToDouble(Expense::getAmount)
                    .sum();
            final double expenseSalg = expenseList.stream()
                    .filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.SALG))
                    .mapToDouble(Expense::getAmount)
                    .sum();

            ExpenseDocument expenseDocument = new ExpenseDocument(finalStartDate, expenseSalaries, expensePersonale, expenseLokale, expenseSalg, expenseAdministration, expenseProduktion);
            expenseDocumentList.add(expenseDocument);

            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(LocalDate.now().plusMonths(1).withDayOfMonth(1)));

        return expenseDocumentList;
    }

    public long getActiveEmployeeCountByMonth(LocalDate month) {
        List<AvailabilityDocument> availabilityData = getAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)) &&
                                availabilityDocument.getGrossAvailableHours()>0.0)
                .count();
    }

    public long countActiveConsultantCountByMonth(LocalDate month) {
        List<AvailabilityDocument> availabilityData = getAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)) &&
                                availabilityDocument.getGrossAvailableHours()>0.0 &&
                                availabilityDocument.getConsultantType().equals(ConsultantType.CONSULTANT) &&
                                availabilityDocument.getStatusType().equals(StatusType.ACTIVE))
                .count();
    }

    public long countActiveEmployeeTypesByMonth(LocalDate month, ConsultantType... consultantTypes) {
        List<AvailabilityDocument> availabilityData = getAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)) &&
                                availabilityDocument.getGrossAvailableHours()>0.0 &&
                                Arrays.asList(consultantTypes).contains(availabilityDocument.getConsultantType()) &&
                                availabilityDocument.getStatusType().equals(StatusType.ACTIVE))
                .count();
    }

    public double getConsultantRevenueByMonth(User user, LocalDate month) {
        List<WorkDocument> incomeData = getIncomeData();
        return incomeData.stream()
                .filter(workDocument -> (workDocument.getUser().getUuid().equals(user.getUuid()) && workDocument.getMonth().isEqual(month.withDayOfMonth(1))))
                .mapToDouble(workDocument -> workDocument.getRate() * workDocument.getWorkHours()).sum();
    }

    public double getRegisteredRevenueByMonth(LocalDate month) {
        List<WorkDocument> incomeData = getIncomeData();
        return incomeData.stream()
                .filter(workDocument -> (workDocument.getMonth().withDayOfMonth(1).isEqual(month.withDayOfMonth(1))))
                .mapToDouble(workDocument -> workDocument.getRate() * workDocument.getWorkHours()).sum();
    }

    public double getConsultantRevenueHoursByMonth(User user, LocalDate month) {
        List<WorkDocument> incomeData = getIncomeData();
        return incomeData.stream()
                .filter(workDocument -> (workDocument.getUser().getUuid().equals(user.getUuid()) && workDocument.getMonth().isEqual(month.withDayOfMonth(1))))
                .mapToDouble(WorkDocument::getWorkHours).sum();
    }

    public double getHoursByMonth(LocalDate month) {
        List<WorkDocument> incomeData = getIncomeData();
        return incomeData.stream().filter(workDocument -> workDocument.getMonth().isEqual(month.withDayOfMonth(1))).mapToDouble(WorkDocument::getWorkHours).sum();
    }

    public double getTotalInvoiceSumByMonth(LocalDate month) {
        return getInvoiceData().stream()
                .filter(invoicedDocument -> invoicedDocument.getMonth().withDayOfMonth(1).isEqual(month.withDayOfMonth(1)))
                .mapToDouble(value -> value.getInvoiceType().equals(InvoiceType.CREDIT_NOTE)?(-value.getInvoiced()):value.getInvoiced()).sum();
    }

    public double getConsultantBudgetByMonth(User user, LocalDate month) {
        List<BudgetDocument> budgetData = getBudgetData();
        return budgetData.stream()
                .filter(budgetDocument -> budgetDocument.getUser().getUuid().equals(user.getUuid()) && budgetDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(budgetDocument -> budgetDocument.getGrossBudgetHours() * budgetDocument.getRate()).sum();
    }

    public List<BudgetDocument> getConsultantBudgetDataByMonth(User user, LocalDate month) {
        List<BudgetDocument> budgetData = getBudgetData();
        return budgetData.stream()
                .filter(budgetDocument -> budgetDocument.getUser().getUuid().equals(user.getUuid()) && budgetDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .collect(Collectors.toList());
    }

    public double getConsultantBudgetHoursByMonth(User user, LocalDate month) {
        List<BudgetDocument> budgetData = getBudgetData();
        return budgetData.stream()
                .filter(budgetDocument -> budgetDocument.getUser().getUuid().equals(user.getUuid()) && budgetDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(BudgetDocument::getGrossBudgetHours).sum();
    }

    public AvailabilityDocument getConsultantAvailabilityByMonth(User user, LocalDate month) {
        List<AvailabilityDocument> availabilityData = getAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getUser().getUuid().equals(user.getUuid()) &&
                                availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .findAny().orElse(null);
    }

    public double calcAllUserExpensesByMonth(LocalDate month) {
        List<UserExpenseDocument> userExpenseData = getUserExpenseData();
        return userExpenseData.stream()
                .filter(expenseDocument -> expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(UserExpenseDocument::getExpenseSum).sum();
    }

    public List<ExpenseDocument> getAllExpensesByMonth(LocalDate month) {
        List<ExpenseDocument> expenseData = getExpenseData();
        return expenseData.stream()
                .filter(expenseDocument -> expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .collect(Collectors.toList());
    }

    public double calcAllExpensesByMonth(LocalDate month) {
        List<ExpenseDocument> expenseData = getExpenseData();
        return expenseData.stream()
                .filter(expenseDocument -> expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(ExpenseDocument::sum).sum();
    }

    public double getSharedExpensesAndStaffSalariesByMonth(LocalDate month) {
        List<UserExpenseDocument> expenseData = getUserExpenseData();
        return expenseData.stream()
                .filter(expenseDocument -> expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(expenseDocument1 -> (expenseDocument1.getSharedExpense()+expenseDocument1.getStaffSalaries())).sum();
    }

    public UserExpenseDocument getConsultantExpensesByMonth(User user, LocalDate month) {
        List<UserExpenseDocument> expenceData = getUserExpenseData();
        return expenceData.stream()
                .filter(
                        expenseDocument -> expenseDocument.getUser().getUuid().equals(user.getUuid())
                                && expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .findAny().orElse(new UserExpenseDocument(month, user, 0.0, 0.0, 0.0, 0.0));
    }

    public List<UserExpenseDocument> getConsultantsExpensesByMonth(LocalDate month) {
        List<UserExpenseDocument> expenceData = getUserExpenseData();
        return expenceData.stream()
                .filter(expenseDocument -> expenseDocument.getMonth().isEqual(month.withDayOfMonth(1))).collect(Collectors.toList());
    }

}
