package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Invoice;
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

    public List<Invoice> getInvoicesForSingleMonth(LocalDate month) {
        return invoiceRestService.getInvoicesForSingleMonth(month);
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
}