package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue
    private int id;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate receiptdate;

    private String useruuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectuuid")
    private Project project;

    private String description;

    private float amount;

    public Receipt() {
    }

    public Receipt(LocalDate receiptdate, User user, Project project, String description, float amount) {
        this.receiptdate = receiptdate;
        this.useruuid = user.getUuid();
        this.project = project;
        this.description = description;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getReceiptdate() {
        return receiptdate;
    }

    public void setReceiptdate(LocalDate receiptdate) {
        this.receiptdate = receiptdate;
    }

    public User getUser() {
        return UserService.get().findByUUID(getUseruuid());
    }

    public void setUser(User user) {
        this.useruuid = user.getUuid();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "id=" + id +
                ", receiptdate=" + receiptdate +
                ", user=" + useruuid +
                ", project=" + project.getName() +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                '}';
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }
}
