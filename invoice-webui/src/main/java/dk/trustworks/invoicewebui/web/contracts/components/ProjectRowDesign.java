package dk.trustworks.invoicewebui.web.contracts.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;

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
public class ProjectRowDesign extends HorizontalLayout {
    private Button btnIcon;
    private Label lblName;
    private Button btnDelete;

    public ProjectRowDesign() {
        Design.read(this);
    }

    public Button getBtnIcon() {
        return btnIcon;
    }

    public Label getLblName() {
        return lblName;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }

}
