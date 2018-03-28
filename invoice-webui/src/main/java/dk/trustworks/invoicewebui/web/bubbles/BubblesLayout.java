package dk.trustworks.invoicewebui.web.bubbles;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.web.bubbles.components.BubbleFormDesign;
import dk.trustworks.invoicewebui.web.bubbles.components.BubblesDesign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import server.droporchoose.UploadComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static com.vaadin.ui.Notification.Type.ERROR_MESSAGE;
import static com.vaadin.ui.Notification.Type.HUMANIZED_MESSAGE;
import static com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION;

@SpringComponent
@SpringUI
public class BubblesLayout extends VerticalLayout {

    private PhotoRepository photoRepository;

    @Autowired
    public BubblesLayout() {
        ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

        ResponsiveRow formRow = responsiveLayout.addRow();
        BubbleFormDesign bubbleFormDesign = new BubbleFormDesign();

        UploadComponent uploadComponent = new UploadComponent(this::uploadReceived);
        uploadComponent.setStartedCallback(this::uploadStarted);
        uploadComponent.setProgressCallback(this::uploadProgress);
        uploadComponent.setFailedCallback(this::uploadFailed);
        uploadComponent.setWidth(100, Unit.PERCENTAGE);
        uploadComponent.setHeight(200, Unit.PIXELS);
        uploadComponent.setCaption("File upload");

        formRow.addColumn().withDisplayRules(12, 12, 8, 8).withComponent(bubbleFormDesign, ResponsiveColumn.ColumnComponentAlignment.CENTER);
        formRow.addColumn().withDisplayRules(12, 12, 4, 4).withComponent(bubbleFormDesign, ResponsiveColumn.ColumnComponentAlignment.CENTER);

        BubblesDesign bubblesDesign = new BubblesDesign();
        for (int i = 0; i < 10; i++) {
            Resource res = new ThemeResource("images/hans.png");
            Image image = new Image(null, res);
            image.setStyleName("img-circle");
            image.setWidth(75, Unit.PIXELS);
            image.setHeight(75, Unit.PIXELS);
            bubblesDesign.getPhotoContainer().addComponent(image);
        }

        bubblesDesign.getImgTop().setSource(new ThemeResource("images/cards/summer.jpg"));

        responsiveLayout.addRow().addColumn().withDisplayRules(12, 12, 6,4).withComponent(bubblesDesign);
        this.addComponent(responsiveLayout);
    }

    @Transactional
    public BubblesLayout init() {
        return this;
    }

    private void uploadReceived(String fileName, Path file) {
        Notification.show("Upload finished: " + fileName, HUMANIZED_MESSAGE);
        try {
            Photo photo = photoRepository.findByRelateduuid("");
            byte[] bytes = Files.readAllBytes(file);
            if(photo==null) {
                photo = new Photo(UUID.randomUUID().toString(), "", bytes);
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
