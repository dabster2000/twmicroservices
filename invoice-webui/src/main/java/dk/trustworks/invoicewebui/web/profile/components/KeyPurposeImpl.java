package dk.trustworks.invoicewebui.web.profile.components;

import dk.trustworks.invoicewebui.model.KeyPurpose;

import java.util.List;

public class KeyPurposeImpl extends KeyPurposeDesign {

    public KeyPurposeImpl(List<KeyPurpose> keyPurposeList) {
        this.getLblPurp1().setValue(keyPurposeList.get(0).getDescription());
        this.getLblPurp2().setValue(keyPurposeList.get(1).getDescription());
        this.getLblPurp3().setValue(keyPurposeList.get(2).getDescription());
    }
}
