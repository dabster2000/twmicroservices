package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.InvoiceStatus;
import dk.trustworks.invoicewebui.model.enums.InvoiceType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Invoice {

    public String uuid;
    public String contractuuid;
    public String projectuuid;
    public String projectname;
    public int year;
    public int month;
    public double discount;
    public String clientname;
    public String clientaddresse;
    public String otheraddressinfo;
    public String zipcity;
    public String cvr;
    public String ean;
    public String attention;
    public int invoiceref;
    public int invoicenumber;
    public int referencenumber;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate invoicedate;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate bookingdate; //date from e-conomics
    public String projectref;
    public String contractref;
    public String specificdescription;
    public Set<InvoiceItem> invoiceitems;
    public InvoiceType type;
    public InvoiceStatus status;
    public byte[] pdf;

    @JsonIgnore private double sumNoTax;
    @JsonIgnore private double sumWithTax;

    public Invoice() {
        this.invoiceitems = new HashSet<>();
    }

    public Invoice(InvoiceType type, String contractuuid, String projectuuid, String projectname, double discount, int year, int month, String clientname, String clientaddresse, String otheraddressinfo, String zipcity, String ean, String cvr, String attention, LocalDate invoicedate, String projectref, String contractref, String specificdescription) {
        this();
        this.type = type;
        this.bookingdate = LocalDate.of(1900,1,1);
        if(type.equals(InvoiceType.CREDIT_NOTE)) invoiceref = invoicenumber;
        this.contractuuid = contractuuid;
        this.discount = discount;
        this.otheraddressinfo = otheraddressinfo;
        this.ean = ean;
        this.cvr = cvr;
        this.contractref = contractref;
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
        this.projectref = projectref;
        this.specificdescription = specificdescription;
        uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContractuuid() {
        return contractuuid;
    }

    public void setContractuuid(String contractuuid) {
        this.contractuuid = contractuuid;
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

    public int getInvoiceref() {
        return invoiceref;
    }

    public void setInvoiceref(int invoiceref) {
        this.invoiceref = invoiceref;
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

    public LocalDate getBookingdate() {
        return bookingdate;
    }

    public void setBookingdate(LocalDate bookingdate) {
        this.bookingdate = bookingdate;
    }

    public String getProjectref() {
        return projectref;
    }

    public void setProjectref(String projectref) {
        this.projectref = projectref;
    }

    public String getContractref() {
        return contractref;
    }

    public void setContractref(String contractref) {
        this.contractref = contractref;
    }

    public String getSpecificdescription() {
        return specificdescription;
    }

    public void setSpecificdescription(String specificdescription) {
        this.specificdescription = specificdescription;
    }

    @JsonProperty
    public Set<InvoiceItem> getInvoiceitems() {
        return invoiceitems;
    }

    @JsonProperty
    public void setInvoiceitems(Set<InvoiceItem> invoiceitems) {
        this.invoiceitems = invoiceitems;
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

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        String s = "Invoice{" +
                "uuid='" + uuid + '\'' +
                ", contractuuid='" + contractuuid + '\'' +
                ", projectuuid='" + projectuuid + '\'' +
                ", projectname='" + projectname + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", discount=" + discount +
                ", clientname='" + clientname + '\'' +
                ", clientaddresse='" + clientaddresse + '\'' +
                ", otheraddressinfo='" + otheraddressinfo + '\'' +
                ", zipcity='" + zipcity + '\'' +
                ", cvr='" + cvr + '\'' +
                ", ean='" + ean + '\'' +
                ", attention='" + attention + '\'' +
                ", invoiceref=" + invoiceref +
                ", invoicenumber=" + invoicenumber +
                ", invoicedate=" + invoicedate +
                ", bookingdate=" + bookingdate +
                ", projectref='" + projectref + '\'' +
                ", contractref='" + contractref + '\'' +
                ", specificdescription='" + specificdescription + '\'' +
                ", invoiceitems=" + invoiceitems +
                ", type=" + type +
                ", status=" + status +
                ", sumNoTax=" + sumNoTax +
                ", sumWithTax=" + sumWithTax +
                '}';
        s += "{\n";
        for (InvoiceItem invoiceitem : getInvoiceitems()) {
            s+="invoiceitem = " + invoiceitem + "\n";
        }
        s += "}\n";

        return s;
    }

    public int getReferencenumber() {
        return referencenumber;
    }

    public void setReferencenumber(int referencenumber) {
        this.referencenumber = referencenumber;
    }
}
