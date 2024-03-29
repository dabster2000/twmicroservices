package dk.trustworks.invoicewebui.model.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserAmbitionDTO {

    @Id
    private Integer id;
    private String name;
    private int score;
    private int ambition;
    private String category;

    public UserAmbitionDTO() {
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getAmbition() {
        return ambition;
    }

    public void setAmbition(int ambition) {
        this.ambition = ambition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "UserAmbitionDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", ambition=" + ambition +
                ", category='" + category + '\'' +
                '}';
    }
}
