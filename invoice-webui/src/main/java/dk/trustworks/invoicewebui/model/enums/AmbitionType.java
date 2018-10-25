package dk.trustworks.invoicewebui.model.enums;

public enum AmbitionType {

    SLACK ("Slack", "I do not plan to keep my current skill level"),
    STATUS_QUO ("Status Quo", "I maintain this skill level"),
    IMPROVE ("Improve", "I want to improve this skill level");

    private final String name;
    private final String description;

    AmbitionType(String s, String s1) {
        name = s;
        description = s1;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "AmbitionType{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
