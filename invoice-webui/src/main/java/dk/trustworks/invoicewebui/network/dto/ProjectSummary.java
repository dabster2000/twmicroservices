package dk.trustworks.invoicewebui.network.dto;

import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.network.dto.enums.ProjectSummaryType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hans on 12/07/2017.
 */
public class ProjectSummary {

    private ProjectSummaryType projectSummaryType;
    private String contractuuid;
    private String projectuuid;
    private String projectname;
    private Client client;
    private String clientname;
    private String description;
    private double registeredamount;
    private double invoicedamount;
    private int invoices;
    private List<Invoice> invoiceList = new ArrayList<>();
    private List<Invoice> draftInvoiceList = new ArrayList<>();
    private HashMap<String, String> errors = new HashMap<>();

    public ProjectSummary() {
    }

    public ProjectSummary(String contractuuid, String projectuuid, String projectname, Client client, String clientname, String description, double registeredamount, double invoicedamount, int invoices, ProjectSummaryType projectSummaryType) {
        this.contractuuid = contractuuid;
        this.projectuuid = projectuuid;
        this.projectname = projectname;
        this.client = client;
        this.clientname = clientname;
        this.description = description;
        this.registeredamount = registeredamount;
        this.invoicedamount = invoicedamount;
        this.invoices = invoices;
        this.projectSummaryType = projectSummaryType;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public List<Invoice> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(List<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
    }

    public List<Invoice> getDraftInvoiceList() {
        return draftInvoiceList;
    }

    public void setDraftInvoiceList(List<Invoice> draftInvoiceList) {
        this.draftInvoiceList = draftInvoiceList;
    }

    public ProjectSummaryType getProjectSummaryType() {
        return projectSummaryType;
    }

    public void setProjectSummaryType(ProjectSummaryType projectSummaryType) {
        this.projectSummaryType = projectSummaryType;
    }

    @Override
    public String toString() {
        return "ProjectSummary{" +
                "projectSummaryType=" + projectSummaryType +
                ", contractuuid='" + contractuuid + '\'' +
                ", projectuuid='" + projectuuid + '\'' +
                ", projectname='" + projectname + '\'' +
                ", clientname='" + clientname + '\'' +
                ", description='" + description + '\'' +
                ", registeredamount=" + registeredamount +
                ", invoicedamount=" + invoicedamount +
                ", invoices=" + invoices +
                '}';
    }
}
