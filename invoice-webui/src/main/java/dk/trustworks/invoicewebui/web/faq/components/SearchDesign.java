package dk.trustworks.invoicewebui.web.faq.components;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.TextField;
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
public class SearchDesign extends VerticalLayout {
    private TextField txtSearchField;
    private Accordion accFaq;

    public SearchDesign() {
        Design.read(this);
    }

    public TextField getTxtSearchField() {
        return txtSearchField;
    }

    public Accordion getAccFaq() {
        return accFaq;
    }

}
