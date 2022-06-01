package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.TextArea;
import dk.trustworks.invoicewebui.model.KeyPurpose;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.KeyPurposeRepository;
import dk.trustworks.invoicewebui.web.common.Box;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.List;

@Service
public class PurposeBox {

    @Autowired
    private KeyPurposeRepository keyPurposeRepository;

    public Box createPurposeBox(User user) {
        TextArea purp1 = new TextArea("Key Purpose 1:");
        purp1.setWidth(100, Sizeable.Unit.PERCENTAGE);
        purp1.addBlurListener(event -> {
            KeyPurpose keyPurpose = keyPurposeRepository.findByUseruuidAndNum(user.getUuid(), 1);
            if(keyPurpose == null) keyPurpose = keyPurposeRepository.save(new KeyPurpose(user, 1, ""));
            keyPurpose.setDescription(purp1.getValue());
            keyPurposeRepository.save(keyPurpose);
        });
        TextArea purp2 = new TextArea("Key Purpose 2:");
        purp2.setWidth(100, Sizeable.Unit.PERCENTAGE);
        purp2.addBlurListener(event -> {
            KeyPurpose keyPurpose = keyPurposeRepository.findByUseruuidAndNum(user.getUuid(), 2);
            if(keyPurpose == null) keyPurpose = keyPurposeRepository.save(new KeyPurpose(user, 2, ""));
            keyPurpose.setDescription(purp2.getValue());
            keyPurposeRepository.save(keyPurpose);
        });
        TextArea purp3 = new TextArea("Key Purpose 3:");
        purp3.setWidth(100, Sizeable.Unit.PERCENTAGE);
        purp3.addBlurListener(event -> {
            KeyPurpose keyPurpose = keyPurposeRepository.findByUseruuidAndNum(user.getUuid(), 3);
            if(keyPurpose == null) keyPurpose = keyPurposeRepository.save(new KeyPurpose(user, 3, ""));
            keyPurpose.setDescription(purp3.getValue());
            keyPurposeRepository.save(keyPurpose);
        });

        purp1.setValue("");
        purp2.setValue("");
        purp3.setValue("");
        List<KeyPurpose> keyPurposeList = keyPurposeRepository.findByUseruuidOrderByNumAsc(user.getUuid());
        if(keyPurposeList.size()>0) purp1.setValue(keyPurposeList.get(0).getDescription());
        if(keyPurposeList.size()>1) purp2.setValue(keyPurposeList.get(1).getDescription());
        if(keyPurposeList.size()>2) purp3.setValue(keyPurposeList.get(2).getDescription());

        Box purpBox = new BoxImpl().instance(new MVerticalLayout(purp1, purp2, purp3).withWidth(100, Sizeable.Unit.PERCENTAGE));
        return purpBox;
    }

}
