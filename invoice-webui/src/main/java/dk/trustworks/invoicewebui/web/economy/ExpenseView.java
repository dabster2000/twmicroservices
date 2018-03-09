package dk.trustworks.invoicewebui.web.economy;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.economy.components.ExpenseLayout;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

@AccessRules(roleTypes = {RoleType.ADMIN})
@SpringView(name = ExpenseView.VIEW_NAME)
public class ExpenseView extends VerticalLayout implements View {


    protected static Logger logger = LoggerFactory.getLogger(ExpenseView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private ExpenseLayout expenseLayout;

    public static final String VIEW_NAME = "expenses";
    public static final String MENU_NAME = "Expenses";
    public static final String VIEW_BREADCRUMB = "Expense Manager";
    public static final FontIcon VIEW_ICON = MaterialIcons.ATTACH_MONEY;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(expenseLayout.init(), VIEW_ICON, MENU_NAME, "Alotta expenses!", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {}
}
