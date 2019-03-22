package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by hans on 28/06/2017.
 */
@Entity
@Table(schema = "timemanager")
public class Work {

    @Id
    private int id;
    private LocalDate registered;
    private double workduration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskuuid")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useruuid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workas")
    private User workas;

    public Work() {
    }

    public Work(LocalDate registered, double workduration, User user, Task task) {
        this.registered = registered;
        this.workduration = workduration;
        this.user = user;
        this.task = task;
    }

    public Work(LocalDate registered, double workduration, User user, Task task, User workas) {
        this.registered = registered;
        this.workduration = workduration;
        this.user = user;
        this.task = task;
        this.workas = workas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getRegistered() {
        return registered;
    }

    public void setRegistered(LocalDate registered) {
        this.registered = registered;
    }

    public double getWorkduration() {
        return workduration;
    }

    public void setWorkduration(double workduration) {
        this.workduration = workduration;
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

    @Override
    public String toString() {
        return "Work{" +
                "id='" + id + '\'' +
                ", workduration=" + workduration +
                ", task=" + task.getUuid() +
                ", user=" + user.getUuid() +
                ", workas=" + (workas!=null) +
                ", ["+task.getName()+", "+task.getProject().getName()+", "+task.getProject().getClient().getName()+", "+user.getUsername()+"]" +
                '}';
    }
}
