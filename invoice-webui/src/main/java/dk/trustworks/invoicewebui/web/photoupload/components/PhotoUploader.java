package dk.trustworks.invoicewebui.web.photoupload.components;

import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.model.File;
import dk.trustworks.invoicewebui.services.PhotoService;
import org.vaadin.liveimageeditor.LiveImageEditor;
import server.droporchoose.UploadComponent;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.UUID;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static com.vaadin.server.Sizeable.Unit.PIXELS;
import static com.vaadin.ui.Notification.Type.*;

public class PhotoUploader {


    private PhotoService photoService;
    private final String uuid;
    private final int width;
    private final int height;
    private String caption;
    private final Step startStep;
    private Done done = null;

    private LiveImageEditor imageEditor;
    private Label instructions = new Label("<b>Drag</b> to move the image. Press <b>SHIFT</b> while dragging to rotate. Use the <b>mouse wheel</b> to scale. Click <b>Send</b> to transform the image on the server.");
    private Button btnSavePhoto = new Button("Save Photo");
    private UploadComponent uploadComponent = new UploadComponent();
    private Image editedImage = new Image();
    private Window window;
    private VerticalLayout vlPopupContainer;
    private VerticalLayout vlContainer;

    public PhotoUploader(String uuid, int width, int height, String caption, Step startStep, PhotoService photoService) {
        this.width = width;
        this.height = height;
        this.caption = caption;
        this.startStep = startStep;
        this.photoService = photoService;
        this.uuid = uuid;

        editedImage.setWidth(width, PIXELS);
        editedImage.setHeight(height, PIXELS);

        vlContainer = new VerticalLayout();
        vlPopupContainer = new VerticalLayout();
        vlContainer.setWidth(width, PIXELS);
        vlPopupContainer.setWidth(width, PIXELS);
    }

    public PhotoUploader(String uuid, int widthPercent, int heightPercent, int width, int height, String caption, Step startStep, PhotoService photoService) {
        this.width = width;
        this.height = height;
        this.caption = caption;
        this.startStep = startStep;
        this.photoService = photoService;
        this.uuid = uuid;

        editedImage.setWidth(widthPercent, PERCENTAGE);
        editedImage.setHeight(heightPercent, PERCENTAGE);

        vlContainer = new VerticalLayout();
        vlPopupContainer = new VerticalLayout();
        vlContainer.setWidth(widthPercent, PERCENTAGE);
        vlPopupContainer.setWidth(width, PIXELS);
    }

    public PhotoUploader(String uuid, int width, int height, String caption, Step startStep, PhotoService photoService, Done done) {
        this(uuid, width, height, caption, startStep, photoService);
        this.done = done;
    }

    public PhotoUploader(String uuid, int widthPercent, int heightPercent, int width, int height, String caption, Step startStep, PhotoService photoService, Done done) {
        this(uuid, widthPercent, heightPercent, width, height, caption, startStep, photoService);
        this.done = done;
    }

    public VerticalLayout getUploader() {
        uploadComponent.setReceivedCallback(this::uploadReceived);
        uploadComponent.setStartedCallback(this::uploadStarted);
        uploadComponent.setProgressCallback(this::uploadProgress);
        uploadComponent.setFailedCallback(this::uploadFailed);
        uploadComponent.setWidth(width, PIXELS);
        uploadComponent.setHeight(height, PIXELS);
        uploadComponent.setCaption(caption);

        btnSavePhoto.addClickListener(this::saveEditedPhoto);
        btnSavePhoto.setWidth(100, PERCENTAGE);

        imageEditor = new LiveImageEditor(this::receiveImage);
        imageEditor.setWidth(width, PIXELS);
        imageEditor.setHeight(height, PIXELS);

        instructions.setContentMode(ContentMode.HTML);
        instructions.setWidth(100, PERCENTAGE);

        vlContainer.addComponents(editedImage);
        vlContainer.setComponentAlignment(editedImage, Alignment.TOP_CENTER);

        vlPopupContainer.setMargin(false);
        vlPopupContainer.addComponents(uploadComponent, instructions, imageEditor, btnSavePhoto);
        window = new Window("Upload new photo");
        window.setModal(true);
        window.setClosable(true);
        window.setContent(vlPopupContainer);
        window.addCloseListener(e -> {
            if(done == null) setupFinalStep();
            else done.uploaderDone();
        });
        //window.setWidth(width+50, PIXELS);
        //window.setHeight(height+50, PIXELS);

        editedImage.addClickListener(event -> setupUploadStep());

        File photo = photoService.getRelatedPhoto(uuid);
        if(photo!=null && photo.getFile()!=null && photo.getFile().length > 0 && startStep.equals(Step.PHOTO)) {
            editedImage.setSource(new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(photo.getFile()),
                    "photo-" + System.currentTimeMillis() + ".jpg"));
            setupFinalStep();
        } else {
            setupUploadStep();
        }

        return vlContainer;
    }

    private void uploadReceived(String fileName, Path file) {
        Notification.show("Upload finished: " + fileName, HUMANIZED_MESSAGE);

        try {
            imageEditor.setImage(Files.readAllBytes(file));
            imageEditor.resetTransformations();
            setupEditingStep();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveImage(InputStream inputStream) {
        try {
            BufferedImage inputImage = ImageIO.read(inputStream);

            BufferedImage image = new BufferedImage(width, height, inputImage.getType());
            Graphics2D g2d = image.createGraphics();
            g2d.drawImage(inputImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, width, height, null);
            g2d.dispose();

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (!writers.hasNext()) throw new IllegalStateException("No writers found");

            ByteArrayOutputStream os = new ByteArrayOutputStream(37628);

            float quality = 0.8f;

            ImageWriter writer = writers.next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();

            // compress to a given quality
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            writer.write(null, new IIOImage(image, null, null), param);

            byte[] bytes = os.toByteArray();

            // close all streams
            os.close();
            ios.close();
            writer.dispose();


            File photo = photoService.getRelatedPhoto(uuid);
            if(photo==null) {
                photo = new File(UUID.randomUUID().toString(), uuid, "PHOTO", "", "", LocalDate.now(), bytes);
            } else {
                photo.setFile(bytes);
            }
            StreamResource resource = new StreamResource(() -> new ByteArrayInputStream(bytes), "edited-image-" + System.currentTimeMillis() + ".jpg");
            editedImage.setSource(resource);
            photoService.save(photo);
            if(done == null) setupFinalStep();
            else done.uploaderDone();
        } catch (IOException e) {
            Notification.show("Upload failed", ERROR_MESSAGE);
        }
    }

    private void saveEditedPhoto(Button.ClickEvent event) {
        imageEditor.requestEditedImage();
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

    private void setupUploadStep() {
        uploadComponent.setVisible(true);
        instructions.setVisible(false);
        imageEditor.setVisible(false);
        btnSavePhoto.setVisible(false);
        UI.getCurrent().addWindow(window);
        //editedImage.setVisible(false);
    }

    private void setupEditingStep() {
        uploadComponent.setVisible(false);
        instructions.setVisible(true);
        imageEditor.setVisible(true);
        btnSavePhoto.setVisible(true);
        //editedImage.setVisible(false);
    }

    private void setupFinalStep() {
        uploadComponent.setVisible(false);
        instructions.setVisible(false);
        imageEditor.setVisible(false);
        btnSavePhoto.setVisible(false);
        window.close();
        //editedImage.setVisible(true);
    }

    @FunctionalInterface
    public interface Done {
        void uploaderDone();
    }

    public enum Step {
        PHOTO, UPLOAD, RESIZE
    }
}
