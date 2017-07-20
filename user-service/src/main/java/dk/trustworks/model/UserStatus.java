package dk.trustworks.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by hans on 23/06/2017.
 */
@Entity
@Table(name = "userstatus")
public class UserStatus {

    @Id private String uuid;
    @ManyToOne
    @JoinColumn(name="useruuid")
    private User user;
    private String status;
    private Timestamp statusdate;
    private int allocation;

    public UserStatus() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getStatusdate() {
        return statusdate;
    }

    public void setStatusdate(Timestamp statusdate) {
        this.statusdate = statusdate;
    }

    public int getAllocation() {
        return allocation;
    }

    public void setAllocation(int allocation) {
        this.allocation = allocation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserStatus{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", user=").append(user);
        sb.append(", status='").append(status).append('\'');
        sb.append(", statusdate=").append(statusdate);
        sb.append(", allocation=").append(allocation);
        sb.append('}');
        return sb.toString();
    }
}
