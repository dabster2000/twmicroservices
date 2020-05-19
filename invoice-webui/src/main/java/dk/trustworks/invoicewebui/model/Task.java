package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.trustworks.invoicewebui.model.enums.TaskType;
import dk.trustworks.invoicewebui.services.ProjectService;

import java.util.UUID;

public class Task {
    private String uuid;
    private String name;
    private TaskType type;
    private String projectuuid;

    public Task() {
    }

    public Task(String name, Project project) {
        type = TaskType.CONSULTANT;
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.projectuuid = project.getUuid();
    }

    public Task(String name, Project project, TaskType taskType) {
        type = taskType;
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.projectuuid = project.getUuid();
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

    public String getProjectuuid() {
        return projectuuid;
    }

    public void setProjectuuid(String projectuuid) {
        this.projectuuid = projectuuid;
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

    @JsonIgnore
    public Project getProject() {
        return ProjectService.get().findOne(projectuuid);
    }
}
