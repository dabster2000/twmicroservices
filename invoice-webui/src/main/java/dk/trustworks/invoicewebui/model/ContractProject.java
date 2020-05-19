package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.services.UserService;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "contract_project")
public class ContractProject {

    @Id
    @GeneratedValue
    private String id;

    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.LAZY)
    @JoinColumn(name = "contractuuid")
    private Contract contract;

    private String projectuuid;

    public ContractProject() {
    }

    public ContractProject(Contract contract, String projectuuid) {
        this.contract = contract;
        this.projectuuid = projectuuid;
    }

    public ContractProject(Contract contract, Project project) {
        this.contract = contract;
        this.projectuuid = project.getUuid();
    }

    public String getId() {
        return id;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public String getProjectuuid() {
        return projectuuid;
    }

    public void setProjectuuid(String projectuuid) {
        this.projectuuid = projectuuid;
    }
}
