package dk.trustworks.invoicewebui.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.User;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BudgetDocument {

    @JsonProperty("month")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate month;
    @JsonProperty("client")
    private Client client;
    @JsonProperty("user")
    private User user;
    @JsonProperty("contract")
    private Contract contract;
    @JsonProperty("rate")
    private double rate;
    @JsonProperty("budgetHours")
    private double budgetHours;
    @JsonProperty("budgetHoursWithNoAvailabilityAdjustment")
    private double budgetHoursWithNoAvailabilityAdjustment;

    public BudgetDocument() {
    }

    @JsonProperty("month")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate getMonth() {
        return month;
    }

    @JsonProperty("month")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public void setMonth(LocalDate month) {
        this.month = month;
    }

    @JsonProperty("client")
    public Client getClient() {
        return client;
    }

    @JsonProperty("client")
    public void setClient(Client client) {
        this.client = client;
    }

    @JsonProperty("user")
    public User getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty("contract")
    public Contract getContract() {
        return contract;
    }

    @JsonProperty("contract")
    public void setContract(Contract contract) {
        this.contract = contract;
    }

    @JsonProperty("rate")
    public double getRate() {
        return rate;
    }

    @JsonProperty("rate")
    public void setRate(double rate) {
        this.rate = rate;
    }

    @JsonProperty("budgetHours")
    public double getBudgetHours() {
        return budgetHours;
    }

    @JsonProperty("getBudgetHoursWithNoAvailabilityAdjustment")
    public double getBudgetHoursWithNoAvailabilityAdjustment() {
        return budgetHoursWithNoAvailabilityAdjustment;
    }

    @JsonProperty("grossBudgetHours")
    public void setBudgetHours(double budgetHours) {
        this.budgetHours = budgetHours;
    }

    @Override
    public String toString() {
        return "BudgetDocument{" +
                "month=" + month +
                ", client=" + client.getName() +
                ", user=" + user.getUsername() +
                ", contract=" + contract.getUuid() +
                ", rate=" + rate +
                ", grossBudgetHours=" + budgetHours +
                '}';
    }
}

/*
public class BudgetDocument {

    private LocalDate month;
    private Client client;
    private User user;
    private Contract contract;
    private double budgetHours;
    private double rate;

    public BudgetDocument() {
    }

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

    public double getGrossBudgetHours() {
        return budgetHours;
    }

    public void setGrossBudgetHours(double budgetHours) {
        this.budgetHours = budgetHours;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "BudgetDocument{" +
                "month=" + month +
                ", client=" + client +
                ", user=" + user.getUsername() +
                ", contract=" + contract.getName() +
                ", budgetHours=" + budgetHours +
                ", rate=" + rate +
                '}';
    }
}

 */
