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

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    @Autowired
    private EntityManager entityManager;

    public Invoice createCreditNota(@RequestBody Invoice invoice) {
        log.info("InvoiceController.createCreditNota");
        log.info("invoice = [" + invoice + "]");
        invoice.status = InvoiceStatus.CREDIT_NOTE;
        invoiceRepository.save(invoice);

        Invoice creditNote = new Invoice(InvoiceType.CREDIT_NOTE, invoice.getContractuuid(), invoice.getProjectuuid(),
                invoice.getProjectname(), invoice.getDiscount(), invoice.getYear(), invoice.getMonth(), invoice.getClientname(),
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
                0.0,
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

    public List<Invoice> findByMonth(LocalDate month) {
        return invoiceRepository.findByYearAndMonth(month.getYear(), month.getMonthValue()-1);
    }

    @Transactional
    public void delete(String uuid) {
        invoiceRepository.delete(uuid);
    }

    @Transactional
    public Invoice save(Invoice invoice) {
        invoice = invoiceRepository.save(invoice);
        //entityManager.refresh(invoice);
        return invoice;
    }

    public int getMaxInvoiceNumber() {
        return invoiceRepository.getMaxInvoiceNumber();
    }

    public Invoice findByLatestInvoiceByProjectuuid(String projectuuid) {
        return invoiceRepository.findByLatestInvoiceByProjectuuid(projectuuid);
    }

    public List<Invoice> findByStatuses(InvoiceStatus... invoiceStatuses) {
        return invoiceRepository.findByStatusIn(invoiceStatuses);
    }

    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Transactional
    public void refresh(Invoice invoice) {
        entityManager.refresh(invoice);
    }

    public List<Invoice> findByInvoicedateAndBookingdate(LocalDate invoicedate) {
        List<Invoice> invoiceList = invoiceRepository.findByInvoicedate(invoicedate.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                invoicedate.plusMonths(1).withDayOfMonth(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        invoiceList.addAll(invoiceRepository.findByBookingdate(invoicedate.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                invoicedate.plusMonths(1).withDayOfMonth(1).minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        return invoiceList;
    }
}
