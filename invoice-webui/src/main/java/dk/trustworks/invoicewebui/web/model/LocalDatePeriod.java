package dk.trustworks.invoicewebui.web.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDatePeriod {

    private LocalDate from;
    private LocalDate to;

    public LocalDatePeriod(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return from.format(DateTimeFormatter.ofPattern("MMM yyyy"))+"-"+to.format(DateTimeFormatter.ofPattern("MMM yyyy"));
    }
}
