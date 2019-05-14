package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.model.User;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@NonNull
public class ExpenseDocument {

    private final LocalDate month;
    private final User user;
    private final double sharedExpense;
    private final double salary;
    private final double staffSalaries;

    public double getExpenseSum() {
        return sharedExpense + salary + staffSalaries;
    }
}
