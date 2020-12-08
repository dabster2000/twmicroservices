package dk.trustworks.invoicewebui.web.dashboard.cards;

import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.AvailabilityDocument;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.services.*;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class DashboardBoxCreator {

    private final ProjectService projectService;

    private final StatisticsService statisticsService;

    private final UserService userService;

    private final FinanceService financeService;

    private final RevenueService revenueService;

    private final AvailabilityService availabilityService;

    @Autowired
    public DashboardBoxCreator(ProjectService projectService, StatisticsService statisticsService, UserService userService, FinanceService financeService, RevenueService revenueService, AvailabilityService availabilityService) {
        this.projectService = projectService;
        this.statisticsService = statisticsService;
        this.userService = userService;
        this.financeService = financeService;
        this.revenueService = revenueService;
        this.availabilityService = availabilityService;
    }

    public TopCardContent getGoodPeopleBox() {
        float goodPeopleNow = userService.findCurrentlyEmployedUsers(true).size();//.getActiveEmployeeCountByMonth(LocalDate.now()); //userService.findEmployedUsersByDate(LocalDate.now(), ConsultantType.CONSULTANT, ConsultantType.STAFF, ConsultantType.STUDENT).size();
        float goodPeopleLastYear = userService.findEmployedUsersByDate(LocalDate.now().minusYears(1), true, ConsultantType.CONSULTANT, ConsultantType.STAFF, ConsultantType.STUDENT).size();//userService.findEmployedUsersByDate(LocalDate.now().minusYears(1), ConsultantType.CONSULTANT, ConsultantType.STAFF, ConsultantType.STUDENT).size();
        int percent = Math.round((goodPeopleNow / goodPeopleLastYear) * 100) - 100;
        return new TopCardContent("images/icons/trustworks_icon_kollega.svg", "Good People", percent + "% more than last year", Math.round(goodPeopleNow)+"", "dark-blue");
    }

    public TopCardContent getCumulativeGrossRevenue() {
        double cumulativeRevenuePerMonth = 0.0;
        double cumulativeExpensePerMonth = 0.0;

        SimpleRegression regression = new SimpleRegression();

        LocalDate periodStart = DateUtils.getCurrentFiscalStartDate();

        for (int i = 0; i < 12; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            if(!currentDate.isBefore(LocalDate.now().withDayOfMonth(1))) break;

            double revenueByMonth = revenueService.getInvoicedRevenueForSingleMonth(currentDate);
            cumulativeRevenuePerMonth += revenueByMonth;
            double expense = financeService.calcAllExpensesByMonth(periodStart.plusMonths(i).withDayOfMonth(1));
            cumulativeExpensePerMonth += expense;

            regression.addData(i+1, cumulativeRevenuePerMonth-cumulativeExpensePerMonth);
        }

        return new TopCardContent("images/icons/trustworks_icon_finans.svg", "Gross Profit",  "Forecast is "+NumberConverter.formatCurrency(Math.round(regression.predict(12))), NumberConverter.formatCurrency(NumberUtils.round(cumulativeRevenuePerMonth-cumulativeExpensePerMonth, 0))+"", "dark-blue");
    }

    public TopCardContent getPayout() {
        SimpleRegression regression = new SimpleRegression();
        GraphKeyValue[] payouts = financeService.getPayoutsByPeriod(DateUtils.getCurrentFiscalStartDate(), LocalDate.now().withDayOfMonth(1));

        LocalDate periodStart = DateUtils.getCurrentFiscalStartDate();
        double payout = 0.0;
        for (int i = 0; i < 12; i++) {
            LocalDate currentDate = periodStart.plusMonths(i);
            if (!currentDate.isBefore(LocalDate.now().withDayOfMonth(1))) break;
            regression.addData(i+1, payouts[i].getValue());
            payout = payouts[i].getValue();
        }

        return new TopCardContent("images/icons/trustworks_icon_data.svg", "Employee bonus",  "Forecast is "+NumberUtils.round(regression.predict(12), 2), NumberUtils.round(payout,2)+"%", "dark-blue");
    }

    public TopCardContent getUserAllocationBox() {
        LocalDate startDate = DateUtils.getCurrentFiscalStartDate();
        double resultThisYear = getAverageAllocationByYear(startDate);
        double resultPreviousYear = getAverageAllocationByYear(startDate.minusYears(1));

        double percent = NumberUtils.round(((resultThisYear / resultPreviousYear) * 100) - 100, 0);

        return new TopCardContent("images/icons/trustworks_icon_ydeevne.svg", "Average utilization",  percent>0?(percent+"% more than last year"):(-percent+"% less than last year"), resultThisYear+"", "dark-blue");
    }

    private double getAverageAllocationByYear(LocalDate startDate) {
        double allocation = 0.0;
        double count = 0.0;

        List<AvailabilityDocument> availabilityDocuments = availabilityService.getConsultantAvailabilityByPeriod(startDate, LocalDate.now());

        do {
            startDate = startDate.plusMonths(1);
            List<GraphKeyValue> registeredHoursPerConsultant = revenueService.getRegisteredHoursPerConsultantForSingleMonth(startDate);

            for (User user : userService.findEmployedUsersByDate(startDate, true, ConsultantType.CONSULTANT)) {
                if(user.getUsername().equals("hans.lassen") || user.getUsername().equals("tobias.kjoelsen") || user.getUsername().equals("lars.albert") || user.getUsername().equals("thomas.gammelvind")) continue;

                LocalDate finalStartDate = startDate;

                double billableWorkHours = registeredHoursPerConsultant.stream().filter(g ->
                        g.getUuid().equals(user.getUuid()) && g.getDescription().equals(DateUtils.stringIt(finalStartDate)))
                        .mapToDouble(GraphKeyValue::getValue).sum();
                //double billableWorkHours = registeredHours.map(GraphKeyValue::getValue).orElse(0.0);

                //double billableWorkHours = revenueService.getRegisteredHoursForSingleMonthAndSingleConsultant(user.getUuid(), startDate);
                //AvailabilityDocument availability = availabilityService.getConsultantAvailabilityByMonth(user.getUuid(), startDate);
                AvailabilityDocument availability = availabilityDocuments.stream().filter(ad ->
                        ad.getMonth().isEqual(finalStartDate) && ad.getUser().getUuid().equals(user.getUuid())).findAny().orElse(
                        new AvailabilityDocument(user, startDate, 0.0, 0.0, 0.0, 0.0, ConsultantType.CONSULTANT, StatusType.TERMINATED)
                );
                double monthAllocation = 0.0;
                if (billableWorkHours > 0.0 && availability.getNetAvailableHours() > 0.0) {
                    monthAllocation = (billableWorkHours / availability.getNetAvailableHours()) * 100.0;
                    count++;
                }
                allocation += monthAllocation;
            }

        } while (startDate.isBefore(LocalDate.now()));
        return NumberUtils.round(allocation / count, 0);
    }

    @Cacheable("activeprojects")
    public TopCardContent createActiveProjectsBox() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        LocalDate lastStartDate = startDate.minusYears(1);
        LocalDate lastEndDate = endDate.minusYears(1);
        /*
        Map<String, Project> currentProjectSet = new HashMap<>();
        for (Work work : workService.findByPeriod(startDate, endDate)) {
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate != null && rate > 0 && work.getWorkduration() > 0) {
                currentProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            }
        }

        Map<String, Project> lastProjectSet = new HashMap<>();
        for (Work work : workService.findByPeriod(lastStartDate, lastEndDate)) {
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate != null && rate > 0 && work.getWorkduration() > 0) {
                lastProjectSet.put(work.getTask().getProject().getUuid(), work.getTask().getProject());
            }
        }
         */

        float projectsThisYear = projectService.findByWorkonCount(startDate, endDate);
        int projectsLastYear = projectService.findByWorkonCount(lastStartDate, lastEndDate);
        int percentProjects = Math.round((projectsThisYear / projectsLastYear) * 100) - 100;
        String projectsMoreOrLess = "more";
        if(percentProjects < 0) projectsMoreOrLess = "less";
        percentProjects = Math.abs(percentProjects);
        return new TopCardContent("images/icons/trustworks_icon_kalender.svg", "Active Projects", percentProjects+"% "+projectsMoreOrLess+" than last year", ""+projectsThisYear, "dark-blue");
    }

    @Cacheable("billablehours")
    public TopCardContent createBillableHoursBox() {
        /*
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        LocalDate lastStartDate = startDate.minusYears(1);
        LocalDate lastEndDate = endDate.minusYears(1);
         */

        LocalDate startDate = DateUtils.getCurrentFiscalStartDate();
        LocalDate endDate = LocalDate.now();
        LocalDate lastStartDate = startDate.minusYears(1);

        double hoursSumAvg = 0;
        double lastHoursSumAvg = 0;

        do {
            double consultants = availabilityService.countActiveConsultantsByMonth(startDate);
            double lastConsultants = availabilityService.countActiveConsultantsByMonth(lastStartDate);

            double hoursByMonth = revenueService.getRegisteredHoursForSingleMonth(startDate);
            double lastHoursByMonth = revenueService.getRegisteredHoursForSingleMonth(lastStartDate);

            hoursSumAvg += (hoursByMonth / consultants);
            lastHoursSumAvg += (lastHoursByMonth / lastConsultants);

            startDate = startDate.plusMonths(1);
            lastStartDate = lastStartDate.plusMonths(1);
        } while (startDate.isBefore(endDate));


/*
        float billableHoursThisYear = 0f;
        for (Work work : workService.findByPeriod(startDate, endDate)) {
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate != null && rate > 0 && work.getWorkduration() > 0) {
                billableHoursThisYear += work.getWorkduration();
            }
        }

        float billableHoursLastYear = 0f;
        for (Work work : workService.findByPeriod(lastStartDate, lastEndDate)) {
            Double rate = contractService.findConsultantRateByWork(work, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
            if(rate != null && rate > 0 && work.getWorkduration() > 0) {
                billableHoursLastYear += work.getWorkduration();
            }
        }
         */

        double percentBillableHours = Math.abs(Math.round((hoursSumAvg / lastHoursSumAvg) * 100) - 100);
        String hoursMoreOrLess = "more";
        if(percentBillableHours < 0) hoursMoreOrLess = "less";

        return new TopCardContent("images/icons/trustworks_icon_ur.svg", "Avg Hours per Consultant", percentBillableHours+"% "+hoursMoreOrLess+" than last year", ""+Math.round(hoursSumAvg), "dark-blue");
    }

    @Cacheable("consultantsperproject")
    public TopCardContent createConsultantsPerProjectBox() {
        LocalDate startDate = LocalDate.now().minusYears(1);
        LocalDate endDate = LocalDate.now();
        LocalDate lastStartDate = startDate.minusYears(1);
        LocalDate lastEndDate = endDate.minusYears(1);
/*
        List<GraphKeyValue> amountPerItemList = graphKeyValueRepository.countConsultantsPerProject(startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        double numberOfConsultants = 0;
        for (GraphKeyValue graphKeyValue : amountPerItemList) {
            numberOfConsultants += graphKeyValue.getValue();
        }
        double numberOfConsultantsPerProject = (double) Math.round((numberOfConsultants / amountPerItemList.size()) * 100) / 100;

        List<GraphKeyValue> amountPerItemListOld = graphKeyValueRepository.countConsultantsPerProject(lastStartDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), lastEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        double numberOfConsultantsOld = 0;
        for (GraphKeyValue graphKeyValue : amountPerItemListOld) {
            numberOfConsultantsOld += graphKeyValue.getValue();
        }
        double numberOfConsultantsPerProjectOld = (double) Math.round((numberOfConsultantsOld / amountPerItemListOld.size()) * 100) / 100;

        String numberOfConsultantsMoreOrLess = "more";
        if(numberOfConsultantsPerProject < numberOfConsultantsPerProjectOld) numberOfConsultantsMoreOrLess = "less";

        double percentNumberOfConsultantsPerProject = Math.abs(Math.round((numberOfConsultantsPerProject / numberOfConsultantsPerProjectOld) * 100) - 100);


 */
        /*
        TopCardDesign consultantsCard4 = new TopCardDesign();
        consultantsCard4.getImgIcon().setSource(new ThemeResource("images/icons/ic_people_black_48dp_2x.png"));
        consultantsCard4.getLblNumber().setValue(""+numberOfConsultantsPerProject);
        consultantsCard4.getLblTitle().setValue("Consultants per Project");
        consultantsCard4.getLblSubtitle().setValue(percentNumberOfConsultantsPerProject+"% "+numberOfConsultantsMoreOrLess+" than last year");
        consultantsCard4.getCardHolder().addStyleName("dark-grey");
        */
        return new TopCardContent("images/icons/trustworks_icon_gruppe.svg", "Consultants per Project", "N/A", "", "dark-blue");
    }

}
