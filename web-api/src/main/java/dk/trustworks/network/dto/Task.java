package dk.trustworks.network.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.Resource;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {
    private String uuid;
    private String name;
    private String type;
    private String projectuuid;
    private Resource<Project> project;

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

    public Resource<Project> getProject() {
        return project;
    }

    public void setProject(Resource<Project> project) {
        this.project = project;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Task{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", project=").append(project);
        sb.append('}');
        return sb.toString();
    }

    public String getProjectuuid() {
        return projectuuid;
    }

    public void setProjectuuid(String projectuuid) {
        this.projectuuid = projectuuid;
    }
}
