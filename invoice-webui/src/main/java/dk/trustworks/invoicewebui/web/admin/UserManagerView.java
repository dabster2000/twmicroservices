package dk.trustworks.invoicewebui.web.admin;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.admin.components.AdminManagerImpl;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 16/08/2017.
 */
@AccessRules(roleTypes = {RoleType.ADMIN, RoleType.CXO})
@SpringView(name = UserManagerView.VIEW_NAME)
public class UserManagerView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(UserManagerView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private AdminManagerImpl adminManager;

    public static final String VIEW_NAME = "employees";
    public static final String MENU_NAME = "Employees";
    public static final String VIEW_BREADCRUMB = "Employees";
    public static final FontIcon VIEW_ICON = MaterialIcons.SECURITY;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(adminManager, VIEW_ICON, MENU_NAME, "For your eyes only!", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {}
}
