package dk.trustworks.invoicewebui.web.profile.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

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
public class CKOExpenseDesign extends VerticalLayout {
    private VerticalLayout cardHolder;
    private Label lblTitle;
    private VerticalLayout container;
    private Grid<dk.trustworks.invoicewebui.model.CKOExpense> gridCKOExpenses;
    private HorizontalLayout hlAddBar;
    private DateField dfDate;
    private TextField txtDescription;
    private ComboBox<String> cbType;
    private TextField txtPrice;
    private Button btnAddSalary;
    private Button btnDelete;

    public CKOExpenseDesign() {
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

    public Grid<dk.trustworks.invoicewebui.model.CKOExpense> getGridCKOExpenses() {
        return gridCKOExpenses;
    }

    public HorizontalLayout getHlAddBar() {
        return hlAddBar;
    }

    public DateField getDfDate() {
        return dfDate;
    }

    public TextField getTxtDescription() {
        return txtDescription;
    }

    public ComboBox<String> getCbType() {
        return cbType;
    }

    public TextField getTxtPrice() {
        return txtPrice;
    }

    public Button getBtnAddSalary() {
        return btnAddSalary;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }

}
