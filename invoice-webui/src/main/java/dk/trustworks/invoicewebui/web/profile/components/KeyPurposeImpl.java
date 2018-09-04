package dk.trustworks.invoicewebui.web.profile.components;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.KeyPurposeRepository;

public class KeyPurposeImpl extends KeyPurposeDesign {

    public KeyPurposeImpl(User user, KeyPurposeRepository keyPurposeRepository) {

        this.getLblPurp1().setValue(keyPurposeRepository.findByUserAndNum(user, 1).getDescription());
        this.getLblPurp2().setValue(keyPurposeRepository.findByUserAndNum(user, 2).getDescription());
        this.getLblPurp3().setValue(keyPurposeRepository.findByUserAndNum(user, 3).getDescription());

    }
}
