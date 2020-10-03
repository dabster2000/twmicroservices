package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.ContractStatus;
import dk.trustworks.invoicewebui.model.enums.ContractType;
import dk.trustworks.invoicewebui.services.ClientdataService;
import main.java.com.maximeroussy.invitrode.RandomWord;
import main.java.com.maximeroussy.invitrode.WordLengthException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Contract {

    @JsonProperty("uuid")
    public String uuid;
    @JsonProperty("amount")
    public double amount;
    @JsonProperty("contractType")
    public ContractType contractType;
    @JsonProperty("refid")
    public String refid;
    @JsonProperty("status")
    public ContractStatus status;
    @JsonProperty("activeFrom")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate activeFrom;
    @JsonProperty("clientuuid")
    public String clientuuid;
    @JsonProperty("activeTo")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate activeTo;
    @JsonProperty("parentuuid")
    public String parentuuid;
    @JsonProperty("created")
    public String created;
    @JsonProperty("name")
    public String name;
    @JsonProperty("clientdatauuid")
    public String clientdatauuid;
    @JsonProperty("note")
    public String note;
    @JsonProperty("contractConsultants")
    public List<ContractConsultant> contractConsultants = null;
    @JsonProperty("contractProjects")
    public List<Object> contractProjects = null;

    /*
    private String uuid;

    private double amount;

    @JsonProperty("contractType")
    private ContractType contractType;

    private String refid;

    private ContractStatus status;

    private Set<ContractConsultant> contractConsultants = new HashSet<>();

    @JsonProperty("activeFrom")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate activeFrom;

    private String clientuuid;

    @JsonProperty("activeTo")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate activeTo;

    private String parentuuid;

    private String name;

    private String clientdatauuid;

    private String note;

     */

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
        this.clientuuid = contract.getClientuuid();
        this.clientdatauuid = contract.getClientdatauuid();
        this.name = contract.getName();
        this.contractConsultants.addAll(contract.getContractConsultants());
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

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public List<ContractConsultant> getContractConsultants() {
        return contractConsultants;
    }

    public void setContractConsultants(List<ContractConsultant> contractConsultants) {
        this.contractConsultants = contractConsultants;
    }

    public String getClientuuid() {
        return clientuuid;
    }

    public void setClientuuid(String clientuuid) {
        this.clientuuid = clientuuid;
    }

    public LocalDate getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(LocalDate activeTo) {
        this.activeTo = activeTo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(LocalDate activeFrom) {
        this.activeFrom = activeFrom;
    }

    public String getParentuuid() {
        return parentuuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Clientdata getClientdata() {
        return ClientdataService.get().findOne(clientdatauuid);
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
                ", refid='" + refid + '\'' +
                ", status=" + status +
                ", activeFrom=" + activeFrom +
                ", clientuuid='" + clientuuid + '\'' +
                ", activeTo=" + activeTo +
                ", parentuuid='" + parentuuid + '\'' +
                ", name='" + name + '\'' +
                ", clientdatauuid='" + clientdatauuid + '\'' +
                ", note='" + note + '\'' +
                ", contractConsultants=" + contractConsultants.size() +
                '}';
    }
}
