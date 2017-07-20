package dk.trustworks.network.dto;

import java.util.UUID;

/**
 * Created by hans on 08/07/2017.
 */
public class InvoiceItem {

    public String uuid;
    public String itemname;
    public String description;
    public double rate;
    public double hours;

    public InvoiceItem() {

    }

    public InvoiceItem(String itemname, String description, double rate, double hours) {
        this();
        this.itemname = itemname;
        this.description = description;
        this.rate = rate;
        this.hours = hours;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InvoiceItem{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", itemname='").append(itemname).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", rate=").append(rate);
        sb.append(", hours=").append(hours);
        sb.append('}');
        return sb.toString();
    }
}
