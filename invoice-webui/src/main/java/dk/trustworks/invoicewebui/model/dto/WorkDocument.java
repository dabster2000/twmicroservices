package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.User;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@NonNull
public class WorkDocument {

    private final LocalDate month;
    private final Client client;
    private final User user;
    private final Contract contract;
    private final double workHours;
    private final double rate;

    public WorkDocument(LocalDate month, Client client, User user, Contract contract, double workHours, double rate) {
        this.month = month;
        this.client = client;
        this.user = user;
        this.contract = contract;
        this.workHours = workHours;
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

    public double getWorkHours() {
        return workHours;
    }

    public double getRate() {
        return rate;
    }

    public double getMonthRevenue() {
        return workHours * rate;
    }
}
