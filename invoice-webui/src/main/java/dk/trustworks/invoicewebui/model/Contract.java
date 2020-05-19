package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.services.ClientdataService;
import dk.trustworks.invoicewebui.services.ClientService;
import dk.trustworks.invoicewebui.services.ProjectService;
import main.java.com.maximeroussy.invitrode.RandomWord;
import main.java.com.maximeroussy.invitrode.WordLengthException;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    private String uuid;

    private double amount;

    @Column(name = "contracttype")
    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    private String refid;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @OneToMany(mappedBy = "contract", cascade = {
            CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE
    }, fetch = FetchType.EAGER)
    private Set<ContractConsultant> contractConsultants = new HashSet<>();

    @OneToMany(mappedBy = "contract", cascade = {
            CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE
    }, fetch = FetchType.EAGER)
    private Set<ContractProject> contractProjects = new HashSet<>();

    @Column(name = "activefrom")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate activeFrom;

    private String clientuuid;

    @Column(name = "activeto")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate activeTo;

    @Column(name = "parentuuid")
    private String parentuuid;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    private String name;

    private String clientdatauuid;

    private String note;

    public Contract() {
        uuid = UUID.randomUUID().toString();
    }

    public Contract(ContractType contractType, ContractStatus contractStatus, String note, String refid, LocalDate activeFrom, LocalDate activeTo, double amount, Client client) {
        this();
        this.status = contractStatus;
        this.note = note;
        this.refid = refid;
        this.amount = amount;
        this.contractType = contractType;
        this.activeTo = activeTo;
        this.activeFrom = activeFrom;
        this.clientuuid = client.getUuid();
        try {
            this.name = RandomWord.getNewWord(8);
        } catch (WordLengthException e) {
            this.name = "ERROR";
        }
    }

    public Contract(Contract contract) {
        this();
        this.status = ContractStatus.INACTIVE;
        this.note = "";
        this.amount = 0.0;
        this.refid = contract.getRefid();
        this.activeFrom = contract.getActiveTo().plusMonths(1).withDayOfMonth(1);
        this.activeTo = contract.getActiveTo().plusMonths(3).withDayOfMonth(1);
        this.parentuuid = contract.getUuid();
        this.contractType = contract.getContractType();
        this.clientuuid = contract.getClient().getUuid();
        this.clientdatauuid = contract.getClientdatauuid();
        for (ContractConsultant contractConsultant : contract.getContractConsultants()) {
            this.contractConsultants.add(new ContractConsultant(this, contractConsultant.getUser(), contractConsultant.getRate(), contractConsultant.getBudget(), contractConsultant.getHours()));
        }
        this.name = contract.getName();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public LocalDate getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(LocalDate activeTo) {
        this.activeTo = activeTo;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

    public String getClientuuid() {
        return clientuuid;
    }

    public Client getClient() {
        return ClientService.get().findOne(getClientuuid());
    }

    public Set<ContractConsultant> getContractConsultants() {
        return contractConsultants;
    }

    public ContractConsultant findByUser(User user) {
        Optional<ContractConsultant> first = contractConsultants.stream().filter(consultant -> consultant.getUser().getUuid().equals(user.getUuid())).findFirst();
        return first.orElse(null);
    }

    public Set<ContractProject> getContractProjects() {
        return contractProjects;
    }

    public void setContractProjects(Set<ContractProject> contractProjects) {
        this.contractProjects = contractProjects;
    }

    public Set<String> getProjectUuids() {
        return contractProjects.stream().map(ContractProject::getProjectuuid).collect(Collectors.toSet());
    }

    public LocalDate getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(LocalDate activeFrom) {
        this.activeFrom = activeFrom;
    }

    public void addConsultants(List<ContractConsultant> contractConsultants) {
        for (ContractConsultant newContractConsultant : contractConsultants) {
            boolean consultantExists = false;
            for (ContractConsultant contractConsultant : this.contractConsultants) {
                if(contractConsultant.getUser().getUuid().equals(newContractConsultant.getUser().getUuid())) consultantExists = true;
            }
            if(!consultantExists) this.contractConsultants.add(newContractConsultant);
        }
    }

    public void addConsultant(ContractConsultant newContractConsultant) {
        boolean consultantExists = false;
        for (ContractConsultant contractConsultant : this.contractConsultants) {
            if(contractConsultant.getUser().getUuid().equals(newContractConsultant.getUser().getUuid())) consultantExists = true;
        }
        if(!consultantExists) this.contractConsultants.add(newContractConsultant);
    }

    public String getParentuuid() {
        return parentuuid;
    }

    public void setParentuuid(String parentuuid) {
        this.parentuuid = parentuuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void removeProject(Project project) {
        setContractProjects(getContractProjects().stream().filter(contractProject -> !contractProject.getProjectuuid().equals(project.getUuid())).collect(Collectors.toSet()));
    }

    public void addProject(Project project) {
        getContractProjects().add(new ContractProject(this, project));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Contract that = (Contract) o;
        return com.google.common.base.Objects.equal(getUuid(), that.getUuid());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), getUuid());
    }

    @Override
    public String toString() {
        return "Contract{" +
                "uuid='" + uuid + '\'' +
                ", amount=" + amount +
                ", contractType=" + contractType +
                ", status=" + status +
                ", activeFrom=" + activeFrom +
                ", activeTo=" + activeTo +
                ", created=" + created +
                ", note='" + note + '\'' +
                '}';
    }

    public Clientdata getClientdata() {
        return ClientdataService.get().findOne(clientuuid);
    }

    public void setClientdata(Clientdata clientdata) {
        clientdatauuid = clientdata.getUuid();
    }

    public String getClientdatauuid() {
        return clientdatauuid;
    }

    public void setClientdatauuid(String clientdatauuid) {
        this.clientdatauuid = clientdatauuid;
    }

    public Set<Project> getProjects() {
        return getProjectUuids().stream().map(uuid -> ProjectService.get().findOne(uuid)).collect(Collectors.toSet());
    }
}
