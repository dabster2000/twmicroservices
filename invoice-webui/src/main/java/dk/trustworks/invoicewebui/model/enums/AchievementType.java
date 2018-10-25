package dk.trustworks.invoicewebui.model.enums;

public enum AchievementType {

    SPEEDDATE10("Speeddates med 10"),
    SPEEDDATE20("Speeddates med 20"),
    SPEEDDATE30("Speeddates med 30");

    private final String name;

    AchievementType(String s) {
        name = s;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
