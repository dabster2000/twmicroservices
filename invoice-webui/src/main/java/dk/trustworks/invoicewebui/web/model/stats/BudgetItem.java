package dk.trustworks.invoicewebui.web.model.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BudgetItem {

    private final double guid;
    private boolean showHours = true;
    private final String name;
    private final double[] budgetHours;
    private final double[] budgetAmount;
    private final List<BudgetItem> clients = new ArrayList<>();

    public BudgetItem(double guid, String name, double[] budgetHours, double[] budgetAmount) {
        this.guid = guid;
        this.showHours = showHours;
        this.name = name;
        this.budgetHours = budgetHours;
        this.budgetAmount = budgetAmount;
    }

    public double getGuid() {
        return guid;
    }

    public boolean isShowHours() {
        return showHours;
    }

    public void setShowHours(boolean showHours) {
        this.showHours = showHours;
    }

    public String getName() {
        return name;
    }

    public double[] getBudgetHours() {
        return budgetHours;
    }

    public double[] getBudgetAmount() {
        return budgetAmount;
    }

    public List<BudgetItem> getClients() {
        return clients;
    }

    public double[] getValueArray() {
        if(showHours) return budgetHours;
        else return budgetAmount;
    }

    public BudgetItem changeValue() {
        showHours = !showHours;
        return this;
    }

    @Override
    public String toString() {
        return "BudgetItem{" +
                "guid=" + guid +
                ", showHours=" + showHours +
                ", name='" + name + '\'' +
                ", budgetHours=" + Arrays.toString(budgetHours) +
                ", budgetAmount=" + Arrays.toString(budgetAmount) +
                ", clients=" + clients +
                '}';
    }
}
