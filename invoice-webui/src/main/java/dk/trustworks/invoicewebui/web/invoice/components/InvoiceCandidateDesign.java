package dk.trustworks.invoicewebui.web.invoice.components;

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
public class InvoiceCandidateDesign extends VerticalLayout {
    private VerticalLayout cardHolder;
    private Image imgTop;
    private VerticalLayout container;
    private Button btnCreate;
    private Button btnSplit;
    private Button btnMerge;
    private Panel textContentHolder;
    private Label lblTotalInvoiced;
    private Label lblTotalNotInvoiced;
    private Label lblSOHours;
    private GridLayout gridInvoices;
    private Panel photosContentHolder;
    private HorizontalLayout photoContainer;

    public InvoiceCandidateDesign() {
        Design.read(this);
    }

    public VerticalLayout getCardHolder() {
        return cardHolder;
    }

    public Image getImgTop() {
        return imgTop;
    }

    public VerticalLayout getContainer() {
        return container;
    }

    public Button getBtnCreate() {
        return btnCreate;
    }

    public Button getBtnSplit() {
        return btnSplit;
    }

    public Button getBtnMerge() {
        return btnMerge;
    }

    public Panel getTextContentHolder() {
        return textContentHolder;
    }

    public Label getLblTotalInvoiced() {
        return lblTotalInvoiced;
    }

    public Label getLblTotalNotInvoiced() {
        return lblTotalNotInvoiced;
    }

    public Label getLblSOHours() {
        return lblSOHours;
    }

    public GridLayout getGridInvoices() {
        return gridInvoices;
    }

    public Panel getPhotosContentHolder() {
        return photosContentHolder;
    }

    public HorizontalLayout getPhotoContainer() {
        return photoContainer;
    }

}
