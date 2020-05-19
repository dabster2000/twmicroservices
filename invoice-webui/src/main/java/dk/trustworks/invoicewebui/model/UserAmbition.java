package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "user_ambition")
public class UserAmbition {

    @Id
    @GeneratedValue
    private int id;

    private int ambitionid;

    private String useruuid;

    private int score;

    private int ambition;

    private LocalDate updated;

    public UserAmbition() {
    }

    public UserAmbition(int ambitionid, User user, int score, int ambition) {
        this.ambitionid = ambitionid;
        this.useruuid = user.getUuid();
        this.score = score;
        this.ambition = ambition;
        updated = LocalDate.now();
    }

    public void refresh(UserAmbition userAmbition) {
        this.id = userAmbition.getId();
        this.ambitionid = userAmbition.getAmbitionid();
        this.useruuid = userAmbition.getUser().getUuid();
        this.score = userAmbition.getScore();
        this.ambition = userAmbition.getAmbition();
        this.updated = userAmbition.getUpdated();
    }

    public int getId() {
        return id;
    }

    public int getAmbitionid() {
        return ambitionid;
    }

    public void setAmbitionid(int ambitionid) {
        this.ambitionid = ambitionid;
    }

    public User getUser() {
        return UserService.get().findByUUID(getUseruuid());
    }

    public void setUser(User user) {
        this.useruuid = user.getUuid();
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

    public LocalDate getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDate updated) {
        this.updated = updated;
    }

    public int rollAmbition() {
        ambition += 1;
        if(ambition == 3) ambition = 0;
        return ambition;
    }

    @Override
    public String toString() {
        return "UserAmbition{" +
                "id=" + id +
                ", ambitionid=" + ambitionid +
                ", user=" + useruuid +
                ", score=" + score +
                ", ambition=" + ambition +
                ", updated=" + updated +
                '}';
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }
}
