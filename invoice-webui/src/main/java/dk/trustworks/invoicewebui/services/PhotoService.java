package dk.trustworks.invoicewebui.services;

import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;
import dk.trustworks.invoicewebui.model.Photo;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    public Image getRoundMemberImage(User member, boolean isOwner) {
        return getRoundMemberImage(member, isOwner, 75, Sizeable.Unit.PIXELS);
    }

    public Image getRoundMemberImage(User member, boolean isOwner, int width, Sizeable.Unit unit) {
        Photo photo = photoRepository.findByRelateduuid(member.getUuid());

        Image image = new Image();
        if(photo!=null && photo.getPhoto()!=null) {
            image.setSource(
                    new StreamResource((StreamResource.StreamSource) () ->
                            new ByteArrayInputStream(photo.getPhoto()),
                            member.getUsername() + System.currentTimeMillis() + ".jpg"));
        } else {
            image.setSource(new ThemeResource("images/clients/missing-logo.jpg"));
        }
        if(isOwner) image.setStyleName("img-circle-gold");
        else image.setStyleName("img-circle");
        image.setWidth(width, unit);
        image.setHeight(width, unit);
        return image;
    }

    public Resource getRelatedPhoto(String relatedUUID) {
        Photo photo = photoRepository.findByRelateduuid(relatedUUID);
        if(photo!=null && photo.getPhoto().length > 0) {
            return new StreamResource((StreamResource.StreamSource) () ->
                    new ByteArrayInputStream(photo.getPhoto()), System.currentTimeMillis() + ".jpg");
        } else {
            return new ThemeResource("images/clients/missing-logo.jpg");
        }
    }
}
