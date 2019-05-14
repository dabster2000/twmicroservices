package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    private final ContractService contractService;

    @Autowired
    public BudgetService(ContractService contractService) {
        this.contractService = contractService;
    }

    public double getUserBudgetInMonth(User user, LocalDate month, boolean withBudget) {
        List<Contract> contracts = contractService.findActiveContractsByDate(month, ContractStatus.BUDGET, ContractStatus.TIME, ContractStatus.SIGNED, ContractStatus.CLOSED);
        return 0.0;
    }
}
