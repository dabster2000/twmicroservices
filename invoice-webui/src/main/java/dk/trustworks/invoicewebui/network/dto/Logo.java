package dk.trustworks.invoicewebui.network.dto;

import java.util.Arrays;

/**
 * Created by hans on 16/08/2017.
 */
public class Logo {

    private String uuid;
    private String clientuuid;
    private byte[] logo;

    public Logo() {
    }

    public Logo(String uuid, String clientuuid, byte[] logo) {
        this.uuid = uuid;
        this.clientuuid = clientuuid;
        this.logo = logo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getClientuuid() {
        return clientuuid;
    }

    public void setClientuuid(String clientuuid) {
        this.clientuuid = clientuuid;
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
        sb.append(", clientuuid='").append(clientuuid).append('\'');
        sb.append(", logo=").append(Arrays.toString(logo));
        sb.append('}');
        return sb.toString();
    }
}
