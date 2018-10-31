package dk.trustworks.invoicewebui.model;

import javax.persistence.*;

@Entity
@Table(name = "projectdescription_projects")
public class ProjectDesctiptionProject {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne()
    @JoinColumn(name="projectuuid")
    private Project project;

    @ManyToOne()
    @JoinColumn(name="projectdescid")
    private ProjectDescription projectDescription;

    public ProjectDesctiptionProject() {
    }

    public ProjectDesctiptionProject(Project project, ProjectDescription projectDescription) {
        this.project = project;
        this.projectDescription = projectDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectDescription getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(ProjectDescription projectDescription) {
        this.projectDescription = projectDescription;
    }

    @Override
    public String toString() {
        return "ProjectDesctiptionProjects{" +
                "id=" + id +
                ", project=" + project +
                ", projectDescription=" + projectDescription +
                '}';
    }
}
