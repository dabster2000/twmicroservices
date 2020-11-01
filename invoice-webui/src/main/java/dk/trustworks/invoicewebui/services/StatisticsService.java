package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.services.cached.StatisticsCachedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class StatisticsService extends StatisticsCachedService {

    private final RevenueService revenueService;

    @Autowired
    public StatisticsService(RevenueService revenueService) {
        this.revenueService = revenueService;
    }
/*
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

 */

    public double getInvoicedOrRegisteredRevenueByMonth(LocalDate month) {
        double invoicedAmountByMonth = revenueService.getInvoicedRevenueForSingleMonth(month);
        return (invoicedAmountByMonth > 0.0)?invoicedAmountByMonth:revenueService.getRegisteredRevenueForSingleMonth(month);
    }
/*
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

 */

    /**
     * Calculates actual invoiced revenue per month. Uses registered hours if no invoices exists.
     * @param periodStart
     * @param periodEnd
     * @return
     */
    /*
    public Map<LocalDate, Double> calcActualRevenuePerMonth(LocalDate periodStart, LocalDate periodEnd) {
        Map<LocalDate, Double> result = new HashMap<>();
        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            double invoicedAmountByMonth = getTotalInvoiceSumByMonth(currentDate);
            if(invoicedAmountByMonth > 0.0) {
                result.put(currentDate, invoicedAmountByMonth);
            } else {
                result.put(currentDate, 0.0);
            }
        }
        return result;
    }

     */
/*
    // w.id as uuid, DATE_FORMAT(w.registered, '%Y-%m-%d') as description, ROUND(SUM(w.workduration*cc.rate
    public List<GraphKeyValue> findRevenueByMonthByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        Map<LocalDate, Double> revenuePerMonth = calcActualRevenuePerMonth(periodStart, periodEnd);
        List<GraphKeyValue> result = new ArrayList<>();
        revenuePerMonth.keySet().stream().sorted(LocalDate::compareTo).forEach(localDate -> {
            result.add(new GraphKeyValue(UUID.randomUUID().toString(), stringIt(localDate), revenuePerMonth.get(localDate)));
        });

        return result;
    }

    /**
     * Calculates revenue from registered hours, not actual invoices.
     * @param periodStart
     * @param periodEnd
     * @return
     */
    /*
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

     */
/*
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

 */
/*
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
            double expense = financeService.calcAllExpensesByMonth(currentDate.withDayOfMonth(1));// getAllUserExpensesByMonth(currentDate.withDayOfMonth(1));
            earningsSeries.add(new DataSeriesItem(currentDate.format(DateTimeFormatter.ofPattern("MMM-yyyy")), invoicedAmountByMonth-expense));
        }
        return earningsSeries;
    }

 */
/*
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
                            double workDaysInMonth = availabilityService.getWorkdaysInMonth(contractConsultant.getUser().getUuid(), currentDate);
                            double weeks = (workDaysInMonth / 5.0);
                            double preBooking = 0.0;
                            double budget = 0.0;
                            double booking;
                            if(i < monthsInPast) {
                                budget = NumberUtils.round((contractConsultant.getHours() * weeks), 2);
                                Double preBookingObj = workService.findHoursRegisteredOnContractByPeriod(contract, user.getUuid(), getFirstDayOfMonth(currentDate), getLastDayOfMonth(currentDate));
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

                    double workDaysInMonth = workService.getWorkdaysInMonth(user.getUuid(), currentDate);
                    double preBooking = 0.0;
                    double hourBudget = 0.0;
                    double booking;

                    if(i < monthsInPast) {
                        hourBudget = NumberUtils.round(budget.getBudget() / budget.getContractConsultant().getRate(), 2);
                        preBooking = Optional.ofNullable(workService.findHoursRegisteredOnContractByPeriod(budget.getContractConsultant().getContract(), budget.getContractConsultant().getUser().getUuid(), getFirstDayOfMonth(currentDate), getLastDayOfMonth(currentDate))).orElse(0.0);
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
                    int workDaysInMonth = workService.getWorkdaysInMonth(userService.findByUsername(userBooking.getUsername()).getUuid(), currentDate);
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


    public Map<LocalDate, Double> calculateConsultantRevenue(User user, LocalDate periodStart, LocalDate periodEnd, int interval) {
        int months = (int) ChronoUnit.MONTHS.between(periodStart, periodEnd);
        double revenueSum = 0.0;
        int count = 1;
        Map<LocalDate, Double> resultMap = new HashMap<>();
        List<CompanyExpense> companyExpenseList = financeService.findByAccountAndPeriod(ExcelExpenseType.LØNNINGER, periodStart, periodEnd);
        for (int i = 0; i < months; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);

            if(userService.isActive(user, currentDate, ConsultantType.CONSULTANT)) {
                double consultantCount = userService.findWorkingUsersByDate(currentDate, ConsultantType.CONSULTANT).size();
                double expense = companyExpenseList.stream().filter(e -> e.getPeriod().withDayOfMonth(1).isEqual(currentDate.withDayOfMonth(1))).mapToDouble(CompanyExpense::getAmount).sum() / consultantCount;

                if (expense == 0) {
                    count = 1;
                    revenueSum = 0.0;
                    continue;
                }

                double revenue = getConsultantRevenueByMonth(user, currentDate);
                double userSalary = userService.getUserSalary(user, currentDate);
                double consultantSalaries = userService.calcMonthSalaries(currentDate, ConsultantType.CONSULTANT.toString());
                double partOfTotalSalary = userSalary / consultantSalaries;
                double consultantSalariesSum = getAllExpensesByMonth(currentDate).stream().mapToDouble(ExpenseDocument::geteSalaries).sum();
                double grossUserSalary = consultantSalariesSum * partOfTotalSalary;
                double allExpensesSum = calcAllExpensesByMonth(currentDate);

                revenueSum += revenue - grossUserSalary - ((allExpensesSum - consultantSalaries) / consultantCount);
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

 */

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
