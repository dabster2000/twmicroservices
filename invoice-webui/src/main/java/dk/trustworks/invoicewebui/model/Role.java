package dk.trustworks.invoicewebui.model;


import dk.trustworks.invoicewebui.model.enums.RoleType;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by hans on 23/06/2017.
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    private String uuid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="useruuid")
    private User user;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    public Role() {
    }

    public Role(User user, RoleType role) {
        uuid = UUID.randomUUID().toString();
        this.user = user;
        this.role = role;
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

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Role{" + "uuid='" + uuid + '\'' +
                ", user=" + user +
                ", role=" + role +
                '}';
    }
}
