package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Created by hans on 28/06/2017.
 */
@Entity
@Table(schema = "timemanager")
public class Work {

    @Id private String uuid;
    private int day;
    private int month;
    private int year;
    private double workduration;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskuuid")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useruuid")
    private User user;

    public Work() {
    }

    public Work(int day, int month, int year, double workduration, User user, Task task) {
        this.uuid = UUID.randomUUID().toString();
        this.day = day;
        this.month = month;
        this.year = year;
        this.workduration = workduration;
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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

    public double getWorkduration() {
        return workduration;
    }

    public void setWorkduration(double workduration) {
        this.workduration = workduration;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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
        final StringBuilder sb = new StringBuilder("Work{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", day=").append(day);
        sb.append(", month=").append(month);
        sb.append(", year=").append(year);
        sb.append(", workduration=").append(workduration);
        sb.append(", created=").append(created);
        sb.append('}');
        return sb.toString();
    }
}
