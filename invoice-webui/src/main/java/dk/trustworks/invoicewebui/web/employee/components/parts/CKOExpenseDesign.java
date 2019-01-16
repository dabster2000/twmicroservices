package dk.trustworks.invoicewebui.web.employee.components.parts;

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
import com.vaadin.ui.TextArea;

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
    private Button btnEdit;
    private VerticalLayout chartContainer;
    private HorizontalLayout dataContainer;
    private VerticalLayout hlAddBar;
    private DateField dfDate;
    private TextField txtDays;
    private TextField txtDescription;
    private TextArea txtComments;
    private ComboBox<String> cbType;
    private ComboBox<String> cbStatus;
    private ComboBox<String> cbPurpose;
    private TextField txtPrice;
    private Button btnAddSalary;
    private Grid<dk.trustworks.invoicewebui.model.CKOExpense> gridCKOExpenses;
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

    public Button getBtnEdit() {
        return btnEdit;
    }

    public VerticalLayout getChartContainer() {
        return chartContainer;
    }

    public HorizontalLayout getDataContainer() {
        return dataContainer;
    }

    public VerticalLayout getHlAddBar() {
        return hlAddBar;
    }

    public DateField getDfDate() {
        return dfDate;
    }

    public TextField getTxtDays() {
        return txtDays;
    }

    public TextField getTxtDescription() {
        return txtDescription;
    }

    public TextArea getTxtComments() {
        return txtComments;
    }

    public ComboBox<String> getCbType() {
        return cbType;
    }

    public ComboBox<String> getCbStatus() {
        return cbStatus;
    }

    public ComboBox<String> getCbPurpose() {
        return cbPurpose;
    }

    public TextField getTxtPrice() {
        return txtPrice;
    }

    public Button getBtnAddSalary() {
        return btnAddSalary;
    }

    public Grid<dk.trustworks.invoicewebui.model.CKOExpense> getGridCKOExpenses() {
        return gridCKOExpenses;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }

}
