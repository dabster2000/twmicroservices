package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Currency {

    @Id
    @GeneratedValue
    private int id;

    private String currencytype;

    private double price;

    @Basic(optional = false)
    @Column(name = "collected", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date collected;

    public Currency() {
    }

    public Currency(String currencytype, double price) {
        this.currencytype = currencytype;
        this.price = price;
    }

    public Currency(String currencytype, double price, Date collected) {
        this.currencytype = currencytype;
        this.price = price;
        this.collected = collected;
    }

    public int getId() {
        return id;
    }

    public String getCurrencytype() {
        return currencytype;
    }

    public void setCurrencytype(String currencytype) {
        this.currencytype = currencytype;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCollected() {
        return collected;
    }

    public void setCollected(Date collected) {
        this.collected = collected;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", currencytype='" + currencytype + '\'' +
                ", price=" + price +
                ", collected=" + collected +
                '}';
    }
}
