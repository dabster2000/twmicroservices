package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Image;

/**
 * !! DO NOT EDIT THIS FILE !!
 * <p>
 * This class is generated by Vaadin Designer and will be overwritten.
 * <p>
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class TopCardDesign extends VerticalLayout {
    private VerticalLayout cardHolder;
    private Image imgIcon;
    private Label lblNumber;
    private Label lblSubtitle;
    private Label lblTitle;

    public TopCardDesign() {
        Design.read(this);
    }

    public VerticalLayout getCardHolder() {
        return cardHolder;
    }

    public Image getImgIcon() {
        return imgIcon;
    }

    public Label getLblNumber() {
        return lblNumber;
    }

    public Label getLblSubtitle() {
        return lblSubtitle;
    }

    public Label getLblTitle() {
        return lblTitle;
    }

}