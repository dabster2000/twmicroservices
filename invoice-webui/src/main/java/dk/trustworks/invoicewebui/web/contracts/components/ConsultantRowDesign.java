package dk.trustworks.invoicewebui.web.contracts.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.VerticalLayout;

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
public class ConsultantRowDesign extends HorizontalLayout {
    private CssLayout imgPhoto;
    private Label lblName;
    private TextField txtRate;
    private VerticalLayout vlHours;
    private TextField txtHours;

    public ConsultantRowDesign() {
        Design.read(this);
    }

    public CssLayout getImgPhoto() {
        return imgPhoto;
    }

    public Label getLblName() {
        return lblName;
    }

    public TextField getTxtRate() {
        return txtRate;
    }

    public VerticalLayout getVlHours() {
        return vlHours;
    }

    public TextField getTxtHours() {
        return txtHours;
    }

}
