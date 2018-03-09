package dk.trustworks.invoicewebui.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.Arrays;

/**
 * Created by hans on 16/08/2017.
 */
@Entity
@Table(name = "photos")
public class Photo {

    @Id
    private String uuid;

    private String relateduuid;

    @Lob
    private byte[] photo;

    public Photo() {
    }

    public Photo(String uuid, String relateduuid, byte[] photo) {
        this.uuid = uuid;
        this.relateduuid = relateduuid;
        this.photo = photo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRelateduuid() {
        return relateduuid;
    }

    public void setRelateduuid(String relateduuid) {
        this.relateduuid = relateduuid;
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
                ", relateduuid='" + relateduuid + '\'' +
                ", photo=" + Arrays.toString(photo) +
                '}';
    }
}
