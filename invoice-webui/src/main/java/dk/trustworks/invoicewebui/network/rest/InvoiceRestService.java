package dk.trustworks.invoicewebui.network.rest;

import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.network.dto.ProjectSummary;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Service
public class InvoiceRestService {

    @Value("#{environment.APISERVICE_URL}")
    private String apiServiceUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public InvoiceRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Invoice> findAll() {
        String url = apiServiceUrl + "/invoices";
        ResponseEntity<Invoice[]> result = systemRestService.secureCall(url, GET, Invoice[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Invoice> findByPeriod(LocalDate fromdate, LocalDate todate) {
        String url = apiServiceUrl + "/invoices?fromdate="+fromdate+"&todate="+todate;
        ResponseEntity<Invoice[]> result = systemRestService.secureCall(url, GET, Invoice[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Invoice> getInvoicesForSingleMonth(LocalDate month) {
        String url = apiServiceUrl + "/invoices/months/"+ DateUtils.stringIt(month);
        ResponseEntity<Invoice[]> result = systemRestService.secureCall(url, GET, Invoice[].class);
        return Arrays.asList(result.getBody());
    }

    public List<ProjectSummary> loadProjectSummaryByYearAndMonth(int year, int month) {
        String url = apiServiceUrl + "/invoices/candidates/"+year+"/"+month;
        ResponseEntity<ProjectSummary[]> result = systemRestService.secureCall(url, GET, ProjectSummary[].class);
        return Arrays.asList(result.getBody());
    }

    public Invoice createDraftFromProject(ProjectSummary projectSummary, LocalDate month) {
        String url = apiServiceUrl + "/invoices/drafts?month="+DateUtils.stringIt(month);
        ResponseEntity<Invoice> result = systemRestService.secureCall(url, POST, Invoice.class, projectSummary);
        return result.getBody();
    }

    public Invoice updateDraftInvoice(Invoice invoice) {
        String url = apiServiceUrl + "/invoices/drafts";
        ResponseEntity<Invoice> result = systemRestService.secureCall(url, PUT, Invoice.class, invoice);
        return result.getBody();
    }

    public Invoice createInvoice(Invoice draftInvoice) {
        String url = apiServiceUrl + "/invoices";
        ResponseEntity<Invoice> result = systemRestService.secureCall(url, POST, Invoice.class, draftInvoice);
        return result.getBody();
    }

    public Invoice createPhantomInvoice(Invoice draftInvoice) {
        String url = apiServiceUrl + "/invoices/phantoms";
        ResponseEntity<Invoice> result = systemRestService.secureCall(url, POST, Invoice.class, draftInvoice);
        return result.getBody();
    }

    public Invoice createCreditNote(Invoice draftInvoice) {
        String url = apiServiceUrl + "/invoices/creditnotes";
        ResponseEntity<Invoice> result = systemRestService.secureCall(url, POST, Invoice.class, draftInvoice);
        return result.getBody();
    }

    public void delete(String uuid) {
        String url = apiServiceUrl + "/invoices/"+uuid;
        systemRestService.secureCall(url, PUT, Void.class);
    }

    public Invoice findByLatestInvoiceByProjectuuid(String projectuuid) {
        return null;
    }
}
