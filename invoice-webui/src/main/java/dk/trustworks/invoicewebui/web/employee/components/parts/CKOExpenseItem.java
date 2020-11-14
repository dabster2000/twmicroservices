package dk.trustworks.invoicewebui.web.employee.components.parts;

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
public class CKOExpenseItem extends VerticalLayout {
    private VerticalLayout cardHolder;
    private HorizontalLayout content;
    private Image imgIcon;
    private Image imgAmortized;
    private Label lblDate;
    private Label lblDays;
    private Label lblDescription;
    private Label lblPurpose;
    private Label lblAmount;
    private CssLayout vlButtonLayout;
    private Button btnEdit;
    private Button btnDelete;
    private VerticalLayout vlStatus;
    private VerticalLayout col1;
    private VerticalLayout col2;
    private VerticalLayout col3;
    private VerticalLayout col4;

    public CKOExpenseItem() {
        Design.read(this);
    }

    public VerticalLayout getCardHolder() {
        return cardHolder;
    }

    public Image getImgIcon() {
        return imgIcon;
    }

    public Image getImgAmortized() {
        return imgAmortized;
    }

    public Label getLblDate() {
        return lblDate;
    }

    public Label getLblDays() {
        return lblDays;
    }

    public Label getLblDescription() {
        return lblDescription;
    }


    public Label getLblPurpose() {
        return lblPurpose;
    }

    public Label getLblAmount() {
        return lblAmount;
    }

    public VerticalLayout getVLStatus() {
        return vlStatus;
    }

    public CssLayout getVlButtonLayout() {
        return vlButtonLayout;
    }

    public Button getBtnEdit() {
        return btnEdit;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }


    public VerticalLayout getCol1() {
        return col1;
    }

    public VerticalLayout getCol2() {
        return col2;
    }

    public VerticalLayout getCol3() {
        return col3;
    }

    public VerticalLayout getCol4() {
        return col4;
    }

    public HorizontalLayout getContent() {
        return content;
    }
}
