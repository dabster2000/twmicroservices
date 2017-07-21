package dk.trustworks.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Task {
    @Id private String uuid;
    private String name;
    private String type;

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

}