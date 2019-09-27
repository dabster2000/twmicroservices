package dk.trustworks.invoicewebui.web.knowledge.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
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
public class ArchitectureCell extends VerticalLayout {
    private VerticalLayout cardHolder;
    private HorizontalLayout hlTitleBar;
    private Label lblTitle;
    private Button btnAlt1;
    private ComboBox<dk.trustworks.invoicewebui.web.model.FileItem> cbFileSelector;
    private Image imgTop;
    private VerticalLayout content;
    private HorizontalLayout vlConsultants;
    private Button btnDownloadFile;
    private Image imgCustomer;
    private Label lblAreaTitle;

    public ArchitectureCell() {
        Design.read(this);
    }

    public VerticalLayout getCardHolder() {
        return cardHolder;
    }

    public HorizontalLayout getHlTitleBar() {
        return hlTitleBar;
    }

    public Label getLblTitle() {
        return lblTitle;
    }

    public Button getBtnAlt1() {
        return btnAlt1;
    }

    public ComboBox<dk.trustworks.invoicewebui.web.model.FileItem> getCbFileSelector() {
        return cbFileSelector;
    }

    public Image getImgTop() {
        return imgTop;
    }

    public VerticalLayout getContent() {
        return content;
    }

    public HorizontalLayout getVlConsultants() {
        return vlConsultants;
    }

    public Button getBtnDownloadFile() {
        return btnDownloadFile;
    }

    public Image getImgCustomer() {
        return imgCustomer;
    }

    public Label getLblAreaTitle() {
        return lblAreaTitle;
    }

}