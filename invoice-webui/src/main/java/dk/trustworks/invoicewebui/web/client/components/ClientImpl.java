package dk.trustworks.invoicewebui.web.client.components;

import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import server.droporchoose.UploadComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static com.vaadin.ui.Notification.Type.*;

/**
 * Created by hans on 13/08/2017.
 */
public class ClientImpl extends ClientDesign {

    private PhotoRepository photoRepository;
    private Client client;

    public ClientImpl(PhotoRepository photoRepository, Client client) {
        this.photoRepository = photoRepository;
        this.client = client;
        UploadComponent uploadComponent = new UploadComponent(this::uploadReceived);
        uploadComponent.setStartedCallback(this::uploadStarted);
        uploadComponent.setProgressCallback(this::uploadProgress);
        uploadComponent.setFailedCallback(this::uploadFailed);
        uploadComponent.setWidth(100, Unit.PERCENTAGE);
        uploadComponent.setHeight(200, Unit.PIXELS);
        uploadComponent.setCaption("File upload");

        getFormLayout().addComponent(uploadComponent);
    }

    private void uploadReceived(String fileName, Path file) {
        Notification.show("Upload finished: " + fileName, HUMANIZED_MESSAGE);
        try {
            Photo photo = photoRepository.findByRelateduuid(client.getUuid());
            byte[] bytes = Files.readAllBytes(file);
            if(photo==null) {
                photo = new Photo(UUID.randomUUID().toString(), client.getUuid(), bytes);
            } else {
                photo.setPhoto(bytes);
            }
            photoRepository.save(photo);
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
}
