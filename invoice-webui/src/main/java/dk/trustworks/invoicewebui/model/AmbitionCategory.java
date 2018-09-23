package dk.trustworks.invoicewebui.model;

import javax.persistence.*;

@Entity
@Table(name = "ambition_category")
public class AmbitionCategory {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "category")
    //@Enumerated(EnumType.STRING)
    private String ambitionCategoryType;

    private String name;

    private boolean active;

    public AmbitionCategory() {
    }

    public AmbitionCategory(String ambitionCategoryType, String name, boolean active) {
        this.ambitionCategoryType = ambitionCategoryType;
        this.name = name;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAmbitionCategoryType() {
        return ambitionCategoryType;
    }

    public void setAmbitionCategoryType(String ambitionCategoryType) {
        this.ambitionCategoryType = ambitionCategoryType;
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

    @Override
    public String toString() {
        return "AmbitionCategory{" +
                "id=" + id +
                ", ambitionCategoryType=" + ambitionCategoryType +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
