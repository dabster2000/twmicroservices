package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "taskworkerconstraintbudget")
public class Budget {
    @Id
    private String uuid;
    private int month;
    private int year;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private Double budget;

    @ManyToOne()
    @JoinColumn(name = "taskuuid")
    private Task task;

    @ManyToOne()
    @JoinColumn(name = "useruuid")
    private User user;

    public Budget() {
    }

    public Budget(int month, int year, double budget, User user, Task task) {
        uuid = UUID.randomUUID().toString();
        this.month = month;
        this.year = year;
        this.budget = budget;
        this.user = user;
        this.task = task;
        this.created = new Date();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
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
        final StringBuilder sb = new StringBuilder("Budget{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", month=").append(month);
        sb.append(", year=").append(year);
        sb.append(", created=").append(created);
        sb.append(", budget=").append(budget);
        sb.append(", task=").append(task);
        sb.append(", user=").append(user);
        sb.append('}');
        return sb.toString();
    }
}
