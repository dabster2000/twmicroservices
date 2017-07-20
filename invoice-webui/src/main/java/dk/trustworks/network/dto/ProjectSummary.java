package dk.trustworks.network.dto;

/**
 * Created by hans on 12/07/2017.
 */
public class ProjectSummary {

    private String projectuuid;
    private String projectname;
    private String clientname;
    private String description;
    private double amount;
    private int invoices;

    public ProjectSummary() {
    }

    public ProjectSummary(String projectuuid, String projectname, String clientname, String description, double amount, int invoices) {
        this.projectuuid = projectuuid;
        this.projectname = projectname;
        this.clientname = clientname;
        this.description = description;
        this.amount = amount;
        this.invoices = invoices;
    }

    public String getProjectuuid() {
        return projectuuid;
    }

    public void setProjectuuid(String projectuuid) {
        this.projectuuid = projectuuid;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getInvoices() {
        return invoices;
    }

    public void setInvoices(int invoices) {
        this.invoices = invoices;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProjectSummary{");
        sb.append("projectuuid='").append(projectuuid).append('\'');
        sb.append(", projectname='").append(projectname).append('\'');
        sb.append(", clientname='").append(clientname).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", invoices=").append(invoices);
        sb.append('}');
        return sb.toString();
    }

    public void addAmount(double workduration) {
        this.amount += workduration;
    }
}
