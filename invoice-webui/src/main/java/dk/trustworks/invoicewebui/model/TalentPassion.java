package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.TalentPassionType;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "talent_passion")
public class TalentPassion {

    @Id
    private String uuid;

    private String useruuid;

    @Transient
    private User user;

    private String owner;

    @Transient
    private User owner_user;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TalentPassionType type;

    private int performance;

    private int potential;

    private LocalDate registered;

    public TalentPassion() {
    }

    public TalentPassion(String uuid, User user, User owner, TalentPassionType type, int performance, int potential, LocalDate registered) {
        this.uuid = uuid;
        this.useruuid = user.getUuid();
        this.owner = owner.getUuid();
        this.type = type;
        this.performance = performance;
        this.potential = potential;
        this.registered = registered;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getOwner() {
        return owner_user;
    }

    public void setOwner(User owner) {
        this.owner_user = owner;
    }

    public TalentPassionType getType() {
        return type;
    }

    public void setType(TalentPassionType type) {
        this.type = type;
    }

    public int getPerformance() {
        return performance;
    }

    public void setPerformance(int performance) {
        this.performance = performance;
    }

    public int getPotential() {
        return potential;
    }

    public void setPotential(int potential) {
        this.potential = potential;
    }

    public LocalDate getRegistered() {
        return registered;
    }

    public void setRegistered(LocalDate registered) {
        this.registered = registered;
    }

    @Override
    public String toString() {
        return "TalentPassion{" +
                "uuid='" + uuid + '\'' +
                ", user=" + user +
                ", owner=" + owner +
                ", type=" + type +
                ", performance=" + performance +
                ", potential=" + potential +
                ", registered=" + registered +
                '}';
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }
}
