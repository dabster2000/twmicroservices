package dk.trustworks.invoicewebui.web.admin.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
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
public class DocumentList extends VerticalLayout {
    private VerticalLayout cardHolder;
    private Grid<dk.trustworks.invoicewebui.web.employee.model.DocumentWithOwner> gridFiles;
    private Button btnDelete;
    private Button btnDownload;

    public DocumentList() {
        Design.read(this);
    }

    public VerticalLayout getCardHolder() {
        return cardHolder;
    }

    public Grid<dk.trustworks.invoicewebui.web.employee.model.DocumentWithOwner> getGridFiles() {
        return gridFiles;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }

    public Button getBtnDownload() {
        return btnDownload;
    }

}
