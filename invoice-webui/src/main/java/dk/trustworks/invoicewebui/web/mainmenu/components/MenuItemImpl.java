package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.vaadin.server.FontIcon;
import com.vaadin.ui.Button;
import org.vaadin.alump.materialicons.MaterialIcons;

/**
 * Created by hans on 30/08/2017.
 */
public class MenuItemImpl extends MenuItemDesign {

    public static FontIcon PLUS_INDICATOR = MaterialIcons.ADD;
    public static FontIcon MINUS_INDICATOR = MaterialIcons.REMOVE;

    public MenuItemImpl withCaption(String caption) {
        getBtnMenuitemText().setCaption(caption);
        return this;
    }

    public MenuItemImpl withParentIndicator(FontIcon indicator) {
        getBtnParentIndicator().setIcon(indicator);
        return this;
    }

    public MenuItemImpl withIcon(FontIcon icon) {
        getBtnMenuitemText().setIcon(icon);
        return this;
    }

    public MenuItemImpl withFontStyle(String style) {
        getBtnMenuitemText().addStyleName(style);
        return this;
    }

    public MenuItemImpl addClickListener(Button.ClickListener listener) {
        getBtnMenuitemText().addClickListener(listener);
        return this;
    }

    public MenuItemImpl setChild(boolean isChild) {
        if(isChild) {
            //this.setVisible(false);
            getBtnMenuitemText().addStyleName("tiny");
            getLblChildSpace().setVisible(true);
        }
        return this;
    }
}
