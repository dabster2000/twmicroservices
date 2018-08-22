package dk.trustworks.invoicewebui.model;

import javax.persistence.*;

@Entity
@Table(name = "user_ambition")
public class UserAmbition {

    @Id
    private int id;

    private int ambitionid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="useruuid")
    private User user;

    private int score;

    private int ambition;

    public UserAmbition() {
    }

    public UserAmbition(int ambitionid, User user, int score, int ambition) {
        this.ambitionid = ambitionid;
        this.user = user;
        this.score = score;
        this.ambition = ambition;
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
                ", user=" + user.getUsername() +
                ", score=" + score +
                ", ambition=" + ambition +
                '}';
    }
}
