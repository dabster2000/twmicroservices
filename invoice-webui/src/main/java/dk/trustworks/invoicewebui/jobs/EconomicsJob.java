package dk.trustworks.invoicewebui.jobs;


import dk.trustworks.invoicewebui.model.Expense;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.model.enums.ExcelExpenseType;
import dk.trustworks.invoicewebui.network.clients.EconomicsAPI;
import dk.trustworks.invoicewebui.network.clients.model.economics.Collection;
import dk.trustworks.invoicewebui.repositories.ExpenseRepository;
import dk.trustworks.invoicewebui.services.InvoiceService;
import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EconomicsJob {

    private static final Logger log = LoggerFactory.getLogger(EconomicsJob.class);

    @Autowired
    private EconomicsAPI economicsAPI;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ExpenseRepository expenseRepository;

    private String[] periods = {"2016_6_2017", "2017_6_2018", "2018_6_2019", "2019_6_2020"};

    @PostConstruct
    public void init() {
        synchronizeAllExpenseAccounts();
        //synchronizeInvoiceBookingDates();
    }

    @Scheduled(cron = "0 0 22 * * ?")
    public void synchronizeAllExpenseAccounts() {
        LocalDate runThrough = LocalDate.of(2016, 1, 1);
        do {
            expenseRepository.deleteByPeriod(runThrough);
            runThrough = runThrough.plusMonths(1);
        } while (runThrough.isBefore(LocalDate.now()));

        for (String period : periods) {
            Map<Range<Integer>, List<Collection>> allEntries = economicsAPI.getAllEntries(period);

            List<Invoice> invoiceList = invoiceService.findAll();

            allEntries.get(EconomicsAPI.OMSAETNING_ACCOUNTS).forEach(collection -> {
                invoiceList.stream().filter(invoice -> invoice.invoicenumber == collection.getVoucherNumber())
                        .findFirst()
                        .ifPresent(invoice -> {
                            invoice.setBookingdate(LocalDate.parse(collection.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            invoiceService.save(invoice);
                        });

            });

            expenseRepository.save(getExpenseMap(ExcelExpenseType.LØNNINGER, allEntries.get(EconomicsAPI.LOENNINGER_ACCOUNTS)).values());
            expenseRepository.save(getExpenseMap(ExcelExpenseType.ADMINISTRATION, allEntries.get(EconomicsAPI.ADMINISTRATION_ACCOUNTS)).values());
            expenseRepository.save(getExpenseMap(ExcelExpenseType.LOKALE, allEntries.get(EconomicsAPI.LOKALE_ACCOUNTS)).values());
            expenseRepository.save(getExpenseMap(ExcelExpenseType.PRODUKTION, allEntries.get(EconomicsAPI.PRODUKTION_ACCOUNTS)).values());
            expenseRepository.save(getExpenseMap(ExcelExpenseType.SALG, allEntries.get(EconomicsAPI.SALG_ACCOUNTS)).values());
            expenseRepository.save(getExpenseMap(ExcelExpenseType.PERSONALE, allEntries.get(EconomicsAPI.PERSONALE_ACCOUNTS)).values());
        }
    }

    //@Scheduled(cron = "0 0 23 * * ?")
    public void synchronizeInvoiceBookingDates() {
        List<Invoice> invoiceList = invoiceService.findAll();

        for (String period : periods) {
            fetchAccountYear(invoiceList, period);
        }
    }

    private void fetchAccountYear(List<Invoice> invoiceList, String accountPeriod) {
        economicsAPI.getInvoices(EconomicsAPI.OMSAETNING, accountPeriod)
                .forEach(collection -> invoiceList.stream().filter(invoice -> invoice.invoicenumber == collection.getInvoiceNumber())
                        .findFirst()
                        .ifPresent(invoice -> {
                            invoice.setBookingdate(LocalDate.parse(collection.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            invoiceService.save(invoice);
                        }));
    }

    //@Scheduled(cron = "0 0 22 * * ?")
    public void synchronizeExpenses() {

        LocalDate runThrough = LocalDate.of(2016, 1, 1);
        do {
            expenseRepository.deleteByPeriod(runThrough);
            runThrough = runThrough.plusMonths(1);
        } while (runThrough.isBefore(LocalDate.now()));

        for (String period : periods) {
            expenseRepository.save(getExpenseMap(ExcelExpenseType.LØNNINGER, EconomicsAPI.LOENNINGER, period).values());
            expenseRepository.save(getExpenseMap(ExcelExpenseType.ADMINISTRATION, EconomicsAPI.ADMINISTRATION, period).values());
            expenseRepository.save(getExpenseMap(ExcelExpenseType.LOKALE, EconomicsAPI.LOKALE, period).values());
            expenseRepository.save(getExpenseMap(ExcelExpenseType.PRODUKTION, EconomicsAPI.PRODUKTION, period).values());
            expenseRepository.save(getExpenseMap(ExcelExpenseType.SALG, EconomicsAPI.SALGSFREMMENDE, period).values());
            expenseRepository.save(getExpenseMap(ExcelExpenseType.PERSONALE, EconomicsAPI.PERSONALE, period).values());
        }
    }

    private Map<LocalDate, Expense> getExpenseMap(ExcelExpenseType excelType, int[] type, String year) {
        Map<LocalDate, Expense> map = new HashMap<>();
        economicsAPI.getInvoices(type, year).forEach(collection -> {
            LocalDate period = LocalDate.parse(collection.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).withDayOfMonth(1);
            if(!map.containsKey(period)) map.put(period, new Expense(period, excelType, 0.0));
            map.get(period).setAmount(map.get(period).getAmount() + collection.getAmount());
        });
        return map;
    }

    private Map<LocalDate, Expense> getExpenseMap(ExcelExpenseType excelType, List<Collection> collectionList) {
        Map<LocalDate, Expense> map = new HashMap<>();
        collectionList.forEach(collection -> {
            LocalDate period = LocalDate.parse(collection.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).withDayOfMonth(1);
            if(!map.containsKey(period)) map.put(period, new Expense(period, excelType, 0.0));
            map.get(period).setAmount(map.get(period).getAmount() + collection.getAmount());
        });
        return map;
    }
}
