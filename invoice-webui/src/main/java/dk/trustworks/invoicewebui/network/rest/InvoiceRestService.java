package dk.trustworks.invoicewebui.network.rest;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.model.InvoiceReference;
import dk.trustworks.invoicewebui.network.dto.ProjectSummary;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Service
public class InvoiceRestService {

    @Value("#{environment.APIGATEWAY_URL}")
    private String apiGatewayUrl;

    private final SystemRestService systemRestService;

    @Autowired
    public InvoiceRestService(SystemRestService systemRestService) {
        this.systemRestService = systemRestService;
    }

    public List<Invoice> findAll() {
        String url = apiGatewayUrl + "/invoices";
        ResponseEntity<Invoice[]> result = systemRestService.secureCall(url, GET, Invoice[].class);
        return Arrays.asList(result.getBody());
    }

    public List<Invoice> findByPeriod(LocalDate fromdate, LocalDate todate) {
        String url = apiGatewayUrl + "/invoices?fromdate="+fromdate+"&todate="+todate;
        ResponseEntity<Invoice[]> result = systemRestService.secureCall(url, GET, Invoice[].class);
        return Arrays.asList(result.getBody());
    }

    /*
    @GET
    @Path("/search/bookingdate")
    public List<Invoice> getInvoicesByBookingdate(@QueryParam("fromdate") String fromdate, @QueryParam("todate") String todate) {
     */

    public List<Invoice> getInvoicesForSingleMonthUsingBookingDate(LocalDate month) {
        String url = apiGatewayUrl + "/invoices/search/bookingdate?fromdate="+DateUtils.stringIt(month.withDayOfMonth(1))+"&todate="+DateUtils.stringIt(month.withDayOfMonth(1).plusMonths(1));
        ResponseEntity<Invoice[]> result = systemRestService.secureCall(url, GET, Invoice[].class);
        return Arrays.asList(result.getBody());
    }

    public List<ProjectSummary> loadProjectSummaryByYearAndMonth(LocalDate month) {
        String url = apiGatewayUrl + "/invoices/candidates/months/"+DateUtils.stringIt(month);
        System.out.println("url = " + url);
        ResponseEntity<ProjectSummary[]> result = systemRestService.secureCall(url, GET, ProjectSummary[].class);
        return Arrays.asList(result.getBody());
    }

    public Invoice createDraftFromProject(ProjectSummary projectSummary, LocalDate month) {
        String url = apiGatewayUrl + "/invoices/drafts?" +
                "contractuuid="+projectSummary.getContractuuid()+
                "&projectuuid="+projectSummary.getProjectuuid()+
                "&type="+projectSummary.getProjectSummaryType()+
                "&month="+DateUtils.stringIt(month.withDayOfMonth(1));
        ResponseEntity<Invoice> result = null;
        try {
            result = systemRestService.secureCall(url, POST, Invoice.class, projectSummary);
        } catch (HttpClientErrorException e) {
            Notification notification=new Notification("Error creating draft",
                    e.getMessage() +" : "+ e.getStatusText(), Notification.Type.TRAY_NOTIFICATION);
            notification.show(Page.getCurrent());
        }
        assert result != null;
        return result.getBody();
    }

    public void updateDraftInvoice(Invoice invoice) {
        String url = apiGatewayUrl + "/invoices/drafts";
        systemRestService.secureCall(url, PUT, Invoice.class, invoice);
    }

    public Invoice createInvoice(Invoice draftInvoice) {
        String url = apiGatewayUrl + "/invoices";
        ResponseEntity<Invoice> result = systemRestService.secureCall(url, POST, Invoice.class, draftInvoice);
        return result.getBody();
    }

    public Invoice createPhantomInvoice(Invoice draftInvoice) {
        String url = apiGatewayUrl + "/invoices/phantoms";
        ResponseEntity<Invoice> result = systemRestService.secureCall(url, POST, Invoice.class, draftInvoice);
        return result.getBody();
    }

    public Invoice createCreditNote(Invoice draftInvoice) {
        String url = apiGatewayUrl + "/invoices/creditnotes";
        ResponseEntity<Invoice> result = systemRestService.secureCall(url, POST, Invoice.class, draftInvoice);
        return result.getBody();
    }

    public void delete(String uuid) {
        String url = apiGatewayUrl + "/invoices/drafts/"+uuid;
        systemRestService.secureCall(url, DELETE, Void.class);
    }

    public List<Invoice> findProjectInvoices(String projectuuid) {
        String url = apiGatewayUrl + "/projects/"+projectuuid+"/invoices";
        ResponseEntity<Invoice[]> result = systemRestService.secureCall(url, GET, Invoice[].class);
        return Arrays.asList(result.getBody());
    }

    public Invoice findByLatestInvoiceByProjectuuid(String projectuuid) {
        System.out.println("InvoiceRestService.findByLatestInvoiceByProjectuuid");
        System.out.println("projectuuid = " + projectuuid);
        return findProjectInvoices(projectuuid).get(0);
    }

    public void updateInvoiceReference(String invoiceuuid, InvoiceReference invoiceReference) {
        String url = apiGatewayUrl + "/invoices/"+invoiceuuid+"/reference";
        System.out.println("InvoiceRestService.updateInvoiceReference");
        System.out.println("invoiceuuid = " + invoiceuuid + ", invoiceReference = " + invoiceReference);
        systemRestService.secureCall(url, POST, InvoiceReference.class, invoiceReference);

    }
}