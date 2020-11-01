package dk.trustworks.invoicewebui.jobs;


import dk.trustworks.invoicewebui.model.ExpenseDetails;
import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.network.dto.enums.EconomicAccountGroup;
import dk.trustworks.invoicewebui.services.FinanceService;
import dk.trustworks.invoicewebui.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EconomicsJob {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private FinanceService financeService;

    @Scheduled(cron = "0 0 22 * * ?")
    public void synchronizeInvoices() {
        List<ExpenseDetails> expenseList = financeService.findExpenseDetailsByGroup(EconomicAccountGroup.OMSAETNING_ACCOUNTS);

        List<Invoice> invoiceList = invoiceService.findAll();

        expenseList.forEach(expenseDetails -> {
            invoiceList.stream().filter(invoice -> invoice.invoicenumber == expenseDetails.getInvoicenumber())
                    .findFirst()
                    .ifPresent(invoice -> {
                        invoice.setBookingdate(expenseDetails.getExpensedate());
                        invoiceService.update(invoice);
                    });
        });
    }
}
