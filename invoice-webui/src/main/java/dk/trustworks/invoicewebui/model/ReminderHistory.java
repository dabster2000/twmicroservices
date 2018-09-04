package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.ReminderType;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reminder_history")
public class ReminderHistory {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ReminderType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="useruuid")
    private User user;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "transmissiondate")
    private LocalDate transmissionDate;

    private String targetuuid;

    public ReminderHistory() {
    }

    public ReminderHistory(ReminderType type, User user, LocalDate transmissionDate) {
        this.type = type;
        this.user = user;
        this.transmissionDate = transmissionDate;
    }

    public ReminderHistory(ReminderType type, User user, LocalDate transmissionDate, String targetuuid) {
        this.type = type;
        this.user = user;
        this.transmissionDate = transmissionDate;
        this.targetuuid = targetuuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ReminderType getType() {
        return type;
    }

    public void setType(ReminderType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getTransmissionDate() {
        return transmissionDate;
    }

    public void setTransmissionDate(LocalDate transmissionDate) {
        this.transmissionDate = transmissionDate;
    }

    public String getTargetuuid() {
        return targetuuid;
    }

    public void setTargetuuid(String targetuuid) {
        this.targetuuid = targetuuid;
    }

    @Override
    public String toString() {
        return "ReminderHistory{" +
                "id=" + id +
                ", type=" + type +
                ", user=" + user +
                ", transmissionDate=" + transmissionDate +
                ", targetuuid='" + targetuuid + '\'' +
                '}';
    }
}
