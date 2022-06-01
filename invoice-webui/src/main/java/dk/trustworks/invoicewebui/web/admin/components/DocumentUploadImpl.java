package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.File;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

@SpringUI
@SpringComponent
public class DocumentUploadImpl extends DocumentUpload {

    private byte[] bytes = null;

    private String filename = null;
    private List<User> users;

    @Autowired
    public DocumentUploadImpl(DocumentService documentService, DocumentListImpl documentList) {
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

        getCbUser().setItemCaptionGenerator(User::getUsername);

        getBtnRemoveUpload().addClickListener(event -> {
            bytes = null;
            filename = null;
            getBtnUpload().setEnabled(false);
            getVlUploadComplete().setVisible(false);
            getUploadComponent().setVisible(true);
        });

        getUploadComponent().setCaption("Document upload:");

        getBtnUpload().addClickListener(event -> {
            if(bytes == null || getTxtName().getValue().equals("") || getCbUser().isEmpty()) return;
            //Document document = new Document(getCbUser().getValue(), getTxtName().getValue(), filename, DocumentType.CONTRACT, LocalDate.now(), bytes);
            File document = new File(UUID.randomUUID().toString(), getCbUser().getValue().getUuid(), "DOCUMENT", getTxtName().getValue(), filename, LocalDate.now(), bytes);
            //documentRepository.save(document);
            documentService.saveDocument(document.getRelateduuid(), document);
            getTxtName().clear();
            getCbUser().clear();
            documentList.init(users);
            bytes = null;
            filename = null;
            getBtnUpload().setEnabled(false);
            getVlUploadComplete().setVisible(false);
            getUploadComponent().setVisible(true);
        });
    }

    public Component init(List<User> users) {
        this.users = users;
        getCbUser().setItems(users);
        return this;
    }

    private void uploadReceived(String filename, Path file) {
        Notification.show("Upload finished: " + filename, Notification.Type.HUMANIZED_MESSAGE);
        try {
            bytes = Files.readAllBytes(file);//fileUtils.compressPDF(Files.readAllBytes(file));
            this.filename = filename;
            getVlUploadComplete().setVisible(true);
            getUploadComponent().setVisible(false);
            getLblFilename().setValue(filename);
            if(getTxtName().getValue().trim().length()>0) getBtnUpload().setEnabled(true);
        } catch (IOException e) {
            Notification.show("Document is too large", "The document is too large. Try to compress the photos in the document.", Notification.Type.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            Notification.show("Something went wrong", "I have no clue what you should do - this shouldn't happen...!!!", Notification.Type.ERROR_MESSAGE);
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
