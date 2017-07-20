package dk.trustworks.network.clients;

import dk.trustworks.network.dto.Invoice;
import dk.trustworks.network.dto.InvoiceStatus;
import dk.trustworks.network.dto.PdfContainer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@FeignClient("invoice-service")
public interface InvoiceClient {

    @RequestMapping(method = GET, value = "/invoices/{uuid}?projection=pdf")
    PdfContainer getInvoicePdf(@PathVariable("uuid") String uuid);

    @RequestMapping(method = GET, value = "/projectsummaries/onMethod/createInvoiceFromProject")
    void createInvoiceFromProject(@RequestParam("projectuuid") String projectuuid, @RequestParam("year") int year, @RequestParam("month") int month);

    @Cacheable("invoices")
    @RequestMapping(method = GET, value = "/invoices/search/findByStatus")
    Resources<Invoice> findByStatus(@RequestParam("status") InvoiceStatus status);

    @Cacheable("invoices")
    @RequestMapping(method = GET, value = "/invoices/search/findByStatusIn")
    Resources<Invoice> findByStatusIn(@RequestParam("statuses") InvoiceStatus... statuses);

    @RequestMapping(method = DELETE, value = "/invoices/{uuid}")
    void deleteInvoice(@PathVariable("uuid") String uuid);

    @RequestMapping(method = PATCH)
    void updateInvoice(@RequestBody Invoice invoice);
}
