package dk.trustworks.invoicewebui.web.model.stats;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BudgetItem {

    private final String name;
    private final List<Double> budgetHours;
    private final List<Double> budgetAmount;
    private final List<BudgetItem> clients = new ArrayList<>();

}
