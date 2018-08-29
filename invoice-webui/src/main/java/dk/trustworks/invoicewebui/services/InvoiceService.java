package dk.trustworks.invoicewebui.services;

import dk.trustworks.invoicewebui.generators.InvoicePdfGenerator;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.model.InvoiceItem;
import dk.trustworks.invoicewebui.model.enums.InvoiceStatus;
import dk.trustworks.invoicewebui.model.enums.InvoiceType;
import dk.trustworks.invoicewebui.network.clients.InvoiceAPI;
import dk.trustworks.invoicewebui.repositories.InvoiceRepository;
import dk.trustworks.invoicewebui.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by hans on 20/07/2017.
 */

@Service
public class InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    private InvoicePdfGenerator invoicePdfGenerator;

    @Autowired
    private InvoiceAPI invoiceAPI;

    @Autowired
    private InvoiceRepository invoiceClient;

    public void createCreditNota(@RequestBody Invoice invoice) {
        log.info("InvoiceController.createCreditNota");
        log.info("invoice = [" + invoice + "]");
        invoice.status = InvoiceStatus.CREDIT_NOTE;
        invoiceClient.save(invoice);

        invoice.uuid = UUID.randomUUID().toString();
        invoice.errors = false;
        invoice.invoicedate = LocalDate.now();
        invoice.type = InvoiceType.CREDIT_NOTE;
        invoice.status = InvoiceStatus.DRAFT;
        invoice.specificdescription = "Kreditnota til faktura " + StringUtils.convertInvoiceNumberToString(invoice.invoicenumber);
        invoice.invoicenumber = 0;
        for (InvoiceItem invoiceitem : invoice.invoiceitems) {
            invoiceitem.uuid = UUID.randomUUID().toString();
        }
        invoiceClient.save(invoice);
    }

    @RequestMapping(value = "/search/findByLatestInvoiceByProjectuuid", method = GET)
    public Invoice findByLatestInvoiceByProjectuuid(@RequestParam("projectuuid") String projectuuid) {
        return invoiceClient.findByLatestInvoiceByProjectuuid(projectuuid);
    }

    public byte[] createInvoicePdf(Invoice invoice) throws IOException {
        //return invoicePdfGenerator.createInvoice(invoice);
        return invoiceAPI.createInvoicePDF(invoice);
    }

    @Transactional
    public void createBlankInvoice(int year, int month) {
        Invoice invoice = new Invoice(
                InvoiceType.INVOICE,
                "",
                "",
                "Blank invoice",
                year,
                month,
                "Blank invoice", "", "", "", "", "", "",
                LocalDate.now().withYear(year).withMonth(month+1).withDayOfMonth(LocalDate.now().withYear(year).withMonth(month+1).lengthOfMonth()),
                "",
                "",
                ""
        );
        invoiceClient.save(invoice);
    }
}
