package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseType;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cko_expense")
public class CKOExpense {

    @Id
    private int id;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate eventdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useruuid")
    private User user;

    private String description;

    private int price;

    @Enumerated(EnumType.STRING)
    private CKOExpenseType type;

    public CKOExpense() {
    }

    public CKOExpense(LocalDate eventdate, User user, String description, int price, CKOExpenseType type) {
        this.eventdate = eventdate;
        this.user = user;
        this.description = description;
        this.price = price;
        this.type = type;
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
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public CKOExpenseType getType() {
        return type;
    }

    public void setType(CKOExpenseType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CKOExpense{" +
                "id=" + id +
                ", eventdate=" + eventdate +
                ", user=" + user +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", type=" + type +
                '}';
    }
}
