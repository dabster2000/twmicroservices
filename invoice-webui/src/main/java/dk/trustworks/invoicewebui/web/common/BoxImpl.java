package dk.trustworks.invoicewebui.web.common;

import com.vaadin.ui.Component;

public class BoxImpl extends Box {

    public Box instance(Component component) {
        this.getContent().addComponent(component);
        return this;
    }

}
