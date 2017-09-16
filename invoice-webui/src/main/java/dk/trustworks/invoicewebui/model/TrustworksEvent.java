package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Created by hans on 13/09/2017.
 */
@Entity
@Table(name = "events")
public class TrustworksEvent {

    @Id
    private String uuid;
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventdate;
    private String name;
    private String location;
    @Enumerated(EnumType.STRING)
    private EventType eventtype;

    public TrustworksEvent() {
    }

    public TrustworksEvent(Date eventdate, String name, String location, EventType eventtype) {
        uuid = UUID.randomUUID().toString();
        this.eventdate = eventdate;
        this.name = name;
        this.location = location;
        this.eventtype = eventtype;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getEventdate() {
        return eventdate;
    }

    public void setEventdate(Date eventdate) {
        this.eventdate = eventdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public EventType getEventtype() {
        return eventtype;
    }

    public void setEventtype(EventType eventtype) {
        this.eventtype = eventtype;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Event{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", eventdate=").append(eventdate);
        sb.append(", name='").append(name).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", eventtype=").append(eventtype);
        sb.append('}');
        return sb.toString();
    }
}
