package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.User;

import java.time.LocalDate;

public class BudgetDocument {

    private final LocalDate month;
    private final Client client;
    private final User user;
    private final Contract contract;
    private double budgetHours;
    private final double rate;

    public BudgetDocument(LocalDate month, Client client, User user, Contract contract, double budgetHours, double rate) {
        this.month = month;
        this.client = client;
        this.user = user;
        this.contract = contract;
        this.budgetHours = budgetHours;
        this.rate = rate;
    }

    public LocalDate getMonth() {
        return month;
    }

    public Client getClient() {
        return client;
    }

    public User getUser() {
        return user;
    }

    public Contract getContract() {
        return contract;
    }

    public double getBudgetHours() {
        return budgetHours;
    }

    public void setBudgetHours(double budgetHours) {
        this.budgetHours = budgetHours;
    }

    public double getRate() {
        return rate;
    }
}
