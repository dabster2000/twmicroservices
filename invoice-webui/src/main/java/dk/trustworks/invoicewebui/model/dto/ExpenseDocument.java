package dk.trustworks.invoicewebui.model.dto;

import java.time.LocalDate;

public class ExpenseDocument {

    private final LocalDate month;
    private final double eSalaries;
    private final double eEmployee_expenses;
    private final double eHousing;
    private final double eSales;
    private final double eAdministration;
    private final double eProduktion;

    public ExpenseDocument(LocalDate month, double eSalaries, double eEmployee_expenses, double eHousing, double eSales, double eAdministration, double eProduktion) {
        this.month = month;
        this.eSalaries = eSalaries;
        this.eEmployee_expenses = eEmployee_expenses;
        this.eHousing = eHousing;
        this.eSales = eSales;
        this.eAdministration = eAdministration;
        this.eProduktion = eProduktion;
    }

    public double geteSalaries() {
        return eSalaries;
    }

    public double geteEmployee_expenses() {
        return eEmployee_expenses;
    }

    public double geteHousing() {
        return eHousing;
    }

    public double geteSales() {
        return eSales;
    }

    public double geteAdministration() {
        return eAdministration;
    }

    public LocalDate getMonth() {
        return month;
    }

    public double geteProduktion() {
        return eProduktion;
    }

    @Override
    public String toString() {
        return "ExpenseDocument{" +
                "month=" + month +
                ", eSalaries=" + eSalaries +
                ", eEmployee_expenses=" + eEmployee_expenses +
                ", eHousing=" + eHousing +
                ", eSales=" + eSales +
                ", eAdministration=" + eAdministration +
                ", eProduktion=" + eProduktion +
                '}';
    }
}
