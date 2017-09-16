package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Created by hans on 12/09/2017.
 */
@Entity
public class Notification {

    @Id
    private String uuid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useruuid")
    private User receiver;
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationdate;
    private String titel;
    private String content;
    private String link;

    public Notification() {
        uuid = UUID.randomUUID().toString();
    }

    public Notification(User receiver, Date expirationdate, String titel, String content, String link) {
        uuid = UUID.randomUUID().toString();
        this.receiver = receiver;
        this.expirationdate = expirationdate;
        this.titel = titel;
        this.content = content;
        this.link = link;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Date getExpirationdate() {
        return expirationdate;
    }

    public void setExpirationdate(Date expirationdate) {
        this.expirationdate = expirationdate;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Notification{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", receiver=").append(receiver);
        sb.append(", expirationdate=").append(expirationdate);
        sb.append(", titel='").append(titel).append('\'');
        sb.append(", notification='").append(content).append('\'');
        sb.append(", link='").append(link).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
