package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.model.enums.InvoiceType;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@NonNull
public class InvoicedDocument {

    private final InvoiceType invoiceType;
    private final LocalDate month;
    private final double invoiced;
}
