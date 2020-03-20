package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.model.User;

import java.time.LocalDate;


public class UserExpenseDocument {

    private final LocalDate month;
    private final User user;
    private final double sharedExpense;
    private final double salary;
    private final double staffSalaries;

    public UserExpenseDocument(LocalDate month, User user, double sharedExpense, double salary, double staffSalaries) {
        this.month = month;
        this.user = user;
        this.sharedExpense = sharedExpense;
        this.salary = salary;
        this.staffSalaries = staffSalaries;
    }

    public LocalDate getMonth() {
        return month;
    }

    public User getUser() {
        return user;
    }

    public double getSharedExpense() {
        return sharedExpense;
    }

    public double getSalary() {
        return salary;
    }

    public double getStaffSalaries() {
        return staffSalaries;
    }

    public double getExpenseSum() {
        return sharedExpense + salary + staffSalaries;
    }

    @Override
    public String toString() {
        return "UserExpenseDocument{" +
                "month=" + month +
                ", user=" + user +
                ", sharedExpense=" + sharedExpense +
                ", salary=" + salary +
                ", staffSalaries=" + staffSalaries +
                '}';
    }
}
