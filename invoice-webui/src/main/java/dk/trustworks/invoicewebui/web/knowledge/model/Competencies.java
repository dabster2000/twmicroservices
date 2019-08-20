package dk.trustworks.invoicewebui.web.knowledge.model;

public class Competencies {
    private String title;
    private String[] consultants;

    public Competencies() {
    }

    public Competencies(String title, String[] consultants) {
        this.title = title;
        this.consultants = consultants;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getConsultants() {
        return consultants;
    }

    public void setConsultants(String[] consultants) {
        this.consultants = consultants;
    }
}
