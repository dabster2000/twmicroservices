package dk.trustworks.invoicewebui.web.client.components;

import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.Client;
import dk.trustworks.invoicewebui.model.File;
import dk.trustworks.invoicewebui.services.PhotoService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

import static com.vaadin.ui.Notification.Type.*;

/**
 * Created by hans on 13/08/2017.
 */
public class ClientImpl extends ClientDesign {

    private PhotoService photoService;
    private Client client;

    public ClientImpl(PhotoService photoService, Client client) {
        this.photoService = photoService;
        this.client = client;
    }

    private void uploadReceived(String fileName, Path file) {
        Notification.show("Upload finished: " + fileName, HUMANIZED_MESSAGE);
        try {
            File photo = photoService.getRelatedPhoto(client.getUuid());
            byte[] bytes = Files.readAllBytes(file);
            if(photo==null || photo.getFile()!=null) {
                photo = new File(UUID.randomUUID().toString(), client.getUuid(), "PHOTO", "", "" , LocalDate.now(), bytes);
            } else {
                photo.setFile(bytes);
            }
            photoService.save(photo);
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
