package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.model.InvoiceReference;
import dk.trustworks.invoicewebui.network.dto.ProjectSummary;
import dk.trustworks.invoicewebui.network.rest.InvoiceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by hans on 20/07/2017.
 */

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRestService invoiceRestService;

    public List<Invoice> findAll() {
        return invoiceRestService.findAll();
    }

    public List<Invoice> findByPeriod(LocalDate fromdate, LocalDate todate) {
        return invoiceRestService.findByPeriod(fromdate, todate);
    }

    public List<Invoice> getInvoicesForSingleMonthUsingBookingDate(LocalDate month) {
        return invoiceRestService.getInvoicesForSingleMonthUsingBookingDate(month);
    }

    public Invoice findByLatestInvoiceByProjectuuid(String projectuuid) {
        return invoiceRestService.findByLatestInvoiceByProjectuuid(projectuuid);
    }

    public List<ProjectSummary> loadProjectSummaryByYearAndMonth(LocalDate month) {
        return invoiceRestService.loadProjectSummaryByYearAndMonth(month);
    }

    public Invoice createInvoiceFromProject(ProjectSummary projectSummary, LocalDate month) {
        return invoiceRestService.createDraftFromProject(projectSummary, month);
    }

    public Invoice createInvoice(Invoice invoice) {
        return invoiceRestService.createInvoice(invoice);
    }

    public void regeneratePdf(String invoiceuuid) {
        invoiceRestService.regeneratePdf(invoiceuuid);
    }

    public Invoice createPhantomInvoice(Invoice phantomInvoice) {
        return invoiceRestService.createPhantomInvoice(phantomInvoice);
    }

    public Invoice createCreditNote(Invoice invoice) {
        return invoiceRestService.createCreditNote(invoice);
    }

    public void update(Invoice invoice) {
        invoiceRestService.updateDraftInvoice(invoice);
    }

    public void delete(String uuid) {
        invoiceRestService.delete(uuid);
    }

    public void updateInvoiceReference(String invoiceuuid, InvoiceReference invoiceReference) {
        invoiceRestService.updateInvoiceReference(invoiceuuid, invoiceReference);
    }
}