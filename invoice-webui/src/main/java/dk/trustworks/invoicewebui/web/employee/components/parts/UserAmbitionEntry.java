package dk.trustworks.invoicewebui.web.employee.components.parts;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;
import org.vaadin.teemu.ratingstars.RatingStars;

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
public class UserAmbitionEntry extends HorizontalLayout {
    private Label lblAmbitionName;
    private RatingStars ratingStars;
    private Button btnAmbition;

    public UserAmbitionEntry() {
        Design.read(this);
    }

    public Label getLblAmbitionName() {
        return lblAmbitionName;
    }

    public RatingStars getRatingStars() {
        return ratingStars;
    }

    public Button getBtnAmbition() {
        return btnAmbition;
    }

}
