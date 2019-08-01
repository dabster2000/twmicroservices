package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.AchievementType;
import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue
    private int id;

    private String useruuid;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate achieved;

    @Enumerated(EnumType.STRING)
    private AchievementType achievement;

    public Achievement() {
    }

    public Achievement(User user, LocalDate achieved, AchievementType achievement) {
        this.useruuid = user.getUuid();
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
        return UserService.get().findByUUID(getUseruuid());
    }

    public void setUser(User user) {
        this.useruuid = user.getUuid();
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
                ", user=" + useruuid +
                ", achieved=" + achieved +
                ", achievement=" + achievement +
                '}';
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }
}
