package dk.trustworks.invoicewebui.web.photoupload.components;

import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
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
import java.util.Iterator;
import java.util.UUID;

import static com.vaadin.ui.Notification.Type.*;

public class PhotoUploader {


    private PhotoRepository photoRepository;
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

    public PhotoUploader(String uuid, int width, int height, String caption, Step startStep, PhotoRepository photoRepository) {
        this.width = width;
        this.height = height;
        this.caption = caption;
        this.startStep = startStep;
        this.photoRepository = photoRepository;
        this.uuid = uuid;
    }

    public PhotoUploader(String uuid, int width, int height, String caption, Step startStep, PhotoRepository photoRepository, Done done) {
        this(uuid, width, height, caption, startStep, photoRepository);
        this.done = done;
    }

    public VerticalLayout getUploader() {
        VerticalLayout vlContainer = new VerticalLayout();
        vlContainer.setWidth(400, Sizeable.Unit.PIXELS);
        uploadComponent.setReceivedCallback(this::uploadReceived);
        uploadComponent.setStartedCallback(this::uploadStarted);
        uploadComponent.setProgressCallback(this::uploadProgress);
        uploadComponent.setFailedCallback(this::uploadFailed);
        uploadComponent.setWidth(width, Sizeable.Unit.PIXELS);
        uploadComponent.setHeight(height, Sizeable.Unit.PIXELS);
        uploadComponent.setCaption(caption);

        btnSavePhoto.addClickListener(this::saveEditedPhoto);

        imageEditor = new LiveImageEditor(this::receiveImage);
        imageEditor.setWidth(width, Sizeable.Unit.PIXELS);
        imageEditor.setHeight(height, Sizeable.Unit.PIXELS);

        instructions.setContentMode(ContentMode.HTML);
        instructions.setWidth(100, Sizeable.Unit.PERCENTAGE);

        vlContainer.addComponents(uploadComponent, instructions, imageEditor, btnSavePhoto, editedImage);
        vlContainer.setComponentAlignment(editedImage, Alignment.TOP_CENTER);

        editedImage.addClickListener(event -> setupUploadStep());

        Photo photo = photoRepository.findByRelateduuid(uuid);
        if(photo!=null && photo.getPhoto().length > 0 && startStep.equals(Step.PHOTO)) {
            editedImage.setSource(new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(photo.getPhoto()),
                    "photo-" + System.currentTimeMillis() + ".jpg"));
            setupFinalStep();
        } else if(startStep.equals(Step.UPLOAD)) {
            setupUploadStep();
        }
        return vlContainer;
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

    private void receiveImage(InputStream inputStream) {
        System.out.println("UserPhotoCardImpl.receiveImage");
        try {
            BufferedImage inputImage = ImageIO.read(inputStream);

            BufferedImage image = new BufferedImage(width, height, inputImage.getType());
            Graphics2D g2d = image.createGraphics();
            g2d.drawImage(inputImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, width, height, null);
            g2d.dispose();

            /*
            BufferedImage image = new BufferedImage(width, height, inputImage.getType());

            Graphics2D g2d = image.createGraphics();
            g2d.drawImage(inputImage, 0, 0, width, height, null);
            g2d.dispose();
            */

            //ImageIO.write(outputImage, formatName, new File(outputImagePath));

            System.out.println("image.getHeight() = " + image.getHeight());
            System.out.println("image.getWidth() = " + image.getWidth());
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
            System.out.println("PHOTO: "+ bytes.length);

            // close all streams
            os.close();
            ios.close();
            writer.dispose();


            Photo photo = photoRepository.findByRelateduuid(uuid);
            //byte[] bytes = IOUtils.toByteArray(inputStream);
            if(photo==null) {
                photo = new Photo(UUID.randomUUID().toString(), uuid, bytes);
            } else {
                photo.setPhoto(bytes);
            }
            StreamResource resource = new StreamResource(() -> new ByteArrayInputStream(bytes), "edited-image-" + System.currentTimeMillis() + ".jpg");
            editedImage.setSource(resource);
            photoRepository.save(photo);
            if(done == null) setupFinalStep();
            else done.uploaderDone();
        } catch (IOException e) {
            Notification.show("Upload failed", ERROR_MESSAGE);
        }
    }

    private void saveEditedPhoto(Button.ClickEvent event) {
        System.out.println("UserPhotoCardImpl.saveEditedPhoto");
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
        System.out.println("PhotoUploader.setupUploadStep");
        uploadComponent.setVisible(true);
        instructions.setVisible(false);
        imageEditor.setVisible(false);
        btnSavePhoto.setVisible(false);
        editedImage.setVisible(false);
    }

    private void setupEditingStep() {
        System.out.println("PhotoUploader.setupEditingStep");
        uploadComponent.setVisible(false);
        instructions.setVisible(true);
        imageEditor.setVisible(true);
        btnSavePhoto.setVisible(true);
        editedImage.setVisible(false);
    }

    private void setupFinalStep() {
        System.out.println("PhotoUploader.setupFinalStep");
        uploadComponent.setVisible(false);
        instructions.setVisible(false);
        imageEditor.setVisible(false);
        btnSavePhoto.setVisible(false);
        editedImage.setVisible(true);
    }

    @FunctionalInterface
    public interface Done {
        void uploaderDone();
    }

    public enum Step {
        PHOTO, UPLOAD, RESIZE
    }
}
