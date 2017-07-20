package dk.trustworks.web.model;

import java.time.LocalDate;

/**
 * Created by hans on 09/07/2017.
 */
public class YearMonthSelect {

    private LocalDate date;

    public YearMonthSelect(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YearMonthSelect that = (YearMonthSelect) o;

        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public String toString() {
        return date.getYear()+" - "+date.getMonthValue();
    }
}
