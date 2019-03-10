package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.TaskType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TaskOffering> taskOfferings = new ArrayList<>();

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

    public List<TaskOffering> getTaskOfferings() {
        return taskOfferings;
    }

    public void setTaskOfferings(List<TaskOffering> taskOfferings) {
        this.taskOfferings = taskOfferings;
    }

    public void addOffering(TaskOffering taskOffering) {
        taskOfferings.add(taskOffering);
        taskOffering.setTask(this);
    }

    public void addOfferings(Set<TaskOffering> taskOfferingList) {
        taskOfferings.addAll(taskOfferingList);
        for (TaskOffering taskOffering : taskOfferingList) {
            taskOffering.setTask(this);
        }
    }

    public void removeOffering(TaskOffering taskOffering) {
        taskOfferings = taskOfferings.stream().filter(taskOffering1 -> !taskOffering1.getName().equals(taskOffering.getName())).collect(Collectors.toList());
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
