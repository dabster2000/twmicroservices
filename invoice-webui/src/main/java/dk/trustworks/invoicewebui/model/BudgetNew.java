package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.services.ProjectService;

import javax.persistence.*;
import java.util.UUID;

public class BudgetNew {
    private String uuid;
    private int month;
    private int year;
    private Double budget;

    private ContractConsultant contractConsultant;

    private String projectuuid;

    public BudgetNew() {
        uuid = UUID.randomUUID().toString();
    }

    public BudgetNew(int month, int year, Double budget, ContractConsultant contractConsultant, Project project) {
        this();
        this.month = month;
        this.year = year;
        this.budget = budget;
        this.contractConsultant = contractConsultant;
        this.projectuuid = project.getUuid();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public ContractConsultant getContractConsultant() {
        return contractConsultant;
    }

    public void setContractConsultant(ContractConsultant contractConsultant) {
        this.contractConsultant = contractConsultant;
    }

    public Project getProject() {
        return ProjectService.get().findOne(projectuuid);
    }

    public void setProject(Project project) {
        this.projectuuid = project.getUuid();
    }

    @Override
    public String toString() {
        return "BudgetNew{" +
                "uuid='" + uuid + '\'' +
                ", month=" + month +
                ", year=" + year +
                ", budget=" + budget +
                '}';
    }
}
