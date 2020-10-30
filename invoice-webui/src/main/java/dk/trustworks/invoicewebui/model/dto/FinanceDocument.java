package dk.trustworks.invoicewebui.model.dto;

import java.time.LocalDate;

public class FinanceDocument {

    private LocalDate month;
    private double eSalaries;
    private double eEmployee_expenses;
    private double eHousing;
    private double eSales;
    private double eAdministration;
    private double eProduktion;

    public FinanceDocument() {
    }

    public FinanceDocument(LocalDate month, double eSalaries, double eEmployee_expenses, double eHousing, double eSales, double eAdministration, double eProduktion) {
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

    public double sum() {
        return eSalaries+eEmployee_expenses+eHousing+eSales+eAdministration+eProduktion;
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
