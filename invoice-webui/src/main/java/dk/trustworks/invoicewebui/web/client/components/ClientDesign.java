package dk.trustworks.invoicewebui.web.client.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.addon.onoffswitch.OnOffSwitch;
import com.vaadin.ui.ComboBox;
import com.vaadin.annotations.PropertyId;

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
public class ClientDesign extends VerticalLayout {
    private VerticalLayout cardHolder;
    private Label lblTitle;
    private VerticalLayout container;
    private FormLayout formLayout;
    private TextField txtName;
    @PropertyId("username")
    private ComboBox<dk.trustworks.invoicewebui.model.User> cbClientManager;
    private OnOffSwitch btnActive;

    public ClientDesign() {
        Design.read(this);
    }

    public VerticalLayout getCardHolder() {
        return cardHolder;
    }

    public Label getLblTitle() {
        return lblTitle;
    }

    public VerticalLayout getContainer() {
        return container;
    }

    public FormLayout getFormLayout() {
        return formLayout;
    }

    public TextField getTxtName() {
        return txtName;
    }

    public ComboBox<dk.trustworks.invoicewebui.model.User> getCbClientManager() {
        return cbClientManager;
    }

    public OnOffSwitch getBtnActive() {
        return btnActive;
    }

}
