package dk.trustworks.invoicewebui.web.client.components;

import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.network.clients.ClientClientImpl;
import dk.trustworks.invoicewebui.network.dto.Client;
import server.droporchoose.UploadComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.vaadin.ui.Notification.Type.*;

/**
 * Created by hans on 13/08/2017.
 */
public class ClientImpl extends ClientDesign {

    private ClientClientImpl clientClient;
    private Client client;

    public ClientImpl(ClientClientImpl clientClient, Client client) {
        this.clientClient = clientClient;
        this.client = client;
        UploadComponent uploadComponent = new UploadComponent(this::uploadReceived);
        uploadComponent.setStartedCallback(this::uploadStarted);
        uploadComponent.setProgressCallback(this::uploadProgress);
        uploadComponent.setFailedCallback(this::uploadFailed);
        uploadComponent.setWidth(500, Unit.PIXELS);
        uploadComponent.setHeight(300, Unit.PIXELS);
        uploadComponent.setCaption("File upload");

        getFormLayout().addComponent(uploadComponent);
    }

    private void uploadReceived(String fileName, Path file) {
        Notification.show("Upload finished: " + fileName, HUMANIZED_MESSAGE);
        try {
            byte[] bytes = Files.readAllBytes(file);
            System.out.println("bytes.length = " + bytes.length);
            client.setLogo(bytes);
            System.out.println("clientResource = " + client);
            clientClient.save(client.getUuid(), client);
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
