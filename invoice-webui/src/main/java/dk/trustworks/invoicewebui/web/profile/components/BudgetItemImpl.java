package dk.trustworks.invoicewebui.web.profile.components;

import com.vaadin.server.ThemeResource;
import dk.trustworks.invoicewebui.model.ItBudgetItem;
import dk.trustworks.invoicewebui.model.enums.ItBudgetStatus;
import dk.trustworks.invoicewebui.utils.NumberConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BudgetItemImpl extends BudgetItem {

    public BudgetItemImpl(ItBudgetItem itBudgetItem) {
        getImgIcon().setSource(new ThemeResource("images/icons/"+itBudgetItem.getCategory().getName()+"-icon.png"));
        getLblDate().setValue(itBudgetItem.getInvoicedate().format(DateTimeFormatter.ofPattern("dd. MMMM yyyy")));
        long amortizationPeriod = ChronoUnit.MONTHS.between(LocalDate.now().minusMonths(itBudgetItem.getCategory().getLifespan()), itBudgetItem.getInvoicedate());
        getLblAmortizationPeriod().setValue(amortizationPeriod + " months");
        if(!itBudgetItem.getStatus().equals(ItBudgetStatus.ACTIVE) || amortizationPeriod <= 0) {
            getLblAmortizationPeriod().setValue(itBudgetItem.getStatus().name());
            getImgAmortized().setSource(new ThemeResource("images/icons/amortized.png"));
            getImgAmortized().setVisible(true);
        }
        getLblAmount().setValue(NumberConverter.formatCurrency(itBudgetItem.getPrice()) + "");
        getLblDescription().setValue(itBudgetItem.getDescription());
    }
}
