package dk.trustworks.invoicewebui.web.common;

import com.vaadin.ui.Component;

public class BoxImpl extends Box {

    public BoxImpl instance(Component component) {
        this.getContent().addComponent(component);
        return this;
    }

    public BoxImpl withWidth(float width, Unit unit) {
        this.getContent().setWidth(width, unit);
        return this;
    }

    public BoxImpl witHeight(float height, Unit unit) {
        this.getContent().setHeight(height, unit);
        return this;
    }

    public BoxImpl withBgStyle(String style) {
        this.getContent().addStyleName(style);
        return this;
    }
}
