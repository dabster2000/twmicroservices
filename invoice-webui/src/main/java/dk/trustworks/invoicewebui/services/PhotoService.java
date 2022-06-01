package dk.trustworks.invoicewebui.services;

import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;
import dk.trustworks.invoicewebui.model.File;
import dk.trustworks.invoicewebui.network.rest.FileRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class PhotoService {

    @Autowired
    private FileRestService fileRestService;

    public Image getRoundMemberImage(String useruuid, boolean isOwner) {
        return getRoundMemberImage(useruuid, isOwner, 75, Sizeable.Unit.PIXELS);
    }

    public Image getRoundMemberImage(String useruuid, boolean isOwner, int width, Sizeable.Unit unit) {
        File photo = fileRestService.findPhotoByRelateduuid(useruuid);

        Image image = new Image();
        //image.setDescription(member.getFirstname()+" "+member.getLastname());
        if(photo!=null && photo.getFile()!=null && photo.getFile()!=null) {
            image.setSource(
                    new StreamResource((StreamResource.StreamSource) () ->
                            new ByteArrayInputStream(photo.getFile()),
                            useruuid + System.currentTimeMillis() + ".jpg"));
        } else {
            image.setSource(new ThemeResource("images/clients/missing-logo.jpg"));
        }
        if(isOwner) image.setStyleName("img-circle-gold");
        else image.setStyleName("img-circle");
        image.setWidth(width, unit);
        image.setHeight(width, unit);
        return image;
    }

    public Image getRoundImage(String uuid, boolean isOwner, int width, Sizeable.Unit unit) {
        File photo = fileRestService.findPhotoByRelateduuid(uuid);

        Image image = new Image();
        if(photo!=null && photo.getFile()!=null && photo.getFile()!=null) {
            image.setSource(
                    new StreamResource((StreamResource.StreamSource) () ->
                            new ByteArrayInputStream(photo.getFile()),
                            uuid + System.currentTimeMillis() + ".jpg"));
        } else {
            image.setSource(new ThemeResource("images/clients/missing-logo.jpg"));
        }
        image.setStyleName("img-circle");
        image.setWidth(width, unit);
        image.setHeight(width, unit);
        return image;
    }

    public Resource getRelatedPhotoResource(String relatedUUID) {
        File photo;
        try {
            photo = fileRestService.findPhotoByRelateduuid(relatedUUID);
        } catch (Exception e) {
            return new ThemeResource("images/clients/missing-logo.jpg");
        }
        if(photo!=null && photo.getFile()!=null && photo.getFile().length > 0) {
            return new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(photo.getFile()), System.currentTimeMillis() + ".jpg");
        } else {
            return new ThemeResource("images/clients/missing-logo.jpg");
        }
    }

    public File getRelatedPhoto(String relatedUUID) {
        return fileRestService.findPhotoByRelateduuid(relatedUUID);
    }

    public void save(File photo) {
        fileRestService.update(photo);
    }
}
