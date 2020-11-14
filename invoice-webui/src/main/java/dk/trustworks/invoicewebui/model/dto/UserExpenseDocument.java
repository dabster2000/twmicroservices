package dk.trustworks.invoicewebui.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.User;

import java.time.LocalDate;


public class UserExpenseDocument {

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate month;
    private final User user;
    private final double sharedExpense;
    private final double salary;
    private final double staffSalaries;
    private final double personaleExpense;

    public UserExpenseDocument(LocalDate month, User user, double sharedExpense, double salary, double staffSalaries, double personaleExpense) {
        this.month = month;
        this.user = user;
        this.sharedExpense = sharedExpense;
        this.salary = salary;
        this.staffSalaries = staffSalaries;
        this.personaleExpense = personaleExpense;
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

    public double getPersonaleExpense() {
        return personaleExpense;
    }

    @Override
    public String toString() {
        return "UserExpenseDocument{" +
                "month=" + month +
                ", user=" + user +
                ", sharedExpense=" + sharedExpense +
                ", salary=" + salary +
                ", staffSalaries=" + staffSalaries +
                ", personaleExpense=" + personaleExpense +
                '}';
    }
}
