package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 08/07/2017.
 */
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
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
    @OneToMany(fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="invoiceuuid")
    public List<InvoiceItem> invoiceitems;
    @Transient
    public boolean errors;
    public InvoiceType type;
    @Enumerated(EnumType.STRING)
    public InvoiceStatus status;
    @Lob @JsonIgnore public byte[] pdf;


    @JsonIgnore @Transient private double sumNoTax;
    @JsonIgnore @Transient private double sumWithTax;

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
        uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProjectuuid() {
        return projectuuid;
    }

    public void setProjectuuid(String projectuuid) {
        this.projectuuid = projectuuid;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public String getClientaddresse() {
        return clientaddresse;
    }

    public void setClientaddresse(String clientaddresse) {
        this.clientaddresse = clientaddresse;
    }

    public String getOtheraddressinfo() {
        return otheraddressinfo;
    }

    public void setOtheraddressinfo(String otheraddressinfo) {
        this.otheraddressinfo = otheraddressinfo;
    }

    public String getZipcity() {
        return zipcity;
    }

    public void setZipcity(String zipcity) {
        this.zipcity = zipcity;
    }

    public String getCvr() {
        return cvr;
    }

    public void setCvr(String cvr) {
        this.cvr = cvr;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public int getInvoicenumber() {
        return invoicenumber;
    }

    public void setInvoicenumber(int invoicenumber) {
        this.invoicenumber = invoicenumber;
    }

    public LocalDate getInvoicedate() {
        return invoicedate;
    }

    public void setInvoicedate(LocalDate invoicedate) {
        this.invoicedate = invoicedate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecificdescription() {
        return specificdescription;
    }

    public void setSpecificdescription(String specificdescription) {
        this.specificdescription = specificdescription;
    }

    public List<InvoiceItem> getInvoiceitems() {
        return invoiceitems;
    }

    public void setInvoiceitems(List<InvoiceItem> invoiceitems) {
        this.invoiceitems = invoiceitems;
    }

    public boolean isErrors() {
        return errors;
    }

    public void setErrors(boolean errors) {
        this.errors = errors;
    }

    public InvoiceType getType() {
        return type;
    }

    public void setType(InvoiceType type) {
        this.type = type;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
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

    public double getSumNoTax() {
        return sumNoTax;
    }

    public void setSumNoTax(double sumNoTax) {
        this.sumNoTax = sumNoTax;
    }

    public void addToSumNoTax(double sumNoTax) {
        this.sumNoTax += sumNoTax;
        this.sumWithTax = (this.sumNoTax * 1.25);
    }

    public double getSumWithTax() {
        return sumWithTax;
    }

    public void setSumWithTax(double sumWithTax) {
        this.sumWithTax = sumWithTax;
    }
}
