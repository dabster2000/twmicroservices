package dk.trustworks.invoicewebui.web.model.stats;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BudgetItem {

    private final double guid;
    private boolean showHours = true;
    private final String name;
    private final double[] budgetHours;
    private final double[] budgetAmount;
    private final List<BudgetItem> clients = new ArrayList<>();

    public double[] getValueArray() {
        if(showHours) return budgetHours;
        else return budgetAmount;
    }

    public BudgetItem changeValue() {
        showHours = !showHours;
        return this;
    }
}
