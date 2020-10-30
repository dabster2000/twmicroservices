package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.BudgetNew;
import dk.trustworks.invoicewebui.model.GraphKeyValue;
import dk.trustworks.invoicewebui.model.dto.BudgetDocument;
import dk.trustworks.invoicewebui.network.rest.BudgetRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    private final BudgetRestService budgetRestService;

    @Autowired
    public BudgetService(BudgetRestService budgetRestService) {
        this.budgetRestService = budgetRestService;
    }

    public List<GraphKeyValue> getBudgetsByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        return budgetRestService.getBudgetsByPeriod(periodStart, periodEnd);
    }

    public double getConsultantBudgetHoursByMonth(String useruuid, LocalDate month) {
        return budgetRestService.getConsultantBudgetHoursByMonth(useruuid, month).getValue();
    }

    public double getMonthBudget(LocalDate month) {
        return budgetRestService.getMonthBudget(month).getValue();
    }

    public void save(BudgetNew budget) {
        budgetRestService.save(budget);
    }

    public List<BudgetDocument> getConsultantBudgetHoursByMonthDocuments(String useruuid, LocalDate month) {
        return budgetRestService.getConsultantBudgetHoursByMonthDocuments(useruuid, month);
    }

    public List<BudgetNew> findByConsultantAndProject(String projectUuid, String contractConsultantUuid) {
        return budgetRestService.findByConsultantAndProject(projectUuid, contractConsultantUuid);
    }

    public BudgetNew findByMonthAndYearAndContractConsultantAndProjectuuid(int month, int year, String contractconsultantuuid, String projectuuid) {
        return budgetRestService.findByMonthAndYearAndContractConsultantAndProjectuuid(month, year, contractconsultantuuid, projectuuid);
    }

    /*
    public double getUserBudgetInMonth(User user, LocalDate month, boolean withBudget) {
        List<Contract> contracts = contractService.findActiveContractsByDate(month, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
        return 0.0;
    }

     */

}
