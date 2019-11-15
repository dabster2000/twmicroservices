package dk.trustworks.invoicewebui.web.vtv.model;

import java.util.Objects;

public class MarginRow implements Cloneable {
    private String customer;
    private String consultant;
    private double rate;
    private int margin;

    public MarginRow() {
    }

    public MarginRow(String customer, String consultant, double rate, int margin) {
        this.customer = customer;
        this.consultant = consultant;
        this.rate = rate;
        this.margin = margin;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getConsultant() {
        return consultant;
    }

    public void setConsultant(String consultant) {
        this.consultant = consultant;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarginRow marginRow = (MarginRow) o;
        return Double.compare(marginRow.rate, rate) == 0 &&
                margin == marginRow.margin &&
                customer.equals(marginRow.customer) &&
                consultant.equals(marginRow.consultant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, consultant, rate, margin);
    }

    @Override
    protected MarginRow clone() {
        try {
            return (MarginRow) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(
                    "The MarginRow object could not be cloned.", e);
        }
    }
}