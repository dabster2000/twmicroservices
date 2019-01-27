package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_ambition")
public class UserAmbition {

    @Id
    @GeneratedValue
    private int id;

    private int ambitionid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="useruuid")
    private User user;

    private int score;

    private int ambition;

    private LocalDate updated;

    public UserAmbition() {
    }

    public UserAmbition(int ambitionid, User user, int score, int ambition) {
        this.ambitionid = ambitionid;
        this.user = user;
        this.score = score;
        this.ambition = ambition;
        updated = LocalDate.now();
    }

    public void refresh(UserAmbition userAmbition) {
        this.id = userAmbition.getId();
        this.ambitionid = userAmbition.getAmbitionid();
        this.user = userAmbition.getUser();
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
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
                ", user=" + user +
                ", score=" + score +
                ", ambition=" + ambition +
                ", updated=" + updated +
                '}';
    }
}
