package dk.trustworks.invoicewebui.web.model.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpenseItem {

    private final double guid;
    private final String name;
    private final double[] expenses;
    private final List<ExpenseItem> expenseItems = new ArrayList<>();

    public ExpenseItem(double guid, String name, double[] expenses) {
        this.guid = guid;
        this.name = name;
        this.expenses = expenses;
    }

    public double getGuid() {
        return guid;
    }

    public String getName() {
        return name;
    }

    public double[] getExpenses() {
        return expenses;
    }

    public List<ExpenseItem> getExpenseItems() {
        return expenseItems;
    }

    @Override
    public String toString() {
        return "ExpenseItem{" +
                "guid=" + guid +
                ", name='" + name + '\'' +
                ", expenses=" + Arrays.toString(expenses) +
                ", expenseItems=" + expenseItems +
                '}';
    }
}
