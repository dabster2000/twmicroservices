package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "incomeforecast")
public class IncomeForecast {

    @Id
    private String uuid;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private int sort;
    private double amount;

    public IncomeForecast() {
    }

    public IncomeForecast(int sort, double amount) {
        this.uuid = UUID.randomUUID().toString();
        this.created = new Date();
        this.sort = sort;
        this.amount = amount;
    }

    public String getUuid() {
        return uuid;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "IncomeForecast{" +
                "uuid='" + uuid + '\'' +
                ", created=" + created +
                ", sort=" + sort +
                ", amount=" + amount +
                '}';
    }
}
