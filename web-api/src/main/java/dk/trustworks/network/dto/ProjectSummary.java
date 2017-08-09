package dk.trustworks.network.dto;

/**
 * Created by hans on 12/07/2017.
 */
public class ProjectSummary {

    private String projectuuid;
    private String projectname;
    private String clientname;
    private String description;
    private double registeredamount;
    private double invoicedamount;
    private int invoices;

    public ProjectSummary() {
    }

    public ProjectSummary(String projectuuid, String projectname, String clientname, String description, double registeredamount, double invoicedamount, int invoices) {
        this.projectuuid = projectuuid;
        this.projectname = projectname;
        this.clientname = clientname;
        this.description = description;
        this.registeredamount = registeredamount;
        this.invoicedamount = invoicedamount;
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

    public double getRegisteredamount() {
        return registeredamount;
    }

    public void setRegisteredamount(double registeredamount) {
        this.registeredamount = registeredamount;
    }

    public int getInvoices() {
        return invoices;
    }

    public void setInvoices(int invoices) {
        this.invoices = invoices;
    }

    public void addAmount(double workduration) {
        this.registeredamount += workduration;
    }

    public double getInvoicedamount() {
        return invoicedamount;
    }

    public void setInvoicedamount(double invoicedamount) {
        this.invoicedamount = invoicedamount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProjectSummary{");
        sb.append("projectuuid='").append(projectuuid).append('\'');
        sb.append(", projectname='").append(projectname).append('\'');
        sb.append(", clientname='").append(clientname).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", registeredamount=").append(registeredamount);
        sb.append(", invoicedamount=").append(invoicedamount);
        sb.append(", invoices=").append(invoices);
        sb.append('}');
        return sb.toString();
    }
}
