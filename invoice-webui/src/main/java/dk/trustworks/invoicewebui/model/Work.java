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

    private String useruuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workas")
    private User workas;

    public Work() {
    }

    public Work(LocalDate registered, double workduration, String useruuid, Task task) {
        this.registered = registered;
        this.workduration = workduration;
        this.useruuid = useruuid;
        this.task = task;
    }

    public Work(LocalDate registered, double workduration, String useruuid, Task task, User workas) {
        this.registered = registered;
        this.workduration = workduration;
        this.useruuid = useruuid;
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

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
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
                ", user=" + useruuid +
                ", workas=" + (workas!=null) +
                ", ["+task.getName()+", "+task.getProject().getName()+", "+task.getProject().getClient().getName()+", "+useruuid+"]" +
                '}';
    }
}
