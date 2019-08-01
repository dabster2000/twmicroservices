package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.CKOExpensePurpose;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseStatus;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseType;
import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cko_expense")
public class CKOExpense {

    @Id
    @GeneratedValue
    private int id;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate eventdate;

    private String useruuid;

    private String description;

    private int price;

    private String comment;

    private double days;

    @Enumerated(EnumType.STRING)
    private CKOExpenseType type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CKOExpenseStatus status;

    @Column(name = "purpose")
    @Enumerated(EnumType.STRING)
    private CKOExpensePurpose purpose;

    public CKOExpense() {
    }

    public CKOExpense(User user) {
        this.useruuid = user.getUuid();
    }

    public CKOExpense(LocalDate eventdate, User user, String description, int price, String comment, double days, CKOExpenseType type, CKOExpenseStatus status, CKOExpensePurpose purpose) {
        this.eventdate = eventdate;
        this.useruuid = user.getUuid();
        this.description = description;
        this.price = price;
        this.comment = comment;
        this.days = days;
        this.type = type;
        this.status = status;
        this.purpose = purpose;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getEventdate() {
        return eventdate;
    }

    public void setEventdate(LocalDate eventdate) {
        this.eventdate = eventdate;
    }

    public User getUser() {
        return UserService.get().findByUUID(getUseruuid());
    }

    public void setUser(User user) {
        this.useruuid = user.getUuid();
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getDays() {
        return days;
    }

    public void setDays(double days) {
        this.days = days;
    }

    public CKOExpenseType getType() {
        return type;
    }

    public void setType(CKOExpenseType type) {
        this.type = type;
    }

    public CKOExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(CKOExpenseStatus status) {
        this.status = status;
    }

    public CKOExpensePurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(CKOExpensePurpose purpose) {
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return "CKOExpense{" +
                "id=" + id +
                ", eventdate=" + eventdate +
                ", user=" + useruuid +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", comment='" + comment + '\'' +
                ", days=" + days +
                ", type=" + type +
                ", status=" + status +
                ", purpose=" + purpose +
                '}';
    }
}
