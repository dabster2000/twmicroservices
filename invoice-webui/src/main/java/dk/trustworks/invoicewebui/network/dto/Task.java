package dk.trustworks.invoicewebui.network.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {
    private String uuid;
    private String name;
    private String type;
    private String projectuuid;
    private Project project;

    public Task() {
    }

    public Task(String name) {
        this.name = name;
    }

    public Task(String name, String projectuuid) {
        this.name = name;
        this.projectuuid = projectuuid;
        this.setType("KONSULENT");
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getProjectuuid() {
        return projectuuid;
    }

    public void setProjectuuid(String projectuuid) {
        this.projectuuid = projectuuid;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Task{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", projectuuid='").append(projectuuid).append('\'');
        sb.append(", project=").append(project);
        sb.append('}');
        return sb.toString();
    }
}
