package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Taskworkerconstraint {
    @Id private String uuid;
    private Double price;

    @ManyToOne()
    @JoinColumn(name = "taskuuid")
    private Task task;

    @ManyToOne()
    @JoinColumn(name = "useruuid")
    private User user;

    public Taskworkerconstraint() {
    }

    public Taskworkerconstraint(double price, User user, Task task) {
        this.price = price;
        this.user = user;
        this.task = task;
        uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Taskworkerconstraint{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", price=").append(price);
        sb.append('}');
        return sb.toString();
    }
}
