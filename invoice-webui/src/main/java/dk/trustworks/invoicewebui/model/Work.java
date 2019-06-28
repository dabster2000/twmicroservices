package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by hans on 28/06/2017.
 */
@Entity
public class Work {

    @Id
    private int id;
    private LocalDate registered;
    private double workduration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskuuid")
    private Task task;

    private String useruuid;

    @Transient
    private User user;

    private String workas;

    @Transient
    private User workasUser;

    public Work() {
    }

    public Work(LocalDate registered, double workduration, User user, Task task) {
        this.registered = registered;
        this.workduration = workduration;
        this.useruuid = user.getUuid();
        this.task = task;
    }

    public Work(LocalDate registered, double workduration, User user, Task task, User workas) {
        this.registered = registered;
        this.workduration = workduration;
        this.useruuid = user.getUuid();
        this.task = task;
        this.workas = workas.getUuid();
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

    public User getUser() {
        return UserService.get().findByUUID(getUseruuid());
    }

    public User getWorkas() {
        return UserService.get().findByUUID(workas);
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
