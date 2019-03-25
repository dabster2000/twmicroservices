package dk.trustworks.invoicewebui.services;

import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsArea;
import com.vaadin.addon.charts.model.style.SolidColor;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.UserBooking;
import dk.trustworks.invoicewebui.model.dto.UserProjectBooking;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

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

    @Cacheable("calcRevenuePerMonth")
    public DataSeries calcRevenuePerMonth(LocalDate periodStart, LocalDate periodEnd) {
        DataSeries revenueSeries = new DataSeries("Revenue");
        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.findRevenueByMonthByPeriod(periodStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")))).collect(Collectors.toList());
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            double invoicedAmountByMonth = invoiceService.invoicedAmountByMonth(currentDate);
            if(invoicedAmountByMonth > 0.0) {
                revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), invoicedAmountByMonth));
            } else {
                if(amountPerItemList.size() <= i) continue;
                GraphKeyValue amountPerItem = amountPerItemList.get(i);
                if(amountPerItem!=null) revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), amountPerItem.getValue()));
            }
        }
        return revenueSeries;
    }

    @Cacheable("calcBillableHoursRevenuePerMonth")
    public DataSeries calcBillableHoursRevenuePerMonth(LocalDate periodStart, LocalDate periodEnd) {
        DataSeries revenueSeries = new DataSeries("Billable Hours Revenue");

        PlotOptionsArea plotOptionsArea = new PlotOptionsArea();
        plotOptionsArea.setColor(new SolidColor(84, 214, 158));
        revenueSeries.setPlotOptions(plotOptionsArea);

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.findRevenueByMonthByPeriod(periodStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), periodEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        amountPerItemList = amountPerItemList.stream().sorted(Comparator.comparing(o -> LocalDate.parse(o.getDescription(), DateTimeFormatter.ofPattern("yyyy-M-dd")))).collect(Collectors.toList());
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            if(amountPerItemList.size() <= i) continue;
            GraphKeyValue amountPerItem = amountPerItemList.get(i);
            if(amountPerItem!=null) revenueSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), amountPerItem.getValue()));
        }
        return revenueSeries;
    }

    @Cacheable("calcBudgetPerMonth")
    public DataSeries calcBudgetPerMonth(LocalDate periodStart, LocalDate periodEnd) {
        DataSeries budgetSeries = new DataSeries("Budget");

        PlotOptionsArea plotOptionsArea = new PlotOptionsArea();
        plotOptionsArea.setColor(new SolidColor(18, 51, 117));
        budgetSeries.setPlotOptions(plotOptionsArea);

        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            List<Contract> contracts = contractService.findActiveContractsByDate(currentDate, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            double budgetSum = 0.0;
            for (Contract contract : contracts) {
                if(contract.getContractType().equals(ContractType.PERIOD)) {
                    for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
                        double weeks = workService.getWorkDaysInMonth(contractConsultant.getUser().getUuid(), currentDate) / 5.0;
                        List<Work> workList = workService.findByPeriodAndUserUUID(
                                currentDate.withDayOfMonth(1),
                                currentDate.withDayOfMonth(currentDate.lengthOfMonth()),
                                contractConsultant.getUser().getUuid());
                        double notWork = 0.0;
                        for (Work work : workList) {
                            if(work.getTask().getUuid().equals("02bf71c5-f588-46cf-9695-5864020eb1c4") ||
                                    work.getTask().getUuid().equals("f585f46f-19c1-4a3a-9ebd-1a4f21007282")) notWork += work.getWorkduration();
                        }
                        budgetSum += ((contractConsultant.getHours() * weeks) - notWork) * contractConsultant.getRate();
                    }
                }
            }
            List<BudgetNew> budgets = budgetNewRepository.findByMonthAndYear(currentDate.getMonthValue() - 1, currentDate.getYear());
            for (BudgetNew budget : budgets) {
                budgetSum += budget.getBudget();
            }

            budgetSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(budgetSum)));
        }

        return budgetSeries;
    }

    @Cacheable("calcEarningsPerMonth")
    public DataSeries calcEarningsPerMonth(LocalDate periodStart, LocalDate periodEnd) {
        DataSeries earningsSeries = new DataSeries("Earnings");

        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            double invoicedAmountByMonth = invoiceService.invoicedAmountByMonth(currentDate);
            System.out.println("invoicedAmountByMonth = " + invoicedAmountByMonth);
            double expense = expenseRepository.findByPeriod(currentDate.withDayOfMonth(1)).stream().mapToDouble(Expense::getAmount).sum();
            earningsSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), invoicedAmountByMonth-expense));
        }
        return earningsSeries;
    }

    public List<UserBooking> getUserBooking(int monthsInFuture, int monthsInPast) {
        List<UserBooking> userBookings = new ArrayList<>();
        Map<String, UserProjectBooking> userProjectBookingMap = new HashMap<>();
        LocalDate currentDate;
        for (User user : userService.findCurrentlyWorkingEmployees(ConsultantType.CONSULTANT)) {
            currentDate = LocalDate.now().withDayOfMonth(1).minusMonths(monthsInPast);
            UserBooking userBooking = new UserBooking(user.getUsername(),user.getUuid(), monthsInFuture, true);
            userBookings.add(userBooking);

            boolean debug = user.getUsername().equals("hans.lassen");

            for (int i = 0; i < monthsInFuture; i++) {
                List<Contract> contracts = contractService.findActiveContractsByDate(currentDate, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
                for (Contract contract : contracts) {
                    if(debug) System.out.println("contract = " + contract);
                    if(contract.getContractType().equals(ContractType.PERIOD)) {
                        for (ContractConsultant contractConsultant : contract.getContractConsultants().stream().filter(c -> c.getUser().getUsername().equals(user.getUsername())).collect(Collectors.toList())) {
                            if(debug) System.out.println("contractConsultant = " + contractConsultant);
                            String key = contractConsultant.getUser().getUuid()+contractConsultant.getContract().getClient().getUuid();
                            if(!userProjectBookingMap.containsKey(key)) {
                                UserProjectBooking newUserProjectBooking = new UserProjectBooking(contractConsultant.getContract().getClient().getName(), contractConsultant.getContract().getClient().getUuid(), monthsInFuture, false);
                                userProjectBookingMap.put(key, newUserProjectBooking);
                                userBooking.addSubProject(newUserProjectBooking);
                            }
                            UserProjectBooking userProjectBooking = userProjectBookingMap.get(key);

                            double workDaysInMonth = workService.getWorkDaysInMonth(contractConsultant.getUser().getUuid(), currentDate);
                            if(debug) System.out.println("workDaysInMonth = " + workDaysInMonth);
                            double weeks = (workDaysInMonth / 5.0);
                            if(debug) System.out.println("weeks = " + weeks);
                            double preBooking = 0.0;
                            double budget = 0.0;
                            double booking;
                            if(i < monthsInPast) {
                                if(debug) System.out.println("PAST");
                                budget = NumberUtils.round((contractConsultant.getHours() * weeks), 2);
                                if(debug) System.out.println("budget = " + budget);
                                //preBooking = Optional.ofNullable(workService.findHoursRegisteredOnContractByPeriod(contract.getUuid(), user.getUuid(), DateUtils.getFirstDayOfMonth(currentDate), DateUtils.getLastDayOfMonth(currentDate))).orElse(0.0);
                                Double preBookingObj = workService.findHoursRegisteredOnContractByPeriod(contract.getUuid(), user.getUuid(), DateUtils.getFirstDayOfMonth(currentDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), DateUtils.getLastDayOfMonth(currentDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                                if(debug) System.out.println("preBookingObj = " + preBookingObj);
                                if(preBookingObj != null) preBooking = preBookingObj;
                                if(debug) System.out.println("preBooking = " + preBooking);
                                if(debug) System.out.println("contract.getUuid() = " + contract.getUuid());
                                if(debug) System.out.println("user.getUuid() = " + user.getUuid());
                                if(debug) System.out.println("DateUtils.getFirstDayOfMonth(currentDate) = " + DateUtils.getFirstDayOfMonth(currentDate));
                                if(debug) System.out.println("DateUtils.getLastDayOfMonth(currentDate) = " + DateUtils.getLastDayOfMonth(currentDate));
                                if(debug) System.out.println("preBooking = " + preBooking);
                                booking = NumberUtils.round((preBooking / budget) * 100.0, 2);
                                if(debug) System.out.println("booking = " + booking);
                            } else {
                                if(debug) System.out.println("FUTURE");
                                if (contract.getStatus().equals(ContractStatus.BUDGET)) {
                                    if(debug) System.out.println("BUDGET");
                                    preBooking = NumberUtils.round((contractConsultant.getHours() * weeks), 2);
                                    if(debug) System.out.println("preBooking = " + preBooking);
                                } else {
                                    if(debug) System.out.println("TIME");
                                    budget = NumberUtils.round((contractConsultant.getHours() * weeks), 2);
                                    if(debug) System.out.println("budget = " + budget);
                                }
                                booking = NumberUtils.round((budget / (workDaysInMonth * 7)) * 100.0, 2);
                                if(debug) System.out.println("booking = " + booking);
                            }

                            userProjectBooking.setAmountItemsPerProjects(budget, i);
                            userProjectBooking.setAmountItemsPerPrebooking(preBooking, i);
                            userProjectBooking.setBookingPercentage(booking, i);
                            //userProjectBooking.setMonthNorm(NumberUtils.round(workDaysInMonth * 7, 2), i);
                            if(debug) System.out.println("(workDaysInMonth * 7) = " + (workDaysInMonth * 7));
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
                        preBooking = Optional.ofNullable(workService.findHoursRegisteredOnContractByPeriod(budget.getContractConsultant().getContract().getUuid(), budget.getContractConsultant().getUser().getUuid(), DateUtils.getFirstDayOfMonth(currentDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), DateUtils.getLastDayOfMonth(currentDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).orElse(0.0);
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

        System.out.println("SUM PROJECTS");
        for(UserBooking userBooking : userBookings) {
            if(userBooking.getSubProjects().size() == 0) continue;
            boolean debug = (userBooking.getUsername().equals("hans.lassen"));
            for (UserBooking subProject : userBooking.getSubProjects()) {
                currentDate = LocalDate.now().withDayOfMonth(1).minusMonths(monthsInPast);
                for (int i = 0; i < monthsInFuture; i++) {
                    if(debug) System.out.println("i = " + i);
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
