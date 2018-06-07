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
@Table(name = "files")
public class File {

    @Id
    private String uuid;

    private String relateduuid;

    @Lob
    private byte[] file;

    public File() {
    }

    public File(String uuid, String relateduuid, byte[] file) {
        this.uuid = uuid;
        this.relateduuid = relateduuid;
        this.file = file;
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

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "File{" +
                "uuid='" + uuid + '\'' +
                ", relateduuid='" + relateduuid + '\'' +
                ", file=" + Arrays.toString(file) +
                '}';
    }
}
