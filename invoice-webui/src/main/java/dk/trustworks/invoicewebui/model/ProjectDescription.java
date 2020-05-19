package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.services.ClientService;

import javax.persistence.*;

@Entity
@Table(name = "projectdescriptions")
public class ProjectDescription {

    @Id
    @GeneratedValue
    private int id;

    private String clientuuid;

    private String name;

    @Lob
    private String description;

    public ProjectDescription() {
    }

    public ProjectDescription(Client client, String name, String description) {
        this.clientuuid = client.getUuid();
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientuuid() {
        return clientuuid;
    }

    public void setClientuuid(String clientuuid) {
        this.clientuuid = clientuuid;
    }

    public Client getClient() {
        return ClientService.get().findOne(clientuuid);
    }

    public void setClient(Client client) {
        this.clientuuid = client.getUuid();
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
                ", clientuuid=" + clientuuid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
