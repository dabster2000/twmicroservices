package dk.trustworks.invoicewebui.model.dto;

import java.time.LocalDate;

public class BudgetDocument {

    private final LocalDate month;
    private final String taskId;
    private final String userId;
    private final double[] budgetHours;
    private final double[] rate;

    public BudgetDocument(LocalDate localDate, String taskId, String userId) {
        this.month = localDate.withDayOfMonth(1);
        this.taskId = taskId;
        this.userId = userId;

        budgetHours = new double[31];
        rate = new double[31];
    }
}
