package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.*;

/**
 * Created by hans on 23/06/2017.
 */
@Entity
@Table(name = "keypurpose")
public class KeyPurpose {

    @Id
    @GeneratedValue
    private int id;

    private String useruuid;

    private int num;

    @Column(length = 255)
    private String description;

    public KeyPurpose() {
    }

    public KeyPurpose(User user, int num, String description) {
        this.useruuid = user.getUuid();
        this.num = num;
        this.description = description;
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "KeyPurpose{" +
                "id=" + id +
                ", user=" + useruuid +
                ", num=" + num +
                ", description='" + description + '\'' +
                '}';
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }
}
