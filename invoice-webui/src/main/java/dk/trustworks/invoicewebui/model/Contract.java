package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.ContractType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Contract {

    @Id
    private String uuid;

    private ContractType contractType;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate activeFrom;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate activeTo;

    private double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientuuid")
    private Client client;

    @OneToMany(mappedBy = "contract", fetch = FetchType.LAZY)
    private List<Consultant> consultants;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "projectcontracts",
            joinColumns = @JoinColumn(name = "contractuuid"),
            inverseJoinColumns = @JoinColumn(name = "projectuuid")
    )
    private List<Project> projects;

    @ManyToOne
    private Contract parent;

    @OneToMany(mappedBy="parent")
    private List<Contract> children;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public Contract() {
        uuid = UUID.randomUUID().toString();
    }

    public Contract(ContractType contractType, LocalDate activeFrom, LocalDate activeTo, double amount, Client client) {
        this();
        this.contractType = contractType;
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
        this.amount = amount;
        this.client = client;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public LocalDate getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(LocalDate activeFrom) {
        this.activeFrom = activeFrom;
    }

    public LocalDate getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(LocalDate activeTo) {
        this.activeTo = activeTo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public Contract getParent() {
        return parent;
    }

    public void setParent(Contract parent) {
        this.parent = parent;
    }

    public List<Contract> getChildren() {
        return children;
    }

    public void setChildren(List<Contract> children) {
        this.children = children;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<Consultant> getConsultants() {
        return consultants;
    }

    public void addConsultants(List<Consultant> consultants) {
        for (Consultant newConsultant : consultants) {
            boolean consultantExists = false;
            for (Consultant consultant : this.consultants) {
                if(consultant.getUser().getUuid().equals(newConsultant.getUser().getUuid())) consultantExists = true;
            }
            if(!consultantExists) consultants.add(newConsultant);
        }
    }

    public void addConsultant(Consultant newConsultant) {
        boolean consultantExists = false;
        for (Consultant consultant : this.consultants) {
            if(consultant.getUser().getUuid().equals(newConsultant.getUser().getUuid())) consultantExists = true;
        }
        if(!consultantExists) consultants.add(newConsultant);
    }

    @Override
    public String toString() {
        return "Contract{" +
                "uuid='" + uuid + '\'' +
                ", contractType=" + contractType +
                ", activeFrom=" + activeFrom +
                ", activeTo=" + activeTo +
                ", amount=" + amount +
                ", created=" + created +
                '}';
    }
}
