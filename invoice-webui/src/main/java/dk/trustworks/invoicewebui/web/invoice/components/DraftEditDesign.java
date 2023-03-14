package dk.trustworks.invoicewebui.web.invoice.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Label;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;

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
public class DraftEditDesign extends VerticalLayout {
    protected Button btnDelete;
    protected Button btnCreatePhantom;
    protected Button btnCreateInvoice;
    protected Button btnCreateCreditNote;
    protected Button btnDropbox;
    protected Button btnDownload;
    protected TextField txtClientname;
    protected TextField txtStreetname;
    protected TextField txtZipCity;
    protected TextField txtCvr;
    protected TextField txtEan;
    protected TextField txtAttention;
    protected Label lblInvoiceHeadline;
    protected HorizontalLayout hlInvoiceReference;
    protected Label lblInvoiceNumber;
    protected DateField dfInvoiceDate;
    protected DateField dfInvoiceDueDate;
    protected Label lblBalanceDue;
    protected GridLayout gridInvoiceItems;
    protected Label lblSumNoTax;
    protected Button btnSetSKIDiscount;
    protected TextField txtDiscount;
    protected Label lblTax;
    protected Label lblSumWithTax;
    protected Label lblContractReference;
    protected Label lblProjectReference;
    protected TextField txtSpecificDescription;
    protected Button btnCopyDescription;

    public DraftEditDesign() {
        Design.read(this);
    }
}
