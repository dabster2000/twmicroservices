package dk.trustworks.invoicewebui.web.model;

import dk.trustworks.invoicewebui.model.User;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;

public class CateringEntry {

    private String name;
    private User contact;
    private LocalDateTime start;
    private LocalDateTime end;
    private int people;
    private Set<String> orderTypes;
    private String details;
    private String quality;
    private String account;

    public CateringEntry() {
    }

    public CateringEntry(String name, User contact, LocalDateTime start, LocalDateTime end, int people, Set<String> orderTypes, String details, String quality, String account) {
        this.name = name;
        this.contact = contact;
        this.start = start;
        this.end = end;
        this.people = people;
        this.orderTypes = orderTypes;
        this.details = details;
        this.quality = quality;
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getContact() {
        return contact;
    }

    public void setContact(User contact) {
        this.contact = contact;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public Set<String> getOrderTypes() {
        return orderTypes;
    }

    public void setOrderTypes(Set<String> orderTypes) {
        this.orderTypes = orderTypes;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "" +
                "Arrangement: " + name + "\n" +
                "Arrangør: " + contact + "\n" +
                "Møde start: " + start + "\n" +
                "Møde slut: " + end + "\n" +
                "Antal deltagere: " + people + "\n" +
                "Bestilling: " + StringUtils.join(orderTypes, ", ") + "\n" +
                "Beskrivelse: " + details + "\n" +
                "Kvalitet: " + quality + "\n" +
                "Bilagstekst: " + account + "\n";
    }
}
