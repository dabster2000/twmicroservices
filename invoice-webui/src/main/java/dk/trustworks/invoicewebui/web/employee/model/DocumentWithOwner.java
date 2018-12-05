package dk.trustworks.invoicewebui.web.employee.model;

import dk.trustworks.invoicewebui.model.Document;

import java.time.LocalDate;

public class DocumentWithOwner extends Document {

    private Document document;
    private String username;

    public DocumentWithOwner(Document document, String username) {
        this.username = username;
        this.document = document;
    }

    public int getId() {
        return document.getId();
    }

    public String getName() {
        return document.getName();
    }

    public LocalDate getUploaddate() {
        return document.getUploaddate();
    }

    public String getUsername() {
        return username;
    }
}
