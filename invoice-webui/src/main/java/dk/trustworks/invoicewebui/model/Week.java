package dk.trustworks.invoicewebui.model;

import javax.persistence.*;

/**
 * Created by hans on 28/06/2017.
 */

@Entity
@Table(schema = "timemanager")
public class Week {

    @Id private String uuid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskuuid")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useruuid")
    private User user;
    private int weeknumber;
    private int year;
    private int sorting;

    public Week() {
    }

    public Week(String uuid, int weeknumber, int year, User user, Task task) {
        this.uuid = uuid;
        this.weeknumber = weeknumber;
        this.year = year;
        this.user = user;
        this.task = task;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getWeeknumber() {
        return weeknumber;
    }

    public void setWeeknumber(int weeknumber) {
        this.weeknumber = weeknumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
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
}
