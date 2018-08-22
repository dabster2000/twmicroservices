package dk.trustworks.invoicewebui.model.enums;

public enum ExcelExpenseType {

    PRODUKTION("Produktionsomk. i alt"), LØNNINGER("Lønninger i alt"), LOKALE("Lokaleomkostninger i alt"), SALG("SALGSFREMMENDE OMK I ALT"), ADMINISTRATION("Øvrige administrationsomk. i alt");

    private String text;

    ExcelExpenseType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static ExcelExpenseType fromString(String text) {
        for (ExcelExpenseType b : ExcelExpenseType.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
