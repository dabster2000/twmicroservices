package dk.trustworks.invoicewebui.model;

import java.time.LocalDate;

public class ExpenseDetails {

    private int entrynumber;

    private int accountnumber;

    private int invoicenumber;

    private double amount;

    private LocalDate expensedate;

    private String text;

    public ExpenseDetails() {
    }

    public ExpenseDetails(int entrynumber, int accountnumber, int invoicenumber, double amount, LocalDate expensedate, String text) {
        this.invoicenumber = invoicenumber;
        this.entrynumber = entrynumber;
        this.accountnumber = accountnumber;
        this.amount = amount;
        this.expensedate = expensedate;
        this.text = text;
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

    public int getInvoicenumber() {
        return invoicenumber;
    }

    public void setInvoicenumber(int invoicenumber) {
        this.invoicenumber = invoicenumber;
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
                ", entrynumber=" + entrynumber +
                ", accountnumber=" + accountnumber +
                ", amount=" + amount +
                ", expensedate=" + expensedate +
                ", text='" + text + '\'' +
                '}';
    }
}
