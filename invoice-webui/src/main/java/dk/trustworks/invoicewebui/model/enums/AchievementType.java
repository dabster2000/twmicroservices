package dk.trustworks.invoicewebui.model.enums;

/**
 * Speeddates med 10, 20, 30
 * 3, 4, 5 ugers sammenhængene ferie
 * 1 Orlov
 * 1 uges sygdom
 * 40, 50, 60 timers faktureret uge
 * Registrér 0 timer på en uge
 * 4 key purpose samtaler til tiden
 * 1 feriedag i alle årets måneder
 * Brugt uddannelsesbudgettet 1, 2, 3 år (med maks 500kr variation)
 * Har besøgt intra hver dag i 14 dage
 * Har udfyldt hele sit kompetenceskema
 * Har opdateret kompetenceskema 1, 2, 3 år i træk
 * Har lagt mere end 40, 50, 60 timer på backoffice opgaver på et år
 * Har arbejdet på mere end 5, 10, 15 kunder på et år
 * Har arbejdet på mere end 5, 10, 15 projekter på ét år
 */

public enum AchievementType {

    SPEEDDATES10("+10 Speeddates", 0, 1, "SPEEDDATES"),
    SPEEDDATES20("+20 Speeddates", 0, 2, "SPEEDDATES"),
    SPEEDDATES30("+30 Speeddates", 0, 3, "SPEEDDATES"),
    VACATION3("+3 weeks vacation", 1, 1, "VACATION"),
    VACATION4("+4 weeks vacation", 1, 2, "VACATION"),
    VACATION5("+5 weeks vacation", 1, 3, "VACATION"),
    WORKWEEK40("+40 hours billable work", 2, 1, "WORKWEEK"),
    WORKWEEK50("+50 hours billable work", 2, 2, "WORKWEEK"),
    WORKWEEK60("+60 hours billable work", 2, 3, "WORKWEEK"),
    MONTHVACATION("Vacation every month", 3, 1, "VACATIONMONTH"),
    WEEKVACATION("Vacation every week", 3, 2, "VACATIONMONTH"),
    CKOEXPENSE1("Used CKO budget 1 year", 4, 1, "CKOEXPENSE"),
    CKOEXPENSE2("Used CKO budget 2 year", 4, 2, "CKOEXPENSE"),
    CKOEXPENSE3("Used CKO budget 3 year", 4, 3, "CKOEXPENSE");

    private final String name;
    private final int number;
    private final int rank;
    private final String parent;

    AchievementType(String s, int number, int rank, String parent) {
        name = s;
        this.number = number;
        this.rank = rank;
        this.parent = parent;
    }

    public String getFilename() {
        return this.name().toLowerCase();
    }

    public int getNumber() {
        return number;
    }

    public String getParent() {
        return parent;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
