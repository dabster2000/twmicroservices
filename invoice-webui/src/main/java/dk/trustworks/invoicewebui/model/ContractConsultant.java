package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.trustworks.invoicewebui.services.UserService;

import java.util.Objects;
import java.util.UUID;

public class ContractConsultant {

    private String uuid;

    private String contractuuid;

    private String useruuid;

    private double rate;

    private double budget;

    private double hours;

    public ContractConsultant() {
    }

    public ContractConsultant(String contractuuid, String useruuid, double rate, double budget, double hours) {
        uuid = UUID.randomUUID().toString();
        this.contractuuid = contractuuid;
        this.useruuid = useruuid;
        this.rate = rate;
        this.budget = budget;
        this.hours = hours;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonIgnore
    public User getUser() {
        return UserService.get().findByUUID(getUseruuid());
    }

    @JsonIgnore
    public void setUser(User user) {
        this.useruuid = user.getUuid();
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public String getContractuuid() {
        return contractuuid;
    }

    public void setContractuuid(String contractuuid) {
        this.contractuuid = contractuuid;
    }

    @Override
    public String toString() {
        return "ContractConsultant{" +
                "uuid='" + uuid + '\'' +
                ", contractuuid='" + contractuuid + '\'' +
                ", useruuid='" + useruuid + '\'' +
                ", rate=" + rate +
                ", budget=" + budget +
                ", hours=" + hours +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractConsultant that = (ContractConsultant) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid);
    }

    public String getUseruuid() {
        return useruuid;
    }

    public void setUseruuid(String useruuid) {
        this.useruuid = useruuid;
    }
}
