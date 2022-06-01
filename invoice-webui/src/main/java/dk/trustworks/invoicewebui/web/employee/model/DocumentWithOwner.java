package dk.trustworks.invoicewebui.web.employee.model;

import dk.trustworks.invoicewebui.model.File;

import java.time.LocalDate;

public class DocumentWithOwner extends File {

    private File document;
    private String username;

    public DocumentWithOwner(File document, String username) {
        this.username = username;
        this.document = document;
    }

    public String getUuid() {
        return document.getUuid();
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
