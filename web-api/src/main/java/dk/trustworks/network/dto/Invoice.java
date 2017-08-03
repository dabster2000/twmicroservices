package dk.trustworks.network.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 08/07/2017.
 */
public class Invoice {

    public String uuid;
    public String projectuuid;
    public String projectname;
    public int year;
    public int month;
    public String clientname;
    public String clientaddresse;
    public String otheraddressinfo;
    public String zipcity;
    public String cvr;
    public String ean;
    public String attention;
    public int invoicenumber;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate invoicedate;
    public String description;
    public String specificdescription;
    public List<InvoiceItem> invoiceitems;
    public boolean errors;
    public InvoiceType type;
    public InvoiceStatus status;

    public Invoice() {
        this.invoiceitems = new ArrayList<>();
        this.errors = false;
    }

    public Invoice(InvoiceType type, String projectuuid, String projectname, int year, int month, String clientname, String clientaddresse, String otheraddressinfo, String zipcity, String ean, String cvr, String attention, LocalDate invoicedate, String description, String specificdescription) {
        this();
        this.type = type;
        this.otheraddressinfo = otheraddressinfo;
        this.ean = ean;
        this.cvr = cvr;
        this.status = InvoiceStatus.DRAFT;
        this.projectuuid = projectuuid;
        this.projectname = projectname;
        this.year = year;
        this.month = month;
        this.clientname = clientname;
        this.clientaddresse = clientaddresse;
        this.zipcity = zipcity;
        this.attention = attention;
        this.invoicedate = invoicedate;
        this.description = description;
        this.specificdescription = specificdescription;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Invoice{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", projectuuid='").append(projectuuid).append('\'');
        sb.append(", projectname='").append(projectname).append('\'');
        sb.append(", year=").append(year);
        sb.append(", month=").append(month);
        sb.append(", clientname='").append(clientname).append('\'');
        sb.append(", clientaddresse='").append(clientaddresse).append('\'');
        sb.append(", otheraddressinfo='").append(otheraddressinfo).append('\'');
        sb.append(", zipcity='").append(zipcity).append('\'');
        sb.append(", cvr='").append(cvr).append('\'');
        sb.append(", ean='").append(ean).append('\'');
        sb.append(", attention='").append(attention).append('\'');
        sb.append(", invoicenumber=").append(invoicenumber);
        sb.append(", invoicedate=").append(invoicedate);
        sb.append(", description='").append(description).append('\'');
        sb.append(", invoiceitems=").append(invoiceitems);
        sb.append(", errors=").append(errors);
        sb.append(", type=").append(type);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
