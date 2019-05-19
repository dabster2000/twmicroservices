package dk.trustworks.invoicewebui.web.model.stats;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExpenseItem {

    private final double guid;
    private final String name;
    private final double[] expenses;
    private final List<ExpenseItem> expenseItems = new ArrayList<>();

}
