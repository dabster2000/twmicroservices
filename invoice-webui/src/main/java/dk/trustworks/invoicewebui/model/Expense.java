package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    private String uuid;
    @Temporal(TemporalType.TIMESTAMP)
    private Date period;
    @Enumerated(EnumType.STRING)
    private ExcelExpenseType expensetype;
    private double amount;

    public Expense() {
    }

    public Expense(Date period, ExcelExpenseType expensetype, double amount) {
        this.uuid = UUID.randomUUID().toString();
        this.period = period;
        this.expensetype = expensetype;
        this.amount = amount;
    }

    public String getUuid() {
        return uuid;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
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
