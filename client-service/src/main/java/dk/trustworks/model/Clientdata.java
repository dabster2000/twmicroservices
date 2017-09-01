package dk.trustworks.model;

import javax.persistence.*;

@Entity
public class Clientdata {
    @Id
    private String uuid;
    private String city;
    private String clientname;
    private String contactperson;
    private String cvr;
    private String ean;
    private String otheraddressinfo;
    private Long postalcode;
    private String streetnamenumber;
    @ManyToOne()
    @JoinColumn(name="clientuuid")
    private Client client;

    public Clientdata() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public String getContactperson() {
        return contactperson;
    }

    public void setContactperson(String contactperson) {
        this.contactperson = contactperson;
    }

    public String getCvr() {
        return cvr;
    }

    public void setCvr(String cvr) {
        this.cvr = cvr;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getOtheraddressinfo() {
        return otheraddressinfo;
    }

    public void setOtheraddressinfo(String otheraddressinfo) {
        this.otheraddressinfo = otheraddressinfo;
    }

    public Long getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(Long postalcode) {
        this.postalcode = postalcode;
    }

    public String getStreetnamenumber() {
        return streetnamenumber;
    }

    public void setStreetnamenumber(String streetnamenumber) {
        this.streetnamenumber = streetnamenumber;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Clientdata{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", clientname='").append(clientname).append('\'');
        sb.append(", contactperson='").append(contactperson).append('\'');
        sb.append(", cvr='").append(cvr).append('\'');
        sb.append(", ean='").append(ean).append('\'');
        sb.append(", otheraddressinfo='").append(otheraddressinfo).append('\'');
        sb.append(", postalcode=").append(postalcode);
        sb.append(", streetnamenumber='").append(streetnamenumber).append('\'');
        sb.append(", client=").append(client);
        sb.append('}');
        return sb.toString();
    }
}
