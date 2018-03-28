package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.liveimageeditor.LiveImageEditor;
import server.droporchoose.UploadComponent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static com.vaadin.ui.Notification.Type.*;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class UserPhotoCardImpl extends UserPhotoCardDesign {

    @Autowired
    private PhotoRepository photoRepository;

    private LiveImageEditor imageEditor;

    private Label instructions = new Label("<b>Drag</b> to move the image. Press <b>SHIFT</b> while dragging to rotate. Use the <b>mouse wheel</b> to scale. Click <b>Send</b> to transform the image on the server.");
    private Button btnSavePhoto = new Button("Save Photo");
    private UploadComponent uploadComponent = new UploadComponent();

    private String userUUID;

    public UserPhotoCardImpl() {
        uploadComponent.setReceivedCallback(this::uploadReceived);
        uploadComponent.setStartedCallback(this::uploadStarted);
        uploadComponent.setProgressCallback(this::uploadProgress);
        uploadComponent.setFailedCallback(this::uploadFailed);
        uploadComponent.setWidth(100, Unit.PERCENTAGE);
        uploadComponent.setHeight(200, Unit.PIXELS);
        uploadComponent.setCaption("Photo upload");

        btnSavePhoto.addClickListener(this::saveEditedPhoto);

        imageEditor = new LiveImageEditor(this::receiveImage);
        imageEditor.setWidth(400, Unit.PIXELS);
        imageEditor.setHeight(400, Unit.PIXELS);

        instructions.setContentMode(ContentMode.HTML);
        instructions.setWidth(100, Unit.PIXELS);

        getContainer().addComponents(uploadComponent, instructions, imageEditor, btnSavePhoto, getEditedImage());

        getEditedImage().addClickListener(event -> setupUploadStep());
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN, RoleType.PARTNER, RoleType.CXO})
    public void init(String userUUID) {
        this.userUUID = userUUID;

        Photo photo = photoRepository.findByRelateduuid(userUUID);
        if(photo!=null && photo.getPhoto().length > 0) {
            getEditedImage().setSource(new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(photo.getPhoto()),
                    "photo.jpg"));
            setupFinalStep();
        } else {
            setupUploadStep();
        }
    }

    private void uploadReceived(String fileName, Path file) {
        System.out.println("UserPhotoCardImpl.uploadReceived");
        Notification.show("Upload finished: " + fileName, HUMANIZED_MESSAGE);

        try {
            imageEditor.setImage(Files.readAllBytes(file));
            imageEditor.resetTransformations();
            setupEditingStep();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveEditedPhoto(Button.ClickEvent event) {
        System.out.println("UserPhotoCardImpl.saveEditedPhoto");
        imageEditor.requestEditedImage();
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

    private void receiveImage(InputStream inputStream) {
        System.out.println("UserPhotoCardImpl.receiveImage");
        try {
            Photo photo = photoRepository.findByRelateduuid(userUUID);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            if(photo==null) {
                photo = new Photo(UUID.randomUUID().toString(), userUUID, bytes);
            } else {
                photo.setPhoto(bytes);
            }
            photoRepository.save(photo);
        } catch (IOException e) {
            Notification.show("Upload failed", ERROR_MESSAGE);
        }


        StreamResource resource = new StreamResource(() -> inputStream, "edited-image-" + System.currentTimeMillis());
        getEditedImage().setSource(resource);
        setupFinalStep();
    }

    private void setupUploadStep() {
        uploadComponent.setVisible(true);
        instructions.setVisible(false);
        imageEditor.setVisible(false);
        btnSavePhoto.setVisible(false);
        getEditedImage().setVisible(false);
    }

    private void setupEditingStep() {
        uploadComponent.setVisible(false);
        instructions.setVisible(true);
        imageEditor.setVisible(true);
        btnSavePhoto.setVisible(true);
        getEditedImage().setVisible(false);
    }

    private void setupFinalStep() {
        uploadComponent.setVisible(false);
        instructions.setVisible(false);
        imageEditor.setVisible(false);
        btnSavePhoto.setVisible(false);
        getEditedImage().setVisible(true);
    }
}
