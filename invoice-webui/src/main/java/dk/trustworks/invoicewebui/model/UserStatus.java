package dk.trustworks.invoicewebui.model;


import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Created by hans on 23/06/2017.
 */
public class UserStatus {

    private String uuid;
    private ConsultantType type;
    private StatusType status;
    private LocalDate statusdate;
    private int allocation;

    public UserStatus() {
    }

    public UserStatus(ConsultantType type, StatusType status, LocalDate statusdate, int allocation) {
        uuid = UUID.randomUUID().toString();
        this.type = type;
        this.status = status;
        this.statusdate = statusdate;
        this.allocation = allocation;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public LocalDate getStatusdate() {
        return statusdate;
    }

    public void setStatusdate(LocalDate statusdate) {
        this.statusdate = statusdate;
    }

    public int getAllocation() {
        return allocation;
    }

    public void setAllocation(int allocation) {
        this.allocation = allocation;
    }

    public ConsultantType getType() {
        return type;
    }

    public void setType(ConsultantType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "uuid='" + uuid + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", statusdate=" + statusdate +
                ", allocation=" + allocation +
                '}';
    }
}
