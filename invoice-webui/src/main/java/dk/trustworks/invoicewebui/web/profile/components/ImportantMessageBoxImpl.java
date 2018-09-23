package dk.trustworks.invoicewebui.web.profile.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;

public class ImportantMessageBoxImpl extends ImportantMessageBox {

    public ImportantMessageBoxImpl(String line1, String line2) {
        getLblPrimaryDescription().setValue(line1);
        getLblSecondaryDescription().setValue(line2);
    }

    public ImportantMessageBoxImpl withHalftoneSecondline() {
        getLblSecondaryDescription().addStyleName("grey-font");
        return this;
    }

    public ImportantMessageBoxImpl withComponent(Component component) {
        getVlContents().addComponent(component);
        getVlContents().setComponentAlignment(component, Alignment.TOP_CENTER);
        return this;
    }
}
