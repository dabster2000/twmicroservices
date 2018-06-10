package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.TaskType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Task {
    @Id private String uuid;
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TaskType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectuuid")
    private Project project;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<Work> workList = new ArrayList<>();

    public Task() {
    }

    public Task(String name, Project project) {
        type = TaskType.CONSULTANT;
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.project = project;
    }

    public Task(String name, Project project, TaskType taskType) {
        type = taskType;
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.project = project;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Work> getWorkList() {
        return workList;
    }

    @Override
    public String toString() {
        return "Task{" + "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return uuid.equals(task.uuid);
    }
}
