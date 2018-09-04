package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.DocumentType;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by hans on 23/06/2017.
 */
@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="useruuid")
    private User user;

    @Column(length = 80)
    private String name;

    @Enumerated(EnumType.STRING)
    private DocumentType type;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate uploaddate;

    @Lob
    private String content;

    public Document() {
    }

    public Document(User user, String name, DocumentType type, LocalDate uploaddate, String content) {
        this.user = user;
        this.name = name;
        this.type = type;
        this.uploaddate = uploaddate;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public LocalDate getUploaddate() {
        return uploaddate;
    }

    public void setUploaddate(LocalDate uploaddate) {
        this.uploaddate = uploaddate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", user=" + user +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", uploaddate=" + uploaddate +
                ", content='" + content + '\'' +
                '}';
    }
}
