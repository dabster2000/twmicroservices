package dk.trustworks.controllers;

import dk.trustworks.network.clients.feign.InvoiceClient;
import dk.trustworks.network.dto.Invoice;
import dk.trustworks.network.dto.InvoiceItem;
import dk.trustworks.network.dto.InvoiceStatus;
import dk.trustworks.network.dto.InvoiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by hans on 20/07/2017.
 */

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    protected static Logger logger = LoggerFactory.getLogger(ProjectSummaryController.class.getName());

    @Autowired
    private InvoiceClient invoiceClient;

    @RequestMapping(value = "/methodOn/createCreditNota", method = POST)
    public void createCreditNota(@RequestBody Invoice invoice) {
        System.out.println("InvoiceController.createCreditNota");
        System.out.println("invoice = [" + invoice + "]");
        invoice.status = InvoiceStatus.CREDIT_NOTE;
        invoiceClient.updateInvoice(invoice.uuid, invoice);

        invoice.uuid = UUID.randomUUID().toString();
        invoice.errors = false;
        invoice.invoicedate = LocalDate.now();
        invoice.invoicenumber = 0;
        invoice.type = InvoiceType.CREDIT_NOTE;
        invoice.status = InvoiceStatus.DRAFT;
        for (InvoiceItem invoiceitem : invoice.invoiceitems) {
            invoiceitem.uuid = UUID.randomUUID().toString();
        }
        invoiceClient.saveInvoice(invoice);
    }
}
