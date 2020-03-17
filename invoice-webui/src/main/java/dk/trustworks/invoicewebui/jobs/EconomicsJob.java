package dk.trustworks.invoicewebui.jobs;


import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.network.clients.EconomicsAPI;
import dk.trustworks.invoicewebui.network.clients.model.economics.Collection;
import dk.trustworks.invoicewebui.services.InvoiceService;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class EconomicsJob {

    private static final Logger log = LoggerFactory.getLogger(EconomicsJob.class);

    @Autowired
    private EconomicsAPI economicsAPI;

    @Autowired
    private InvoiceService invoiceService;


    @PostConstruct
    public void init() {
        synchronizeInvoiceBookingDates();
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void synchronizeInvoiceBookingDates() {
        List<Invoice> invoiceList = invoiceService.findAll();

        fetchAccountYear(invoiceList, "2016_6_2017");
        fetchAccountYear(invoiceList, "2017_6_2018");
        fetchAccountYear(invoiceList, "2018_6_2019");
        fetchAccountYear(invoiceList, "2019_6_2020");
    }

    private void fetchAccountYear(List<Invoice> invoiceList, String accountPeriod) {
        economicsAPI.getInvoices(accountPeriod).getCollection().parallelStream()
                .forEach(collection -> invoiceList.stream().filter(invoice -> invoice.invoicenumber == collection.getInvoiceNumber())
                        .findFirst()
                        .ifPresent(invoice -> {
                            invoice.setBookingdate(LocalDate.parse(collection.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            invoiceService.save(invoice);
                        }));
    }
}
