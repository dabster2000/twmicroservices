package dk.trustworks.invoicewebui.web.dashboard.cards;

import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.Team;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.*;
import dk.trustworks.invoicewebui.utils.DateUtils;
import dk.trustworks.invoicewebui.utils.NumberConverter;
import dk.trustworks.invoicewebui.utils.NumberUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class DashboardBoxCreator {

    private final ProjectService projectService;

    private final TeamRestService teamService;

    private final UserService userService;

    private final FinanceService financeService;

    private final RevenueService revenueService;

    private final AvailabilityService availabilityService;

    private final StatisticsService statisticsService;

    @Autowired
    public DashboardBoxCreator(ProjectService projectService, TeamRestService teamService, UserService userService, FinanceService financeService, RevenueService revenueService, AvailabilityService availabilityService, StatisticsService statisticsService) {
        this.projectService = projectService;
        this.teamService = teamService;
        this.userService = userService;
        this.financeService = financeService;
        this.revenueService = revenueService;
        this.availabilityService = availabilityService;
        this.statisticsService = statisticsService;
    }

    public TopCardContent getGoodPeopleBox() {
        float goodPeopleNow = userService.findCurrentlyEmployedUsers(true).size();//.getActiveEmployeeCountByMonth(LocalDate.now()); //userService.findEmployedUsersByDate(LocalDate.now(), ConsultantType.CONSULTANT, ConsultantType.STAFF, ConsultantType.STUDENT).size();
        float goodPeopleLastYear = userService.findEmployedUsersByDate(LocalDate.now().minusYears(1), true, ConsultantType.CONSULTANT, ConsultantType.STAFF, ConsultantType.STUDENT).size();//userService.findEmployedUsersByDate(LocalDate.now().minusYears(1), ConsultantType.CONSULTANT, ConsultantType.STAFF, ConsultantType.STUDENT).size();
        int percent = Math.round((goodPeopleNow / goodPeopleLastYear) * 100) - 100;
        return new TopCardContent("images/icons/trustworks_icon_kollega.svg", "Good People", percent + "% more than last year", Math.round(goodPeopleNow)+"", "dark-blue");
    }

    public TopCardContent getGoodTeamPeopleBox(String teamuuid) {
        int goodPeopleNow = teamService.getUsersByTeamByMonth(teamuuid, LocalDate.now()).size();
        LocalDate currentFiscalStartDate = DateUtils.getCurrentFiscalStartDate();
        LocalDate endDate = LocalDate.now();
        float avgGoodPeople = teamService.getAvgGoodPeopleByPeriod(teamuuid, currentFiscalStartDate, endDate);

        return new TopCardContent("images/icons/trustworks_icon_kollega.svg", "Good People", avgGoodPeople + " Avg Good People", goodPeopleNow+"", "dark-blue");
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

    public TopCardContent getTotalTeamProfits(Team team) {
        LocalDate periodStart = DateUtils.getCurrentFiscalStartDate();

        double allTeamsProfits = revenueService.getTotalTeamProfits(periodStart.getYear(), teamService.getAllTeams().stream().filter(Team::isTeamleadbonus).collect(Collectors.toList())).getValue();
        double yourTeamProfits = revenueService.getTotalTeamProfits(periodStart.getYear(), Collections.singletonList(team)).getValue();

        return new TopCardContent("images/icons/trustworks_icon_finans.svg", "Teams Profit",  "Your Team "+NumberConverter.formatCurrency(NumberUtils.round(yourTeamProfits, 0)), NumberConverter.formatCurrency(NumberUtils.round(allTeamsProfits, 0))+"", "dark-blue");
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
        double resultThisYear = statisticsService.getAverageTeamAllocationByPeriod(startDate, startDate.plusYears(1), null);
        double resultPreviousYear = statisticsService.getAverageTeamAllocationByPeriod(startDate.minusYears(1), startDate, null);
        double percent = NumberUtils.round(((resultThisYear / resultPreviousYear) * 100) - 100, 2);
        return new TopCardContent("images/icons/trustworks_icon_ydeevne.svg", "Average utilization",  percent>0?(percent+"% more than last year"):(-percent+"% less than last year"), NumberUtils.round(resultThisYear * 100,0)+"", "dark-blue");
    }

    public TopCardContent getTeamAllocationBox(Team team) {
        LocalDate startDate = DateUtils.getCurrentFiscalStartDate();
        double resultThisYear = statisticsService.getAverageTeamAllocationByPeriod(startDate, startDate.plusYears(1), team);
        double resultPreviousYear = statisticsService.getAverageTeamAllocationByPeriod(startDate.minusYears(1), startDate, team);

        double percent = NumberUtils.round(((resultThisYear / resultPreviousYear) * 100) - 100, 2);

        return new TopCardContent("images/icons/trustworks_icon_ydeevne.svg", "Average utilization",  percent>0?(percent+"% more than last year"):(-percent+"% less than last year"), NumberUtils.round(resultThisYear * 100,0)+"", "dark-blue");
    }

    @Cacheable("activeprojects")
    public TopCardContent createActiveProjectsBox() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        LocalDate lastStartDate = startDate.minusYears(1);
        LocalDate lastEndDate = endDate.minusYears(1);

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

        double percentBillableHours = Math.abs(Math.round((hoursSumAvg / lastHoursSumAvg) * 100) - 100);
        String hoursMoreOrLess = "more";
        if(percentBillableHours < 0) hoursMoreOrLess = "less";

        return new TopCardContent("images/icons/trustworks_icon_ur.svg", "Avg Hours per Consultant", percentBillableHours+"% "+hoursMoreOrLess+" than last year", ""+Math.round(hoursSumAvg), "dark-blue");
    }

    @Cacheable("consultantsperproject")
    public TopCardContent createConsultantsPerProjectBox() {

        return new TopCardContent("images/icons/trustworks_icon_gruppe.svg", "Consultants per Project", "N/A", "", "dark-blue");
    }

}
