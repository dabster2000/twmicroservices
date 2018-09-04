package dk.trustworks.invoicewebui.model;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "contract_consultants")
public class ContractConsultant {

    @Id
    private String uuid;

    @ManyToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.LAZY)
    @JoinColumn(name = "contractuuid")
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useruuid")
    private User user;

    private double rate;

    private double budget;

    private double hours;

    @OneToMany(mappedBy = "contractConsultant", fetch = FetchType.LAZY)
    private List<BudgetNew> budgets;

    public ContractConsultant() {
        uuid = UUID.randomUUID().toString();
    }

    public ContractConsultant(Contract contract, User user, double rate, double budget, double hours) {
        this();
        this.contract = contract;
        this.user = user;
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

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<BudgetNew> getBudgets() {
        return budgets;
    }

    public void setBudgets(List<BudgetNew> budgets) {
        this.budgets = budgets;
    }

    @Override
    public String toString() {
        return "Consultant{" +
                "uuid='" + uuid + '\'' +
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
}
