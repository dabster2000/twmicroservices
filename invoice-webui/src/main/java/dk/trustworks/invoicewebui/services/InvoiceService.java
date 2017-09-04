package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.model.InvoiceItem;
import dk.trustworks.invoicewebui.model.InvoiceStatus;
import dk.trustworks.invoicewebui.model.InvoiceType;
import dk.trustworks.invoicewebui.repositories.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by hans on 20/07/2017.
 */

@Service
public class InvoiceService {

    protected static Logger logger = LoggerFactory.getLogger(InvoiceService.class.getName());

    @Autowired
    private InvoiceRepository invoiceClient;

    public void createCreditNota(@RequestBody Invoice invoice) {
        System.out.println("InvoiceController.createCreditNota");
        System.out.println("invoice = [" + invoice + "]");
        invoice.status = InvoiceStatus.CREDIT_NOTE;
        invoiceClient.save(invoice);

        invoice.uuid = UUID.randomUUID().toString();
        invoice.errors = false;
        invoice.invoicedate = LocalDate.now();
        invoice.invoicenumber = 0;
        invoice.type = InvoiceType.CREDIT_NOTE;
        invoice.status = InvoiceStatus.DRAFT;
        for (InvoiceItem invoiceitem : invoice.invoiceitems) {
            invoiceitem.uuid = UUID.randomUUID().toString();
        }
        invoiceClient.save(invoice);
    }

    @RequestMapping(value = "/search/findByLatestInvoiceByProjectuuid", method = GET)
    public Invoice findByLatestInvoiceByProjectuuid(@RequestParam("projectuuid") String projectuuid) {
        return invoiceClient.findByLatestInvoiceByProjectuuid(projectuuid);
    }
}
