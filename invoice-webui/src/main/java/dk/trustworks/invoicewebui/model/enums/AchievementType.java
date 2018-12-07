package dk.trustworks.invoicewebui.model.enums;

public enum AchievementType {

    SPEEDDATES10("Speeddates med 10"),
    SPEEDDATES20("Speeddates med 20"),
    SPEEDDATES30("Speeddates med 30"),
    WORKWEEK40("40 timer faktureret arbejde"),
    WORKWEEK50("50 timer faktureret arbejde"),
    WORKWEEK60("60 timer faktureret arbejde");

    private final String name;

    AchievementType(String s) {
        name = s;
    }

    public String getFilename() {
        return this.name().toLowerCase();
    }

    @Override
    public String toString() {
        return this.name;
    }

}
