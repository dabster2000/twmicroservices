package dk.trustworks.invoicewebui.model;

import javax.persistence.*;

@Entity
@Table(name = "projectdescription_users")
public class ProjectDescriptionUser {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne()
    @JoinColumn(name="useruuid")
    private User user;

    @ManyToOne()
    @JoinColumn(name="projectdescid")
    private ProjectDescription projectDescription;

    @Lob
    private String description;

    public ProjectDescriptionUser() {
    }

    public ProjectDescriptionUser(User user, ProjectDescription projectDescription, String description) {
        this.user = user;
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
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
                ", user=" + user +
                ", projectDescription=" + projectDescription +
                ", description='" + description + '\'' +
                '}';
    }
}
