package dk.trustworks.invoicewebui.web.time.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.PropertyId;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
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
public class DateButtons extends HorizontalLayout {
    private Button btnWeekNumberDecr;
    private TextField txtWeekNumber;
    private Button btnWeekNumberIncr;
    @PropertyId("username")
    private ComboBox<dk.trustworks.invoicewebui.model.User> selActiveUser;

    public DateButtons() {
        Design.read(this);
    }

    public Button getBtnWeekNumberDecr() {
        return btnWeekNumberDecr;
    }

    public TextField getTxtWeekNumber() {
        return txtWeekNumber;
    }

    public Button getBtnWeekNumberIncr() {
        return btnWeekNumberIncr;
    }

    public ComboBox<dk.trustworks.invoicewebui.model.User> getSelActiveUser() {
        return selActiveUser;
    }

}
