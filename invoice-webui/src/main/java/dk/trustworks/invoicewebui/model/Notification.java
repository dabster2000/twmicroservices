package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.NotificationType;

import javax.persistence.*;
import java.time.LocalDate;
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
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate entrydate;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate expirationdate;
    private String titel;
    private String content;
    private String link;
    private String themeimage;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    public Notification() {
        uuid = UUID.randomUUID().toString();
    }

    public Notification(User receiver, LocalDate entrydate, LocalDate expirationdate, String titel, String content, String link, String themeimage, NotificationType notificationType) {
        this.notificationType = notificationType;
        uuid = UUID.randomUUID().toString();
        this.receiver = receiver;
        this.entrydate = entrydate;
        this.expirationdate = expirationdate;
        this.titel = titel;
        this.content = content;
        this.link = link;
        this.themeimage = themeimage;
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

    public LocalDate getExpirationdate() {
        return expirationdate;
    }

    public void setExpirationdate(LocalDate expirationdate) {
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

    public LocalDate getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(LocalDate entrydate) {
        this.entrydate = entrydate;
    }

    public String getThemeimage() {
        return themeimage;
    }

    public void setThemeimage(String themeimage) {
        this.themeimage = themeimage;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "uuid='" + uuid + '\'' +
                ", receiver=" + receiver +
                ", entrydate=" + entrydate +
                ", expirationdate=" + expirationdate +
                ", titel='" + titel + '\'' +
                ", content='" + content + '\'' +
                ", link='" + link + '\'' +
                ", themeimage='" + themeimage + '\'' +
                ", notificationType=" + notificationType +
                '}';
    }
}
