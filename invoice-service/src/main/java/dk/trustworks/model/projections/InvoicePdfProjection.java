package dk.trustworks.model.projections;

import dk.trustworks.model.Invoice;
import dk.trustworks.model.InvoiceType;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

/**
 * Created by hans on 18/07/2017.
 */

@Projection(name = "pdf", types = { Invoice.class })
public interface InvoicePdfProjection {
    LocalDate getInvoicedate();
    int getInvoicenumber();
    String getClientname();
    String getProjectname();
    byte[] getPdf();
    InvoiceType getType();
}
