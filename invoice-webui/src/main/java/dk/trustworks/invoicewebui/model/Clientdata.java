package dk.trustworks.invoicewebui.model;

import java.util.UUID;

public class Clientdata {
    private String uuid;
    private String city;
    private String clientname;
    private String contactperson;
    private String cvr;
    private String ean;
    private String otheraddressinfo;
    private Long postalcode;
    private String streetnamenumber;
    private String clientuuid;

    public Clientdata() {
    }

    public Clientdata(String city, String clientname, String contactperson, String cvr, String ean, String otheraddressinfo, Long postalcode, String streetnamenumber, Client client) {
        this.city = city;
        this.clientname = clientname;
        this.contactperson = contactperson;
        this.cvr = cvr;
        this.ean = ean;
        this.otheraddressinfo = otheraddressinfo;
        this.postalcode = postalcode;
        this.streetnamenumber = streetnamenumber;
        this.clientuuid = client.getUuid();
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

    public String getClientuuid() {
        return clientuuid;
    }

    public void setClientuuid(String clientuuid) {
        this.clientuuid = clientuuid;
    }

    @Override
    public String toString() {
        return "Clientdata{" + "uuid='" + uuid + '\'' +
                ", city='" + city + '\'' +
                ", clientname='" + clientname + '\'' +
                ", contactperson='" + contactperson + '\'' +
                ", cvr='" + cvr + '\'' +
                ", ean='" + ean + '\'' +
                ", otheraddressinfo='" + otheraddressinfo + '\'' +
                ", postalcode=" + postalcode +
                ", streetnamenumber='" + streetnamenumber + '\'' +
                '}';
    }
}
