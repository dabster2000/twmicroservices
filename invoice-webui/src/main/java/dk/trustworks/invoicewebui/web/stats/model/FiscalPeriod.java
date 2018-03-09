package dk.trustworks.invoicewebui.web.stats.model;

import org.joda.time.LocalDate;

public class FiscalPeriod {

    private String name;
    private LocalDate fromDate;
    private LocalDate toDate;

    public FiscalPeriod(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        name = fromDate.toString("MM-yyyy") + " to " + toDate.toString("MM-yyyy");
    }

    public String getName() {
        return name;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }
}
