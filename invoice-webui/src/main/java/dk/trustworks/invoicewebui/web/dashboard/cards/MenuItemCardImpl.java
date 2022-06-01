package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.server.ThemeResource;

/**
 * Created by hans on 11/08/2017.
 */
public class MenuItemCardImpl extends MenuItemCardDesign implements Component {

    private int priority;
    private int boxWidth;
    private String name;

    public MenuItemCardImpl(String image, String headline, String body, int priority, int boxWidth, String name) {
        getImgTop().setSource(new ThemeResource("images/"+image));
        getImgTop().setSizeFull();
        getLblHeading().setValue(headline);
        getLblBody().setValue(body);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public com.vaadin.ui.Component getBoxComponent() {
        return this;
    }
}
