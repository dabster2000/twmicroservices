package dk.trustworks.invoicewebui.web.contracts.model;

import dk.trustworks.invoicewebui.model.Consultant;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.utils.NumberConverter;

import java.util.Arrays;

/**
 * Created by hans on 21/08/2017.
 */
public class ConsultantRow {

    private Consultant consultant;
    private User user;
    private String username;
    private String rate;
    private String amount;

    private String[] budget;

    public ConsultantRow(int months) {
        budget = new String[months];
    }

    public ConsultantRow(Consultant consultant, int months) {
        this(months);
        this.user = consultant.getUser();
        this.consultant = consultant;
        this.username = user.getUsername();
        this.rate = NumberConverter.formatDouble(consultant.getRate());
        this.amount = NumberConverter.formatDouble(consultant.getBudget());
    }

    public String[] getBudget() {
        return budget;
    }

    public void setBudget(String[] budget) {
        this.budget = budget;
    }

    public void setMonth(int month, String value) {
        getBudget()[month] = value;
    }

    public String getMonth(int actualMonth) {
        return (budget[actualMonth]!=null)?budget[actualMonth]:"0.0";
    }

    public Consultant getConsultant() {
        return consultant;
    }

    public void setConsultant(Consultant consultant) {
        this.consultant = consultant;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ConsultantRow{" +
                ", username='" + username + '\'' +
                ", rate='" + rate + '\'' +
                ", amount='" + amount + '\'' +
                ", budget=" + Arrays.toString(budget) +
                '}';
    }
}
