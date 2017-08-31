package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.vaadin.server.FontIcon;
import com.vaadin.ui.Button;
import org.vaadin.alump.materialicons.MaterialIcons;

/**
 * Created by hans on 30/08/2017.
 */
public class MenuItemImpl extends MenuItemDesign {

    public MenuItemImpl withCaption(String caption) {
        getBtnMenuitemText().setCaption(caption);
        return this;
    }

    public MenuItemImpl withParentIndicator(String indicator) {
        getLblParent().setValue(indicator);
        return this;
    }

    public MenuItemImpl withIcon(FontIcon icon) {
        getBtnMenuitemText().setIcon(icon);
        return this;
    }

    public MenuItemImpl addClickListener(Button.ClickListener listener) {
        getBtnMenuitemText().addClickListener(listener);
        return this;
    }

    public MenuItemImpl isChild(boolean isChild) {
        if(isChild) {
            this.setVisible(true);
            getLblChildSpace().setVisible(true);
        }
        return this;
    }
}
