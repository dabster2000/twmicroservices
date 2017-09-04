package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Task {
    @Id private String uuid;
    private String name;
    private String type;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<Taskworkerconstraint> taskworkerconstraint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectuuid")
    private Project project;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<Budget> budget;

    public Task() {
    }

    public Task(String uuid, String name, Project project) {
        type = "CONSULTANT";
        this.uuid = uuid;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Task{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", type='").append(type).append('\'');
        //sb.append(", taskworkerconstraint=").append(taskworkerconstraint);
        //sb.append(", project=").append(project);
        //sb.append(", budget=").append(budget);
        sb.append('}');
        return sb.toString();
    }
}
