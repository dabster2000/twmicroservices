package dk.trustworks.invoicewebui.model.enums;

public enum EducationLevel {

    HIGHSCHOOL(1, "Grundskole"),
    TENSCLASS(2, "10. klasse"),
    GYMNASIUM(4, "Gymnasiale uddannelser"),
    BUSINESSEDUCATION3(3, "Erhvervsuddannelser lvl 3"),
    BUSINESSEDUCATION4(4, "Erhvervsuddannelser lvl 4"),
    BUSINESSEDUCATION5(5, "Erhvervsuddannelser lvl 5"),
    BUSINESSACADAMY(5, "Erhvervsakademiuddannelser og videregående voksenuddannelser"),
    BACHELOR(6, "Bachelor- og diplomuddannelser"),
    MASTER(7, "Master- og kandidatuddannelser"),
    PHD(8, "Ph.d.-uddannelser");

    private final int level;
    private final String name;

    EducationLevel(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }
}
