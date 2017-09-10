package dk.trustworks.invoicewebui.model;


import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * Created by hans on 23/06/2017.
 */
@Entity
@Table(name = "userstatus")
public class UserStatus {

    @Id
    private String uuid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="useruuid")
    private User user;

    @Enumerated(EnumType.STRING)
    private StatusType status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusdate;
    private int allocation;

    public UserStatus() {
    }

    public UserStatus(User user, StatusType status, LocalDate statusdate, int allocation) {
        uuid = UUID.randomUUID().toString();
        this.user = user;
        this.status = status;
        this.statusdate = java.util.Date.from(
                statusdate.atStartOfDay(
                        ZoneId.of( "Europe/Paris" )
                ).toInstant()
        );;
        this.allocation = allocation;
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

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public Date getStatusdate() {
        return statusdate;
    }

    public void setStatusdate(Date statusdate) {
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
