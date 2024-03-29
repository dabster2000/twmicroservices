package dk.trustworks.invoicewebui.web.invoice.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.invoice.components.NewInvoiceImpl2;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 11/07/2017.
 */
@AccessRules(roleTypes = {RoleType.ACCOUNTING})
@SpringView(name = NewInvoiceView.VIEW_NAME)
public class NewInvoiceView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "invoice_new";
    public static final String MENU_NAME = "Invoices";
    public static final String VIEW_BREADCRUMB = "Economy / Invoices";
    public static final FontIcon VIEW_ICON = MaterialIcons.RECEIPT;

    protected static Logger logger = LoggerFactory.getLogger(NewInvoiceView.class.getName());

    @Autowired
    private NewInvoiceImpl2 newInvoiceImpl2;

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        mainTemplate.setMainContent(newInvoiceImpl2.init(), VIEW_ICON, MENU_NAME, "Primed for billing", VIEW_BREADCRUMB);
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        System.out.println("NewInvoiceView.enter");
        newInvoiceImpl2.reloadData();
    }
}
