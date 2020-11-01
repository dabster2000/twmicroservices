package dk.trustworks.invoicewebui.network.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectSummary {

    @JsonProperty("projectSummaryType")
    private ProjectSummaryType projectSummaryType;
    @JsonProperty("contractuuid")
    private String contractuuid;
    @JsonProperty("projectuuid")
    private String projectuuid;
    @JsonProperty("projectname")
    private String projectname;
    @JsonProperty("client")
    private Client client;
    @JsonProperty("clientname")
    private String clientname;
    @JsonProperty("description")
    private String description;
    @JsonProperty("registeredamount")
    private float registeredamount;
    @JsonProperty("invoicedamount")
    private float invoicedamount;
    @JsonProperty("invoices")
    private int invoices;
    @JsonProperty("invoiceList")
    private List<Invoice> invoiceList = null;
    @JsonProperty("draftInvoiceList")
    private List<Invoice> draftInvoiceList = null;

    @JsonProperty("projectSummaryType")
    public ProjectSummaryType getProjectSummaryType() {
        return projectSummaryType;
    }

    @JsonProperty("projectSummaryType")
    public void setProjectSummaryType(ProjectSummaryType projectSummaryType) {
        this.projectSummaryType = projectSummaryType;
    }

    @JsonProperty("contractuuid")
    public String getContractuuid() {
        return contractuuid;
    }

    @JsonProperty("contractuuid")
    public void setContractuuid(String contractuuid) {
        this.contractuuid = contractuuid;
    }

    @JsonProperty("projectuuid")
    public String getProjectuuid() {
        return projectuuid;
    }

    @JsonProperty("projectuuid")
    public void setProjectuuid(String projectuuid) {
        this.projectuuid = projectuuid;
    }

    @JsonProperty("projectname")
    public String getProjectname() {
        return projectname;
    }

    @JsonProperty("projectname")
    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    @JsonProperty("client")
    public Client getClient() {
        return client;
    }

    @JsonProperty("client")
    public void setClient(Client client) {
        this.client = client;
    }

    @JsonProperty("clientname")
    public String getClientname() {
        return clientname;
    }

    @JsonProperty("clientname")
    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("registeredamount")
    public float getRegisteredamount() {
        return registeredamount;
    }

    @JsonProperty("registeredamount")
    public void setRegisteredamount(float registeredamount) {
        this.registeredamount = registeredamount;
    }

    @JsonProperty("invoicedamount")
    public float getInvoicedamount() {
        return invoicedamount;
    }

    @JsonProperty("invoicedamount")
    public void setInvoicedamount(float invoicedamount) {
        this.invoicedamount = invoicedamount;
    }

    @JsonProperty("invoices")
    public int getInvoices() {
        return invoices;
    }

    @JsonProperty("invoices")
    public void setInvoices(int invoices) {
        this.invoices = invoices;
    }

    @JsonProperty("invoiceList")
    public List<Invoice> getInvoiceList() {
        return invoiceList;
    }

    @JsonProperty("invoiceList")
    public void setInvoiceList(List<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
    }

    @JsonProperty("draftInvoiceList")
    public List<Invoice> getDraftInvoiceList() {
        return draftInvoiceList;
    }

    @JsonProperty("draftInvoiceList")
    public void setDraftInvoiceList(List<Invoice> draftInvoiceList) {
        this.draftInvoiceList = draftInvoiceList;
    }

}

/*
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
*/