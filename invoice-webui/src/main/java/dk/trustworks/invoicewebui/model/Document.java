package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.DocumentType;
import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Created by hans on 23/06/2017.
 */
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue
    private int id;

    private String useruuid;

    @Column(length = 80)
    private String name;

    @Column(length = 80)
    private String filename;

    @Enumerated(EnumType.STRING)
    private DocumentType type;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate uploaddate;

    @Lob
    private byte[] content;

    public Document() {
    }

    public Document(User user, String name, String filename, DocumentType type, LocalDate uploaddate, byte[] content) {
        this.useruuid = user.getUuid();
        this.name = name;
        this.filename = filename;
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

    public String getUseruuid() {
        return useruuid;
    }

    public User getUser() {
        return UserService.get().findByUUID(getUseruuid(), true);
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", filename='" + filename + '\'' +
                ", type=" + type +
                ", uploaddate=" + uploaddate +
                ", content=" + Arrays.toString(content) +
                '}';
    }

}
