package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by hans on 28/06/2017.
 */
@Entity
public class WorkWithRate {

    @Id
    private int id;
    private int day;
    private int month;
    private int year;
    private double workduration;
    private double rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskuuid")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useruuid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workas")
    private User workas;

    public WorkWithRate() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
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

    public User getWorkas() {
        return workas;
    }

    public void setWorkas(User workas) {
        this.workas = workas;
    }

    public LocalDate getDate() {
        return LocalDate.of(year, month+1, day);
    }

    @Override
    public String toString() {
        return "Work{" +
                "id='" + id + '\'' +
                ", date=" + getDate() +
                ", workduration=" + workduration +
                ", task=" + task.getUuid() +
                ", user=" + user.getUuid() +
                ", workas=" + (workas!=null) +
                ", ["+task.getName()+", "+task.getProject().getName()+", "+task.getProject().getClient().getName()+", "+user.getUsername()+"]" +
                '}';
    }
}
