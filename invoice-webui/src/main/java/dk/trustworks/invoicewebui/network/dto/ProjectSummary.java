package dk.trustworks.invoicewebui.network.dto;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by hans on 12/07/2017.
 */
public class ProjectSummary {

    private String contractuuid;
    private String projectuuid;
    private String projectname;
    private String clientname;
    private String description;
    private double registeredamount;
    private double invoicedamount;
    private int invoices;
    private HashMap<String, String> errors = new HashMap<>();

    public ProjectSummary() {
    }

    public ProjectSummary(String contractuuid, String projectuuid, String projectname, String clientname, String description, double registeredamount, double invoicedamount, int invoices) {
        this.contractuuid = contractuuid;
        this.projectuuid = projectuuid;
        this.projectname = projectname;
        this.clientname = clientname;
        this.description = description;
        this.registeredamount = registeredamount;
        this.invoicedamount = invoicedamount;
        this.invoices = invoices;
    }

    public String getContractuuid() {
        return contractuuid;
    }

    public void setContractuuid(String contractuuid) {
        this.contractuuid = contractuuid;
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

    public Collection<String> getErrors() {
        return errors.values();
    }

    public void addError(String key, String error) {
        this.errors.put(key, error);
    }

    @Override
    public String toString() {
        return "ProjectSummary{" + "projectuuid='" + projectuuid + '\'' +
                ", projectname='" + projectname + '\'' +
                ", clientname='" + clientname + '\'' +
                ", description='" + description + '\'' +
                ", registeredamount=" + registeredamount +
                ", invoicedamount=" + invoicedamount +
                ", invoices=" + invoices +
                '}';
    }
}
