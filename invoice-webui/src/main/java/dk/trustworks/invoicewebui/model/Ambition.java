package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.AmbitionCategory;

import javax.persistence.*;

@Entity
@Table(name = "ambition")
public class Ambition {

    @Id
    private int id;

    private String name;

    private boolean active;

    @Enumerated(EnumType.STRING)
    private AmbitionCategory category;


    public Ambition() {
    }

    public Ambition(String name, boolean active, AmbitionCategory category) {
        this.name = name;
        this.active = active;
        this.category = category;
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

    public AmbitionCategory getCategory() {
        return category;
    }

    public void setCategory(AmbitionCategory category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Ambition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", category=" + category +
                '}';
    }
}
