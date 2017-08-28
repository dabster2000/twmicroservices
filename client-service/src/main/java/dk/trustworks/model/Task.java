package dk.trustworks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.List;

@Entity
public class Task {
    @Id private String uuid;
    private String name;
    private String type;

    @Column(insertable=false, updatable=false)
    private String projectuuid;

    @OneToMany(mappedBy = "task")
    @JsonIgnore private List<Taskworkerconstraint> taskworkerconstraint;

    @ManyToOne()
    @JoinColumn(name = "projectuuid")
    private Project project;

    @OneToMany(mappedBy = "task")
    private List<Budget> budget;

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

    public List<Taskworkerconstraint> getTaskworkerconstraint() {
        return taskworkerconstraint;
    }

    public void setTaskworkerconstraint(List<Taskworkerconstraint> taskworkerconstraint) {
        this.taskworkerconstraint = taskworkerconstraint;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Budget> getBudget() {
        return budget;
    }

    public void setBudget(List<Budget> budget) {
        this.budget = budget;
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
        //sb.append(", taskworkerconstraint=").append(taskworkerconstraint);
        sb.append(", project=").append(project);
        //sb.append(", budget=").append(budget);
        sb.append('}');
        return sb.toString();
    }
}
