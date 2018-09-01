package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.CommunicationChannelType;
import dk.trustworks.invoicewebui.model.enums.ReminderIntervalType;
import dk.trustworks.invoicewebui.model.enums.ReminderTargetType;
import dk.trustworks.invoicewebui.model.enums.ReminderType;

import javax.persistence.*;

@Entity
@Table(name = "reminder")
public class Reminder {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ReminderType type;

    @Column(name = "interval")
    @Enumerated(EnumType.STRING)
    private ReminderIntervalType interval;

    private String description;

    @Column(name = "target")
    @Enumerated(EnumType.STRING)
    private ReminderTargetType target;

    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    private CommunicationChannelType channel;

    public Reminder() {
    }

    public Reminder(ReminderType type, ReminderIntervalType interval, String description, ReminderTargetType target, CommunicationChannelType channel) {
        this.type = type;
        this.interval = interval;
        this.description = description;
        this.target = target;
        this.channel = channel;
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

    public ReminderIntervalType getInterval() {
        return interval;
    }

    public void setInterval(ReminderIntervalType interval) {
        this.interval = interval;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReminderTargetType getTarget() {
        return target;
    }

    public void setTarget(ReminderTargetType target) {
        this.target = target;
    }

    public CommunicationChannelType getChannel() {
        return channel;
    }

    public void setChannel(CommunicationChannelType channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", type=" + type +
                ", interval=" + interval +
                ", description='" + description + '\'' +
                ", target=" + target +
                ", channel=" + channel +
                '}';
    }
}
