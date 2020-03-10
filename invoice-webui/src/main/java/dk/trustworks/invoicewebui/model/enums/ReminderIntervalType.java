package dk.trustworks.invoicewebui.model.enums;

public enum ReminderIntervalType {

    MONTH(1), QUARTER(3), YEAR(12);

    private final int months;

    ReminderIntervalType(int months) {
        this.months = months;
    }

    public int getMonths() {
        return this.months;
    }


}
