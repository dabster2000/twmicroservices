package dk.trustworks.invoicewebui.model;

import javax.persistence.*;

@Entity
@Table(name = "projectdescriptions")
public class ProjectDescription {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientuuid")
    private Client client;

    private String name;

    @Lob
    private String description;

    public ProjectDescription() {
    }

    public ProjectDescription(Client client, String name, String description) {
        this.client = client;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ProjectDescription{" +
                "id=" + id +
                ", client=" + client +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
