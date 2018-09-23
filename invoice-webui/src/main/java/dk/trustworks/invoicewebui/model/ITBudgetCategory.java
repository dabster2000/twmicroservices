package dk.trustworks.invoicewebui.model;

import javax.persistence.*;

/**
 * Created by hans on 23/06/2017.
 */
@Entity
@Table(name = "itbudget_category")
public class ITBudgetCategory {

    @Id
    @GeneratedValue
    private int id;

    @Column(length = 25)
    private String name;

    private int lifespan;

    @Column(name = "long_name")
    private String longName;

    private String description;

    public ITBudgetCategory() {
    }

    public ITBudgetCategory(String name, int lifespan, String longName, String description) {
        this.name = name;
        this.lifespan = lifespan;
        this.longName = longName;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ITBudgetCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lifespan=" + lifespan +
                ", longName='" + longName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
