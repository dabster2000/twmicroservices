package dk.trustworks.model.projections;

import dk.trustworks.model.Invoice;
import org.springframework.data.rest.core.config.Projection;

/**
 * Created by hans on 18/07/2017.
 */

@Projection(name = "pdf", types = { Invoice.class })
public interface InvoicePdfProjection {
    byte[] getPdf();
}
