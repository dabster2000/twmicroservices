package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.AchievementType;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="useruuid")
    private User user;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate achieved;

    @Enumerated(EnumType.STRING)
    private AchievementType achievement;

    public Achievement() {
    }

    public Achievement(User user, LocalDate achieved, AchievementType achievement) {
        this.user = user;
        this.achieved = achieved;
        this.achievement = achievement;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getAchieved() {
        return achieved;
    }

    public void setAchieved(LocalDate achieved) {
        this.achieved = achieved;
    }

    public AchievementType getAchievement() {
        return achievement;
    }

    public void setAchievement(AchievementType achievement) {
        this.achievement = achievement;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "id=" + id +
                ", user=" + user +
                ", achieved=" + achieved +
                ", achievement=" + achievement +
                '}';
    }
}