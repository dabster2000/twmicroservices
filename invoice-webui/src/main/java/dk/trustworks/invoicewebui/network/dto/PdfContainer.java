package dk.trustworks.invoicewebui.network.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;

/**
 * Created by hans on 18/07/2017.
 */
public class PdfContainer {

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate invoicedate;
    public int invoicenumber;
    public String clientname;
    public String projectname;
    public byte[] pdf;
    public InvoiceType type;

    public PdfContainer() {
    }
}
