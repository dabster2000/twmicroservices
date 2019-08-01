package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.*;

@Entity
@Table(name = "projectdescription_users")
public class ProjectDescriptionUser {

    @Id
    @GeneratedValue
    private int id;

    private String useruuid;

    @ManyToOne()
    @JoinColumn(name="projectdescid")
    private ProjectDescription projectDescription;

    @Lob
    private String description;

    public ProjectDescriptionUser() {
    }

    public ProjectDescriptionUser(User user, ProjectDescription projectDescription, String description) {
        this.useruuid = user.getUuid();
        this.projectDescription = projectDescription;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return UserService.get().findByUUID(getUseruuid());
    }

    public void setUser(User user) {
        this.useruuid = user.getUuid();
    }

    public ProjectDescription getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(ProjectDescription projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ProjectDesctiptionUsers{" +
                "id=" + id +
                ", user=" + useruuid +
                ", projectDescription=" + projectDescription +
                ", description='" + description + '\'' +
                '}';
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }
}
