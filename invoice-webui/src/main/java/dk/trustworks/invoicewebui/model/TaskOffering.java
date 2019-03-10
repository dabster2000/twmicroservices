package dk.trustworks.invoicewebui.model;


import javax.persistence.*;

@Table(name = "task_offering")
@Entity
public class TaskOffering {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskuuid")
    private Task task;

    private String name;

    public TaskOffering() {
    }

    public TaskOffering(Task task, String name) {
        this.task = task;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TaskType{" +
                "id='" + id + '\'' +
                ", task=" + task +
                ", name='" + name + '\'' +
                '}';
    }
}
