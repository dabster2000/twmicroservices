package dk.trustworks.invoicewebui.web.mainmenu.cards;

import com.vaadin.server.ThemeResource;

/**
 * Created by hans on 11/08/2017.
 */
public class MenuItemCardImpl extends MenuItemCardDesign {

    public MenuItemCardImpl(String image, String headline, String body) {
        getImgTop().setSource(new ThemeResource("images/"+image));
        getImgTop().setSizeFull();
        getLblHeading().setValue(headline);
        getLblBody().setValue(body);
    }
}
