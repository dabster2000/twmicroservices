package dk.trustworks.controllers;

import dk.trustworks.model.Invoice;
import dk.trustworks.pdf.InvoicePdfGenerator;
import dk.trustworks.repositories.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by hans on 10/08/2017.
 */

@RestController
@RequestMapping("/admin")
public class AdminController {

    protected static Logger logger = LoggerFactory.getLogger(AdminController.class.getName());

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    InvoicePdfGenerator invoicePdfGenerator;

    @RequestMapping(value = "/createInvoicePdf", method = GET)
    public void createInvoicePdf(@RequestParam("uuid") String uuid) throws IOException {
        Invoice invoice = invoiceRepository.findOne(uuid);
        invoice.pdf = invoicePdfGenerator.createInvoice(invoice);
        invoiceRepository.save(invoice);
    }
}
