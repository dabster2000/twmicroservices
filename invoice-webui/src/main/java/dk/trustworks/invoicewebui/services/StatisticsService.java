package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Team;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.dto.EmployeeAggregateData;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.network.rest.TeamRestService;
import dk.trustworks.invoicewebui.services.cached.StatisticsCachedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService extends StatisticsCachedService {

    private final RevenueService revenueService;

    private final BiService biService;

    private final TeamRestService teamService;

    @Autowired
    public StatisticsService(RevenueService revenueService, BiService biService, TeamRestService teamService) {
        this.revenueService = revenueService;
        this.biService = biService;
        this.teamService = teamService;
    }

    public double getInvoicedOrRegisteredRevenueByMonth(LocalDate month) {
        double invoicedAmountByMonth = revenueService.getInvoicedRevenueForSingleMonth(month);
        return (invoicedAmountByMonth > 0.0)?invoicedAmountByMonth:revenueService.getRegisteredRevenueForSingleMonth(month);
    }

    public static String[] getMonthCategories(LocalDate periodStart, LocalDate periodEnd) {
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

    public double getAverageTeamAllocationByPeriod(LocalDate startDate, LocalDate endDate, Team team) {
        double allocation = 0.0;
        double count = 0.0;

        List<EmployeeAggregateData> employeeData = biService.getEmployeeAggregateDataByPeriod(startDate, endDate);
        do {
            if(team!=null) {
                for (User user : teamService.getUsersByTeamByMonth(team.getUuid(), startDate)) {
                    LocalDate finalStartDate1 = startDate;
                    List<EmployeeAggregateData> employeeAggregateData = employeeData.stream().filter(e -> e.getMonth().isEqual(finalStartDate1) && e.getUseruuid().equals(user.getUuid())).collect(Collectors.toList());
                    if(employeeAggregateData.size()>1) {
                        throw new RuntimeException("Too many employeeAggregateData on single month and single consultant");
                    }
                    if(employeeAggregateData.size()>0 && employeeAggregateData.get(0).getStatusType().equals(StatusType.ACTIVE)) {
                        count++;
                        allocation += employeeAggregateData.get(0).getActualUtilization();
                    }
                }
            }
            startDate = startDate.plusMonths(1);
        } while (startDate.isBefore(LocalDate.now().withDayOfMonth(1)));
        return (allocation / count);
    }
}
