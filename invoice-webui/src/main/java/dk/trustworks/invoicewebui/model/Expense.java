package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    private String uuid;

    private LocalDate period;
    @Enumerated(EnumType.STRING)
    private ExcelExpenseType expensetype;
    private double amount;

    public Expense() {
    }

    public Expense(LocalDate period, ExcelExpenseType expensetype, double amount) {
        this.uuid = UUID.randomUUID().toString();
        this.period = period;
        this.expensetype = expensetype;
        this.amount = amount;
    }

    public String getUuid() {
        return uuid;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public ExcelExpenseType getExpensetype() {
        return expensetype;
    }

    public void setExpensetype(ExcelExpenseType expensetype) {
        this.expensetype = expensetype;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "uuid='" + uuid + '\'' +
                ", period=" + period +
                ", expensetype=" + expensetype +
                ", amount=" + amount +
                '}';
    }
}
