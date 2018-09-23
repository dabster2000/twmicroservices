package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.ItBudgetStatus;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by hans on 23/06/2017.
 */
@Entity
@Table(name = "itbudget")
public class ItBudgetItem {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="useruuid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    private ITBudgetCategory category;

    private String description;

    private int price;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ItBudgetStatus status;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate invoicedate;

    public ItBudgetItem() {
    }

    public ItBudgetItem(User user, ITBudgetCategory category, String description, int price, ItBudgetStatus status, LocalDate invoicedate) {
        this.user = user;
        this.category = category;
        this.description = description;
        this.price = price;
        this.status = status;
        this.invoicedate = invoicedate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ITBudgetCategory getCategory() {
        return category;
    }

    public void setCategory(ITBudgetCategory category) {
        this.category = category;
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

    public LocalDate getInvoicedate() {
        return invoicedate;
    }

    public void setInvoicedate(LocalDate invoicedate) {
        this.invoicedate = invoicedate;
    }

    public ItBudgetStatus getStatus() {
        return status;
    }

    public void setStatus(ItBudgetStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ItBudgetItem{" +
                "id=" + id +
                ", user=" + user +
                ", category=" + category +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", invoicedate=" + invoicedate +
                '}';
    }
}
