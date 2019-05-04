package dk.trustworks.invoicewebui.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Created by hans on 08/07/2017.
 */
@Entity
@Table(name = "invoiceitems")
public class InvoiceItem {

    @Id
    public String uuid;
    public String itemname;
    public String description;
    public double rate;
    public double hours;
    //public String invoiceuuid;

    public InvoiceItem() {

    }

    public InvoiceItem(String itemname, String description, double rate, double hours) {
        this();
        uuid = UUID.randomUUID().toString();
        this.itemname = itemname;
        this.description = description;
        this.rate = rate;
        this.hours = hours;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    @Override
    public String toString() {
        //sb.append(", invoiceuuid='").append(invoiceuuid).append('\'');
        return "InvoiceItem{" + "uuid='" + uuid + '\'' +
                ", itemname='" + itemname + '\'' +
                ", description='" + description + '\'' +
                ", rate=" + rate +
                ", hours=" + hours +
                '}';
    }
}