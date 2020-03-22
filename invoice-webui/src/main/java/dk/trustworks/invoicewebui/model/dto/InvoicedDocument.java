package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.model.enums.InvoiceType;

import java.time.LocalDate;


public class InvoicedDocument {

    private final int invoicenumber;
    private final InvoiceType invoiceType;
    private final LocalDate month;
    private final double invoiced;

    public InvoicedDocument(int invoicenumber, InvoiceType invoiceType, LocalDate month, double invoiced) {
        this.invoicenumber = invoicenumber;
        this.invoiceType = invoiceType;
        this.month = month;
        this.invoiced = invoiced;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public LocalDate getMonth() {
        return month;
    }

    public double getInvoiced() {
        return invoiced;
    }

    @Override
    public String toString() {
        return "InvoicedDocument{" +
                "invoicenumber=" + invoicenumber +
                ", invoiceType=" + invoiceType +
                ", month=" + month +
                ", invoiced=" + invoiced +
                '}';
    }
}
