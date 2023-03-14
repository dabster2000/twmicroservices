package dk.trustworks.invoicewebui.model.enums;

public enum BubbleType {
    FOCUS("Focus Areas"), KNOWLEDGE("Knowledge Bubbles"), ACCOUNT_TEAM("Account Teams"), SOCIAL ("Social");

    private final String name;

    BubbleType(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }

}
