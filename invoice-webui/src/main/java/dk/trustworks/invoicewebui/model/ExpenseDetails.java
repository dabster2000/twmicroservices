package dk.trustworks.invoicewebui.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "expense_details")
public class ExpenseDetails {

    @Id
    @GeneratedValue
    private int id;

    private int entrynumber;

    private int accountnumber;

    private double amount;

    private LocalDate expensedate;

    private String text;

    public ExpenseDetails() {
    }

    public ExpenseDetails(int entrynumber, int accountnumber, double amount, LocalDate expensedate, String text) {
        this.id = id;
        this.entrynumber = entrynumber;
        this.accountnumber = accountnumber;
        this.amount = amount;
        this.expensedate = expensedate;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public int getEntrynumber() {
        return entrynumber;
    }

    public void setEntrynumber(int entrynumber) {
        this.entrynumber = entrynumber;
    }

    public int getAccountnumber() {
        return accountnumber;
    }

    public void setAccountnumber(int accountnumber) {
        this.accountnumber = accountnumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getExpensedate() {
        return expensedate;
    }

    public void setExpensedate(LocalDate expensedate) {
        this.expensedate = expensedate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ExpenseDetailed{" +
                "id=" + id +
                ", entrynumber=" + entrynumber +
                ", accountnumber=" + accountnumber +
                ", amount=" + amount +
                ", expensedate=" + expensedate +
                ", text='" + text + '\'' +
                '}';
    }
}
