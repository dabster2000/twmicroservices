package dk.trustworks.invoicewebui.services;

import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsAreaspline;
import com.vaadin.addon.charts.model.style.SolidColor;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.dto.UserBooking;
import dk.trustworks.invoicewebui.model.dto.UserProjectBooking;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.repositories.BudgetNewRepository;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.repositories.GraphKeyValueRepository;
import dk.trustworks.invoicewebui.services.cached.StatisticsCachedService;
import dk.trustworks.invoicewebui.utils.NumberUtils;
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

import static dk.trustworks.invoicewebui.utils.DateUtils.*;

@Service
public class StatisticsService extends StatisticsCachedService {

    private final static Logger log = LoggerFactory.getLogger(StatisticsService.class.getName());

    private final GraphKeyValueRepository graphKeyValueRepository;

    private final ContractService contractService;

    private final BudgetNewRepository budgetNewRepository;

    private final ExpenseRepository expenseRepository;

    private final WorkService workService;

    private final UserService userService;

    @Autowired
    public StatisticsService(GraphKeyValueRepository graphKeyValueRepository, ContractService contractService, BudgetNewRepository budgetNewRepository, ExpenseRepository expenseRepository, WorkService workService, InvoiceService invoiceService, UserService userService) {
        super(contractService, expenseRepository, workService, userService, invoiceService);
        this.graphKeyValueRepository = graphKeyValueRepository;
        this.contractService = contractService;
        this.budgetNewRepository = budgetNewRepository;
        this.expenseRepository = expenseRepository;
        this.workService = workService;
        this.userService = userService;
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

    public double getInvoicedOrRegisteredRevenueByMonth(LocalDate month) {
        double invoicedAmountByMonth = getTotalInvoiceSumByMonth(month);
        return (invoicedAmountByMonth > 0.0)?invoicedAmountByMonth:getRegisteredRevenueByMonth(month);
    }

    public Number[] getPayoutsByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        double forecastedExpenses = 43000;
        double forecastedSalaries = 64000;
        double forecastedConsultants = countActiveEmployeeTypesByMonth(periodEnd, ConsultantType.CONSULTANT, ConsultantType.STAFF);
        double totalForecastedExpenses = (forecastedExpenses + forecastedSalaries) * forecastedConsultants;

        double totalCumulativeRevenue = 0.0;
        Number[] payout = new Number[12];

        for (int i = 0; i < 12; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            if(!currentDate.isBefore(periodEnd)) break;

            totalCumulativeRevenue += getMonthRevenue(currentDate);
            double grossMargin = totalCumulativeRevenue - totalForecastedExpenses;
            double grossMarginPerConsultant = grossMargin / forecastedConsultants;
            double consultantPayout = grossMarginPerConsultant * 0.1;
            payout[i] = NumberUtils.round((consultantPayout / forecastedSalaries) * 100.0 - 100.0, 2);
        }
        return payout;
    }

    /**
     * Calculates actual invoiced revenue per month. Uses registered hours if no invoices exists.
     * @param periodStart
     * @param periodEnd
     * @return
     */
    public Map<LocalDate, Double> calcActualRevenuePerMonth(LocalDate periodStart, LocalDate periodEnd) {
        Map<LocalDate, Double> result = new HashMap<>();
        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            double invoicedAmountByMonth = getTotalInvoiceSumByMonth(currentDate);
            if(invoicedAmountByMonth > 0.0) {
                result.put(currentDate, invoicedAmountByMonth);
            } else {
                result.put(currentDate, getMonthRevenue(currentDate));
            }
        }
        return result;
    }

    /**
     * Calculates revenue from registered hours, not actual invoices.
     * @param periodStart
     * @param periodEnd
     * @return
     */
    public DataSeries calcRegisteredHoursRevenuePerMonth(LocalDate periodStart, LocalDate periodEnd) {
        DataSeries revenueSeries = new DataSeries("Registered Hours Revenue");

        PlotOptionsAreaspline plotOptionsArea = new PlotOptionsAreaspline();
        plotOptionsArea.setColor(new SolidColor("#7084AC"));
        revenueSeries.setPlotOptions(plotOptionsArea);

        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            revenueSeries.add(new DataSeriesItem(stringIt(currentDate, "MMM-yyyy"), getMonthRevenue(currentDate)));
        }
        return revenueSeries;
    }

    public DataSeries calcBudgetPerMonth(LocalDate periodStart, LocalDate periodEnd) {
        DataSeries budgetSeries = new DataSeries("Budget Revenue");

        PlotOptionsAreaspline plotOptionsArea = new PlotOptionsAreaspline();
        plotOptionsArea.setColor(new SolidColor("#123375"));
        budgetSeries.setPlotOptions(plotOptionsArea);

        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            budgetSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), Math.round(getMonthBudget(currentDate))));
        }

        return budgetSeries;
    }

    public DataSeries calcEarningsPerMonth(LocalDate periodStart, LocalDate periodEnd) {
        DataSeries earningsSeries = new DataSeries("Gross Profit");

        PlotOptionsAreaspline plotOptionsArea = new PlotOptionsAreaspline();
        plotOptionsArea.setColor(new SolidColor("#54D69E"));
        plotOptionsArea.setNegativeColor(new SolidColor("#FD5F5B"));
        earningsSeries.setPlotOptions(plotOptionsArea);

        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            double invoicedAmountByMonth = getTotalInvoiceSumByMonth(currentDate);
            double expense = getAllUserExpensesByMonth(currentDate.withDayOfMonth(1));
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

    public String[] getMonthCategories(LocalDate periodStart, LocalDate periodEnd) {
        int months = (int)ChronoUnit.MONTHS.between(periodStart, periodEnd);
        String[] categories = new String[months];
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            categories[i] = currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy"));
        }
        return categories;
    }

    public String[] getYearCategories(LocalDate periodStart, LocalDate periodEnd) {
        int years = (int)ChronoUnit.YEARS.between(periodStart, periodEnd);
        String[] categories = new String[years];
        for (int i = 0; i < years; i++) {
            LocalDate currentDate = periodStart.plusYears(i);
            categories[i] = currentDate.format(DateTimeFormatter.ofPattern("yyyy"));
        }
        return categories;
    }
}
