package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.ContractStatus;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class SubContract extends Contract {

    @ManyToOne
    @JoinColumn(name = "parentuuid")
    private MainContract parent;

    public SubContract() {
        super();
    }

    public SubContract(ContractStatus contractStatus, String note, double amount, LocalDate activeTo, MainContract parent) {
        super(contractStatus, note, amount, parent.getContractType(), activeTo);
        this.parent = parent;
    }

    public MainContract getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "SubContract{" +
                "parent=" + parent +
                '}';
    }
}
