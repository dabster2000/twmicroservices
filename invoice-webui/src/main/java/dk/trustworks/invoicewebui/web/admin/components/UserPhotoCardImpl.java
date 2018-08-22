package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.repositories.PhotoRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.photoupload.components.PhotoUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hans on 09/09/2017.
 */
@SpringUI
@SpringComponent
public class UserPhotoCardImpl extends UserPhotoCardDesign {

    @Autowired
    private PhotoRepository photoRepository;

    public UserPhotoCardImpl() {
    }

    @Transactional
    @AccessRules(roleTypes = {RoleType.ADMIN, RoleType.PARTNER, RoleType.CXO})
    public void init(String userUUID) {
        getContainer().removeAllComponents();
        getContainer().addComponents(new PhotoUploader(userUUID, 400, 400, "Upload a photograph of this employee:", PhotoUploader.Step.PHOTO, photoRepository).getUploader());
    }

}
