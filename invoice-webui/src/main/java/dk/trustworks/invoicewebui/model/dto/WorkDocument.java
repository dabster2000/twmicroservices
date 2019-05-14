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

    public double getMonthRevenue() {
        return workHours * rate;
    }
}
