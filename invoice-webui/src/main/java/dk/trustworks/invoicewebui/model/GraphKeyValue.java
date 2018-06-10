package dk.trustworks.invoicewebui.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by hans on 20/09/2017.
 */
@Entity
public class GraphKeyValue {

    @Id
    private String uuid;
    private String description;
    private double value;

    public GraphKeyValue() {
    }

    public GraphKeyValue(String uuid, String description, int value) {
        this.uuid = uuid;
        this.description = description;
        this.value = value;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void addValue(int value) {
        this.value += value;
    }

    @Override
    public String toString() {
        return "GraphKeyValues{" + "uuid='" + uuid + '\'' +
                ", description='" + description + '\'' +
                ", value=" + value +
                '}';
    }
}
