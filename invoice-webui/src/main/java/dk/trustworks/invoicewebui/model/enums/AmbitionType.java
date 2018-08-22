package dk.trustworks.invoicewebui.model.enums;

public enum AmbitionType {

    SLACK ("Slack"),
    STATUS_QUO ("Status Quo"),
    IMPROVE ("Improve");

    private final String name;

    AmbitionType(String s) {
        name = s;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
