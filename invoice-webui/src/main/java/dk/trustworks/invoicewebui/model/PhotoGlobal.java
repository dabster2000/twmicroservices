package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.PhotoGlobalType;

import javax.persistence.*;
import java.util.Arrays;

/**
 * Created by hans on 16/08/2017.
 */
@Entity
@Table(name = "photos_global")
public class PhotoGlobal {

    @Id
    private String uuid;

    @Enumerated(EnumType.STRING)
    private PhotoGlobalType type;

    @Lob
    private byte[] photo;

    public PhotoGlobal() {
    }

    public PhotoGlobal(String uuid, PhotoGlobalType type, byte[] photo) {
        this.uuid = uuid;
        this.type = type;
        this.photo = photo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public PhotoGlobalType getType() {
        return type;
    }

    public void setType(PhotoGlobalType type) {
        this.type = type;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "uuid='" + uuid + '\'' +
                ", type='" + type + '\'' +
                ", photo=" + Arrays.toString(photo) +
                '}';
    }
}
