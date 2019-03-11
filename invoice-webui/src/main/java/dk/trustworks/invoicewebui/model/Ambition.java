package dk.trustworks.invoicewebui.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ambition")
public class Ambition {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    private boolean active;

    //@Enumerated(EnumType.STRING)
    private String category;

    private boolean offering;

    public Ambition() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isOffering() {
        return offering;
    }

    public void setOffering(boolean offering) {
        this.offering = offering;
    }

    @Override
    public String toString() {
        return "Ambition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", category='" + category + '\'' +
                ", offering=" + offering +
                '}';
    }
}
