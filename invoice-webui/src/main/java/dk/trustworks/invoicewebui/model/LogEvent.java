package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.LogType;

import javax.persistence.*;

@Entity
@Table(name = "logging_event")
public class LogEvent {

    @Id
    @Column(name = "event_id")
    private long id;

    @Column(name = "arg0")
    @Enumerated(EnumType.STRING)
    private LogType type;

    @Column(name = "arg1")
    private String parameter;

    @Column(name = "timestmp")
    private Long dateTime;

    public LogEvent() {
    }

    public long getId() {
        return id;
    }

    public LogType getType() {
        return type;
    }

    public String getParameter() {
        return parameter;
    }

    public Long getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "LogEvent{" +
                "id=" + id +
                ", type=" + type +
                ", parameter='" + parameter + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
