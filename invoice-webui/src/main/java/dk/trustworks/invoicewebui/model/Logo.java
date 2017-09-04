package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.Arrays;

/**
 * Created by hans on 16/08/2017.
 */
@Entity
public class Logo {

    @Id
    private String uuid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientuuid")
    private Client client;

    @Lob
    private byte[] logo;

    public Logo() {
    }

    public Logo(String uuid, Client client, byte[] logo) {
        this.uuid = uuid;
        this.client = client;
        this.logo = logo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Logo{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", logo=").append(Arrays.toString(logo));
        sb.append('}');
        return sb.toString();
    }
}
