package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.services.ProjectService;

import javax.persistence.*;

@Entity
@Table(name = "projectdescription_projects")
public class ProjectDesctiptionProject {

    @Id
    @GeneratedValue
    private int id;

    private String projectuuid;

    @ManyToOne()
    @JoinColumn(name="projectdescid")
    private ProjectDescription projectDescription;

    public ProjectDesctiptionProject() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectuuid() {
        return projectuuid;
    }

    public void setProjectuuid(String projectuuid) {
        this.projectuuid = projectuuid;
    }

    public Project getProject() {
        return ProjectService.get().findOne(projectuuid);
    }

    public void setProject(Project project) {
        this.projectuuid = project.getUuid();
    }

    public ProjectDescription getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(ProjectDescription projectDescription) {
        this.projectDescription = projectDescription;
    }

    @Override
    public String toString() {
        return "ProjectDesctiptionProject{" +
                "id=" + id +
                ", projectuuid='" + projectuuid + '\'' +
                ", projectDescription=" + projectDescription +
                '}';
    }
}
