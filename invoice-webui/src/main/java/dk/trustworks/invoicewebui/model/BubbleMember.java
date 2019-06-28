package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "bubble_members")
public class BubbleMember {

    @Id
    private String uuid;

    private String useruuid;

    @Transient
    private User member;

    @ManyToOne()
    @JoinColumn(name="bubbleuuid")
    private Bubble bubble;

    public BubbleMember() {
        uuid = UUID.randomUUID().toString();
    }

    public BubbleMember(User member, Bubble bubble) {
        this.uuid = UUID.randomUUID().toString();
        this.useruuid = member.getUuid();
        this.bubble = bubble;
    }

    public String getUuid() {
        return uuid;
    }

    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
    }

    public Bubble getBubble() {
        return bubble;
    }

    public void setBubble(Bubble bubble) {
        this.bubble = bubble;
    }

    @Override
    public String toString() {
        return "BubbleMember{" +
                "uuid='" + uuid + '\'' +
                ", member=" + member +
                ", bubble=" + bubble +
                '}';
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }
}
