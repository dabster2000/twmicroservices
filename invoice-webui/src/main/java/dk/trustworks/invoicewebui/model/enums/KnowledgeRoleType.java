package dk.trustworks.invoicewebui.model.enums;

public enum KnowledgeRoleType {

    SOLUTION_ARCHITECT("Solution Architect"), BUSINESS_ARCHITECT("Business Architect"), PROJECT_MANAGER("Project Manager"), TENDER_MANAGER("Tender Manager"), UX_SPECIALIST("UX Specialist");

    private final String name;

    KnowledgeRoleType(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AmbitionType{" +
                "name='" + name + '\'' +
                '}';
    }

}
