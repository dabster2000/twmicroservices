package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.Document;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.DocumentType;
import dk.trustworks.invoicewebui.repositories.DocumentRepository;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

@SpringUI
@SpringComponent
public class DocumentUploadImpl extends DocumentUpload {

    private final User user;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentListImpl documentList;

    private byte[] bytes = null;

    private String filename = null;

    public DocumentUploadImpl() {
        user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();

        getBtnUpload().setEnabled(false);
        getVlUploadComplete().setVisible(false);


        getUploadComponent().setWidth(100, PERCENTAGE);
        getUploadComponent().setReceivedCallback(this::uploadReceived);
        getUploadComponent().setStartedCallback(this::uploadStarted);
        getUploadComponent().setProgressCallback(this::uploadProgress);
        getUploadComponent().setFailedCallback(this::uploadFailed);
        getUploadComponent().setWidth(100, PERCENTAGE);
        getUploadComponent().setHeight(150, PIXELS);

        getTxtName().addValueChangeListener(event -> {
            if(bytes!=null && event.getValue().trim().length()>0) getBtnUpload().setEnabled(true);
            else getBtnUpload().setEnabled(false);
        });

        getBtnRemoveUpload().addClickListener(event -> {
            bytes = null;
            filename = null;
            getBtnUpload().setEnabled(false);
            getVlUploadComplete().setVisible(false);
            getUploadComponent().setVisible(true);
        });

        getUploadComponent().setCaption("Document upload:");

        getBtnUpload().addClickListener(event -> {
            if(bytes == null && getTxtName().getValue().equals("")) return;
            Document document = new Document(user, getTxtName().getValue(), filename, DocumentType.CONTRACT, LocalDate.now(), bytes);
            documentRepository.save(document);
            getTxtName().clear();
            documentList.reload();
            bytes = null;
            filename = null;
            getBtnUpload().setEnabled(false);
            getVlUploadComplete().setVisible(false);
            getUploadComponent().setVisible(true);
        });
    }

    private void uploadReceived(String filename, Path file) {
        Notification.show("Upload finished: " + filename, Notification.Type.HUMANIZED_MESSAGE);
        try {
            bytes = Files.readAllBytes(file);
            this.filename = filename;
            getVlUploadComplete().setVisible(true);
            getUploadComponent().setVisible(false);
            getLblFilename().setValue(filename);
            if(getTxtName().getValue().trim().length()>0) getBtnUpload().setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadStarted(String fileName) {
        Notification.show("Upload started: " + fileName, Notification.Type.HUMANIZED_MESSAGE);
    }

    private void uploadProgress(String fileName, long readBytes, long contentLength) {
        Notification.show(String.format("Progress: %s : %d/%d", fileName, readBytes, contentLength),
                Notification.Type.TRAY_NOTIFICATION);
    }

    private void uploadFailed(String fileName, Path file) {
        Notification.show("Upload failed: " + fileName, Notification.Type.ERROR_MESSAGE);
    }
}
