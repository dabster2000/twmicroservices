package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Contract;
import dk.trustworks.invoicewebui.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BudgetDocument {

    private final LocalDate month;
    private final Client client;
    private final User user;
    private final Contract contract;
    private double budgetHours;
    private final double rate;

}
