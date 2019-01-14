package dk.trustworks.invoicewebui.services;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Created by hans on 20/07/2017.
 */

@Service
public class InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

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

    public double invoicedAmountByMonth(LocalDate date) {
        System.out.println("InvoiceService.invoicedAmountByMonth");
        System.out.println("date = [" + date + "]");
        double invoiceSum = invoiceClient.invoicedAmountByPeriod(date.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), date.withDayOfMonth(date.getMonth().length(date.isLeapYear())).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        System.out.println("invoiceSum = " + invoiceSum);
        double creditNoteSum = invoiceClient.creditNoteAmountByPeriod(date.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), date.withDayOfMonth(date.getMonth().length(date.isLeapYear())).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        System.out.println("creditNoteSum = " + creditNoteSum);
        System.out.println();
        //double invoiceSum = invoiceClient.findByYearAndMonth(month.getYear(), month.getMonthValue()-1).parallelStream().filter(invoice -> invoice.type.equals(InvoiceType.INVOICE)).mapToDouble(value -> value.getInvoiceitems().stream().mapToDouble(value1 -> value1.hours * value1.rate).sum()).sum();
        //double creditNoteSum = invoiceClient.findByYearAndMonth(month.getYear(), month.getMonthValue()-1).parallelStream().filter(invoice -> invoice.type.equals(InvoiceType.CREDIT_NOTE)).mapToDouble(value -> value.getInvoiceitems().stream().mapToDouble(value1 -> value1.hours * value1.rate).sum()).sum();
        return invoiceSum - creditNoteSum;
    }

    public byte[] createInvoicePdf(Invoice invoice) {
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
