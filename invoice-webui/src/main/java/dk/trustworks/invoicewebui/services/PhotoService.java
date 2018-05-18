package dk.trustworks.invoicewebui.services;

import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
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
        Photo photo = photoRepository.findByRelateduuid(member.getUuid());

        Image image = new Image(null,
                new StreamResource((StreamResource.StreamSource) () ->
                        new ByteArrayInputStream(photo.getPhoto()),
                        member.getUsername()+System.currentTimeMillis()+".jpg"));
        if(isOwner) image.setStyleName("img-circle-gold");
        else image.setStyleName("img-circle");
        image.setWidth(75, Sizeable.Unit.PIXELS);
        image.setHeight(75, Sizeable.Unit.PIXELS);
        return image;
    }
}
