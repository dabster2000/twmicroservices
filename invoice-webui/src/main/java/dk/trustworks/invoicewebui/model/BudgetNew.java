package dk.trustworks.invoicewebui.model;

import java.util.UUID;

public class BudgetNew {
    private String uuid;
    private int month;
    private int year;
    private Double budget;
    private String consultantuuid;
    private String projectuuid;

    public BudgetNew() {
        uuid = UUID.randomUUID().toString();
    }

    public BudgetNew(int month, int year, Double budget, String consultantuuid, String projectuuid) {
        this();
        this.month = month;
        this.year = year;
        this.budget = budget;
        this.consultantuuid = consultantuuid;
        this.projectuuid = projectuuid;
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

    public String getProjectuuid() {
        return projectuuid;
    }

    public void setProjectuuid(String projectuuid) {
        this.projectuuid = projectuuid;
    }

    public String getConsultantuuid() {
        return consultantuuid;
    }

    public void setConsultantuuid(String consultantuuid) {
        this.consultantuuid = consultantuuid;
    }

    @Override
    public String toString() {
        return "BudgetNew{" +
                "uuid='" + uuid + '\'' +
                ", month=" + month +
                ", year=" + year +
                ", budget=" + budget +
                ", consultantuuid='" + consultantuuid + '\'' +
                ", projectuuid='" + projectuuid + '\'' +
                '}';
    }
}
