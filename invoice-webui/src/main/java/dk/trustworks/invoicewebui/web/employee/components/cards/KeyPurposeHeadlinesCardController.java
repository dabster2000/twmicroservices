package dk.trustworks.invoicewebui.web.employee.components.cards;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import dk.trustworks.invoicewebui.model.KeyPurpose;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.KeyPurposeRepository;
import dk.trustworks.invoicewebui.web.employee.components.parts.KeyPurposeImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringUI
@SpringComponent
public class KeyPurposeHeadlinesCardController {

    private final KeyPurposeRepository keyPurposeRepository;

    private User user;

    @Autowired
    public KeyPurposeHeadlinesCardController(KeyPurposeRepository keyPurposeRepository) {
        this.keyPurposeRepository = keyPurposeRepository;
    }

    public Component getCard(User user) {
        List<KeyPurpose> keyPurposeList = keyPurposeRepository.findByUseruuidOrderByNumAsc(user.getUuid());
        while(keyPurposeList.size()<3) {
            KeyPurpose keyPurpose = keyPurposeRepository.save(new KeyPurpose(user, keyPurposeList.size() + 1, "No Purposes Yet!"));
            keyPurposeList.add(keyPurpose);
        }
        return new KeyPurposeImpl(keyPurposeList);
    }
}
