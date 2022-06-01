package dk.trustworks.invoicewebui.web.economy.components;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.File;
import dk.trustworks.invoicewebui.services.PhotoService;
import org.vaadin.viritin.fields.MTextField;
import server.droporchoose.UploadComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

import static com.vaadin.ui.Notification.Type.*;

public class PhotoUploader {

    private final PhotoService photoService;
    private MTextField relatedUuid;

    public PhotoUploader(PhotoService photoService) {
        this.photoService = photoService;
    }

    public Card init() {
        Card card = new Card();

        UploadComponent uploadComponent = new UploadComponent(this::uploadReceived);
        uploadComponent.setStartedCallback(this::uploadStarted);
        uploadComponent.setProgressCallback(this::uploadProgress);
        uploadComponent.setFailedCallback(this::uploadFailed);
        uploadComponent.setWidth(100, Sizeable.Unit.PERCENTAGE);
        uploadComponent.setHeight(200, Sizeable.Unit.PIXELS);
        uploadComponent.setCaption("File upload");

        card.getLblTitle().setValue("Upload photo");
        relatedUuid = new MTextField("Related UUID");
        card.getContent().addComponent(relatedUuid);
        card.getContent().addComponent(uploadComponent);

        return card;
    }

    private void uploadReceived(String fileName, Path file) {
        Notification.show("Upload finished: " + fileName, HUMANIZED_MESSAGE);

        File photo = photoService.getRelatedPhoto(relatedUuid.getValue());

        try {
            final byte[] bytes = Files.readAllBytes(file);

            if(photo==null) {
                photo = new File(UUID.randomUUID().toString(), relatedUuid.getValue(), "PHOTO", "", "", LocalDate.now(), bytes);
            } else {
                photo.setFile(bytes);
            }
            //StreamResource resource = new StreamResource(() -> new ByteArrayInputStream(bytes), "edited-image-" + System.currentTimeMillis() + ".jpg");
            photoService.save(photo);
        } catch (IOException e) {
            e.printStackTrace();
            Notification.show("Upload failed", ERROR_MESSAGE);
        }
    }

    private void uploadStarted(String fileName) {
        Notification.show("Upload started: " + fileName, TRAY_NOTIFICATION);
    }

    private void uploadProgress(String fileName, long readBytes, long contentLength) {
        Notification.show(String.format("Progress: %s : %d/%d", fileName, readBytes, contentLength), TRAY_NOTIFICATION);
    }

    private void uploadFailed(String fileName, Path file) {
        Notification.show("Upload failed: " + fileName, ERROR_MESSAGE);
    }
/*
    private void uploadReceived(String fileName, Path file) {
        Notification.show("New photo uploaded: " + fileName, HUMANIZED_MESSAGE);
        try {
            photoService.save(new File(UUID.randomUUID().toString(), relatedUuid.getValue(), "PHOTO", "", "", LocalDate.now(), Files.readAllBytes(file)));
        } catch (IOException e) {
            uploadFailed(fileName, file);
        }
    }

    private void uploadStarted(String fileName) {
        Notification.show("Upload started: " + fileName, HUMANIZED_MESSAGE);
    }

    private void uploadProgress(String fileName, long readBytes, long contentLength) {
        Notification.show(String.format("Progress: %s : %d/%d", fileName, readBytes, contentLength), TRAY_NOTIFICATION);
    }

    private void uploadFailed(String fileName, Path file) {
        Notification.show("Upload failed: " + fileName, ERROR_MESSAGE);
    }

 */
}
