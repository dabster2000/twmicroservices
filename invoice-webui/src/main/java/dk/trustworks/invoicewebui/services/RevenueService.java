package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.Team;
import dk.trustworks.invoicewebui.network.rest.RevenueRestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RevenueService implements InitializingBean {

    private static RevenueService instance;

    private final RevenueRestService revenueRestService;

    @Autowired
    public RevenueService(RevenueRestService revenueRestService) {
        this.revenueRestService = revenueRestService;
    }

    public List<GraphKeyValue> getSumOfRegisteredRevenueByClient() {
        return revenueRestService.getSumOfRegisteredRevenueByClient();
    }

    public List<GraphKeyValue> getSumOfRegisteredRevenueByClientByYear(int fiscalYear) {
        return revenueRestService.getSumOfRegisteredRevenueByClientByFiscalYear(fiscalYear);
    }

    @Override
    public void afterPropertiesSet() {
        instance = this;
    }

    public static RevenueService get() {
        return instance;
    }

    public double getRegisteredHoursForSingleMonth(LocalDate month) {
        return revenueRestService.getRegisteredHoursForSingleMonth(month).getValue();
    }

    public double getRegisteredRevenueForSingleMonth(LocalDate month) {
        return revenueRestService.getRegisteredRevenueForSingleMonth(month).getValue();
    }

    public List<GraphKeyValue> getRegisteredRevenueByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        return revenueRestService.getRegisteredRevenueByPeriod(periodStart, periodEnd);
    }

    public double getRegisteredRevenueForSingleMonthAndSingleConsultant(String useruuid, LocalDate month) {
        return revenueRestService.getRegisteredRevenueForSingleMonthAndSingleConsultant(useruuid, month).getValue();
    }

    public HashMap<String, Double> getRegisteredRevenueByPeriodAndSingleConsultant(String useruuid, LocalDate periodFrom, LocalDate periodTo) {
        return revenueRestService.getRegisteredRevenueByPeriodAndSingleConsultant(useruuid, periodFrom, periodTo);
    }

    public List<GraphKeyValue> getRegisteredHoursPerConsultantForSingleMonth(LocalDate month) {
        return revenueRestService.getRegisteredHoursPerConsultantForSingleMonth(month);
    }

    public double getRegisteredHoursForSingleMonthAndSingleConsultant(String useruuid, LocalDate month) {
        return revenueRestService.getRegisteredHoursForSingleMonthAndSingleConsultant(useruuid, month).getValue();
    }

    public List<GraphKeyValue> getRegisteredProfitsForSingleConsultant(String useruuid, LocalDate periodStart, LocalDate periodEnd, int interval) {
        return revenueRestService.getRegisteredProfitsForSingleConsultant(useruuid, periodStart, periodEnd, interval);
    }

    public List<GraphKeyValue> getInvoicedOrRegisteredRevenueByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        return revenueRestService.getInvoicedOrRegisteredRevenueByPeriod(periodStart, periodEnd);
    }

    public double getInvoicedRevenueForSingleMonth(LocalDate month) {
        return revenueRestService.getInvoicedRevenueForSingleMonth(month).getValue();
    }

    public List<GraphKeyValue> getProfitsByPeriod(LocalDate fromdate, LocalDate todate) {
        return revenueRestService.getProfitsByPeriod(fromdate, todate);
    }

    public GraphKeyValue getTotalTeamProfits(int fiscalYear, List<Team> teams) {
        return revenueRestService.getTotalTeamProfits(fiscalYear, teams.stream().map(Team::getUuid).collect(Collectors.toList()));
    }

    public GraphKeyValue getTotalTeamProfitsByPeriod(LocalDate fromdate, LocalDate todate, List<Team> teams) {
        return revenueRestService.getTotalTeamProfitsByPeriod(fromdate, todate, teams.stream().map(Team::getUuid).collect(Collectors.toList()));
    }
}