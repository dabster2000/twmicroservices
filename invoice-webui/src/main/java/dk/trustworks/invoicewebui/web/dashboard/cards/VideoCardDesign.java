package dk.trustworks.invoicewebui.web.dashboard.cards;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Label;

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
public class VideoCardDesign extends VerticalLayout {
    private VerticalLayout cardHolder;
    private Label lblTitle;
    private VerticalLayout iframeHolder;

    public VideoCardDesign() {
        Design.read(this);
    }

    public VerticalLayout getCardHolder() {
        return cardHolder;
    }

    public Label getLblTitle() {
        return lblTitle;
    }

    public VerticalLayout getIframeHolder() {
        return iframeHolder;
    }

}