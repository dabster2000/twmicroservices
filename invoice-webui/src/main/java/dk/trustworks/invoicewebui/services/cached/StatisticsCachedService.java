package dk.trustworks.invoicewebui.services.cached;

import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.*;
import dk.trustworks.invoicewebui.model.enums.*;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.services.ContractService;
import dk.trustworks.invoicewebui.services.InvoiceService;
import dk.trustworks.invoicewebui.services.UserService;
import dk.trustworks.invoicewebui.services.WorkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static dk.trustworks.invoicewebui.services.ContractService.getContractsByDate;
import static dk.trustworks.invoicewebui.utils.DateUtils.stringIt;

@Service
public class StatisticsCachedService {

    private final static Logger log = LoggerFactory.getLogger(StatisticsCachedService.class.getName());

    private final ContractService contractService;
    private final ExpenseRepository expenseRepository;
    private final WorkService workService;
    private final UserService userService;
    private final InvoiceService invoiceService;

    public StatisticsCachedService(ContractService contractService, ExpenseRepository expenseRepository, WorkService workService, UserService userService, InvoiceService invoiceService) {
        this.contractService = contractService;
        this.expenseRepository = expenseRepository;
        this.workService = workService;
        this.userService = userService;
        this.invoiceService = invoiceService;
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
        cachedExpenseData = new ArrayList<>();
        cachedIncomeData = new ArrayList<>();
        cachedInvoiceData = new ArrayList<>();

        cachedBudgetData = createBudgetData();
        cachedAvailabilityData = createAvailabilityData();
        cachedExpenseData = createExpenseData();
        cachedIncomeData = createIncomeData();
        cachedInvoiceData = createInvoiceData();
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

                        double budget = userContract.getHours();
                        if(budget == 0.0) continue;

                        AvailabilityDocument availability = getConsultantAvailabilityByMonth(user, startDate);
                        double monthBudget = budget * availability.getWeeks();

                        BudgetDocument budgetDocument = new BudgetDocument(startDate, contract.getClient(), user, contract, monthBudget, userContract.getRate());
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
            } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));
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

                double sum = budgetDocuments.stream().mapToDouble(BudgetDocument::getBudgetHours).sum();
                if(sum > availability.getAvailableHours()) {
                    for (BudgetDocument budgetDocument : budgetDocuments) {
                        double factor = budgetDocument.getBudgetHours() / sum;
                        budgetDocument.setBudgetHours(factor * availability.getAvailableHours());
                    }
                }

                startDate = startDate.plusMonths(1);
            } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));
        }

        return budgetDocumentList;
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
            } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));
        }
        return workDocumentList;
    }

    private List<InvoicedDocument> createInvoiceData() {
        List<Invoice> invoices = invoiceService.findByStatuses(InvoiceStatus.CREATED, InvoiceStatus.CREDIT_NOTE, InvoiceStatus.SUBMITTED, InvoiceStatus.PAID);
        List<InvoicedDocument> invoicedDocumentList = new ArrayList<>();
        for (Invoice invoice : invoices) {
            double sum = invoice.getInvoiceitems().stream().mapToDouble(value -> value.hours * value.rate).sum();
            sum -= sum * (invoice.discount / 100.0);
            invoicedDocumentList.add(new InvoicedDocument(invoice.getType(), invoice.getInvoicedate(), sum));
        }
        return invoicedDocumentList;
    }

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


    private List<ExpenseDocument> createExpenseData() {
        List<ExpenseDocument> expenseDocumentList = new ArrayList<>();
        //System.out.println("----------------------------------");
        LocalDate startDate = LocalDate.of(2014, 7, 1);
        do {
            LocalDate finalStartDate = startDate;
            int consultantSalaries = userService.getMonthSalaries(finalStartDate, ConsultantType.CONSULTANT.toString());
            double expenseSalaries = expenseRepository.findByPeriod(
                    finalStartDate.withDayOfMonth(1)).stream()
                    .filter(expense1 -> expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER))
                    .mapToDouble(Expense::getAmount)
                    .sum();
            long consultantCount = countActiveConsultantCountByMonth(finalStartDate);
            //System.out.println(finalStartDate);
            //System.out.println("getUsername;consultantSalaries;expenseSalaries;consultantCount;staffSalaries;sharedExpense;salary");
            double staffSalaries = (expenseSalaries - consultantSalaries) / consultantCount;
            double sharedExpense = expenseRepository.findByPeriod(finalStartDate.withDayOfMonth(1)).stream().filter(expense1 -> !expense1.getExpensetype().equals(ExcelExpenseType.LØNNINGER)).mapToDouble(Expense::getAmount).sum() / consultantCount;

            if(expenseSalaries <= 0) {
                startDate = startDate.plusMonths(1);
                continue;
            }

            for (User user : userService.findAll()) {
                UserStatus userStatus = userService.getUserStatus(user, finalStartDate);
                if(userStatus.getType().equals(ConsultantType.CONSULTANT) && userStatus.getStatus().equals(StatusType.ACTIVE)) {
                    /*
                    System.out.print("" + user.getUsername());
                    System.out.print(";"+Math.round(consultantSalaries));
                    System.out.print(";" + Math.round(expenseSalaries));
                    System.out.print(";" + Math.round(consultantCount));
                    System.out.print(";" + Math.round(staffSalaries));
                    System.out.print(";" + Math.round(sharedExpense));

                     */
                    AvailabilityDocument availability = getConsultantAvailabilityByMonth(user, finalStartDate);
                    if (availability == null || availability.getAvailableHours() <= 0.0) continue;
                    int salary = userService.getUserSalary(user, finalStartDate);
                    //System.out.println(";" + Math.round(salary));
                    ExpenseDocument expenseDocument = new ExpenseDocument(finalStartDate, user, sharedExpense, salary, staffSalaries);
                    expenseDocumentList.add(expenseDocument);
                }
            }
            //System.out.println();
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1).plusYears(1)));

        return expenseDocumentList;
    }


    public long getActiveEmployeeCountByMonth(LocalDate month) {
        List<AvailabilityDocument> availabilityData = getAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)) &&
                                availabilityDocument.getAvailableHours()>0.0)
                .count();
    }

    public long countActiveConsultantCountByMonth(LocalDate month) {
        List<AvailabilityDocument> availabilityData = getAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)) &&
                                availabilityDocument.getAvailableHours()>0.0 &&
                                availabilityDocument.getConsultantType().equals(ConsultantType.CONSULTANT) &&
                                availabilityDocument.getStatusType().equals(StatusType.ACTIVE))
                .count();
    }

    public long countActiveEmployeeTypesByMonth(LocalDate month, ConsultantType... consultantTypes) {
        List<AvailabilityDocument> availabilityData = getAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)) &&
                                availabilityDocument.getAvailableHours()>0.0 &&
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

    public double getTotalInvoiceSumByMonth(LocalDate month) {
        return getInvoiceData().stream()
                .filter(invoicedDocument -> invoicedDocument.getMonth().withDayOfMonth(1).isEqual(month.withDayOfMonth(1)))
                .mapToDouble(value -> value.getInvoiceType().equals(InvoiceType.CREDIT_NOTE)?(-value.getInvoiced()):value.getInvoiced()).sum();
    }

    public double getConsultantBudgetByMonth(User user, LocalDate month) {
        List<BudgetDocument> budgetData = getBudgetData();
        return budgetData.stream()
                .filter(budgetDocument -> budgetDocument.getUser().getUuid().equals(user.getUuid()) && budgetDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(budgetDocument -> budgetDocument.getBudgetHours() * budgetDocument.getRate()).sum();
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
                .mapToDouble(BudgetDocument::getBudgetHours).sum();
    }

    public AvailabilityDocument getConsultantAvailabilityByMonth(User user, LocalDate month) {
        List<AvailabilityDocument> availabilityData = getAvailabilityData();
        return availabilityData.stream()
                .filter(
                        availabilityDocument -> availabilityDocument.getUser().getUuid().equals(user.getUuid()) &&
                                availabilityDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .findAny().orElse(null);
    }

    public double getAllExpensesByMonth(LocalDate month) {
        List<ExpenseDocument> expenseData = getExpenseData();
        return expenseData.stream()
                .filter(expenseDocument -> expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(ExpenseDocument::getExpenseSum).sum();
    }

    public double getSharedExpensesAndStaffSalariesByMonth(LocalDate month) {
        List<ExpenseDocument> expenseData = getExpenseData();
        return expenseData.stream()
                .filter(expenseDocument -> expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .mapToDouble(expenseDocument1 -> (expenseDocument1.getSharedExpense()+expenseDocument1.getStaffSalaries())).sum();
    }

    public ExpenseDocument getConsultantExpensesByMonth(User user, LocalDate month) {
        List<ExpenseDocument> expenceData = getExpenseData();
        return expenceData.stream()
                .filter(
                        expenseDocument -> expenseDocument.getUser().getUuid().equals(user.getUuid())
                                && expenseDocument.getMonth().isEqual(month.withDayOfMonth(1)))
                .findAny().orElse(new ExpenseDocument(month, user, 0.0, 0.0, 0.0));
    }

    public List<ExpenseDocument> getExpensesByMonth(LocalDate month) {
        List<ExpenseDocument> expenceData = getExpenseData();
        return expenceData.stream()
                .filter(expenseDocument -> expenseDocument.getMonth().isEqual(month.withDayOfMonth(1))).collect(Collectors.toList());
    }

}
