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

/**
 * Created by hans on 20/07/2017.
 */

@Service
public class InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    private InvoiceAPI invoiceAPI;

    @Autowired
    private InvoiceRepository invoiceRepository;

    public Invoice createCreditNota(@RequestBody Invoice invoice) {
        log.info("InvoiceController.createCreditNota");
        log.info("invoice = [" + invoice + "]");
        invoice.status = InvoiceStatus.CREDIT_NOTE;
        invoiceRepository.save(invoice);

        Invoice creditNote = new Invoice(InvoiceType.CREDIT_NOTE, invoice.getContractuuid(), invoice.getProjectuuid(),
                invoice.getProjectname(), invoice.getYear(), invoice.getMonth(), invoice.getClientname(),
                invoice.getClientaddresse(), invoice.getOtheraddressinfo(), invoice.getZipcity(),
                invoice.getEan(), invoice.getCvr(), invoice.getAttention(), LocalDate.now(),
                invoice.getProjectref(), invoice.getContractref(),
                "Kreditnota til faktura " + StringUtils.convertInvoiceNumberToString(invoice.invoicenumber));

        creditNote.invoicenumber = 0;
        for (InvoiceItem invoiceitem : invoice.invoiceitems) {
            creditNote.getInvoiceitems().add(new InvoiceItem(invoiceitem.getItemname(), invoiceitem.getDescription(), invoiceitem.getRate(), invoiceitem.getHours()));
        }
        return invoiceRepository.save(creditNote);
    }

    public double invoicedAmountByMonth(LocalDate date) {
        double invoiceSum = invoiceRepository.invoicedAmountByPeriod(date.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), date.withDayOfMonth(date.getMonth().length(date.isLeapYear())).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        double creditNoteSum = invoiceRepository.creditNoteAmountByPeriod(date.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), date.withDayOfMonth(date.getMonth().length(date.isLeapYear())).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
        invoiceRepository.save(invoice);
    }

    @Transactional
    public void delete(String uuid) {
        invoiceRepository.delete(uuid);
    }

    @Transactional
    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public int getMaxInvoiceNumber() {
        return invoiceRepository.getMaxInvoiceNumber();
    }

    public Invoice findByLatestInvoiceByProjectuuid(String projectuuid) {
        return invoiceRepository.findByLatestInvoiceByProjectuuid(projectuuid);
    }
}
