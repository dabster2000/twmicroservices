package dk.trustworks.invoicewebui.web.time.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.*;
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
public class TaskTitle extends HorizontalLayout {
    private Button btnDelete;
    private Image imgLogo;
    private Label txtTaskname;
    private Label txtProjectname;
    private Label lblDescription;
    private CssLayout imgConsultant;

    public TaskTitle() {
        Design.read(this);
    }

    public Button getBtnDelete() {
        return btnDelete;
    }

    public Image getImgLogo() {
        return imgLogo;
    }

    public Label getTxtTaskname() {
        return txtTaskname;
    }

    public Label getTxtProjectname() {
        return txtProjectname;
    }

    public Label getLblDescription() {
        return lblDescription;
    }

    public CssLayout getImgConsultant() {
        return imgConsultant;
    }

}
