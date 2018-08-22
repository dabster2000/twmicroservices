package dk.trustworks.invoicewebui.web.employee;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.employee.components.EmployeeLayout;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

@AccessRules(roleTypes = {RoleType.USER})
@SpringView(name = EmployeeView.VIEW_NAME)
public class EmployeeView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(EmployeeView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private EmployeeLayout employeeLayout;

    public static final String VIEW_NAME = "employee";
    public static final String MENU_NAME = "Info";
    public static final String VIEW_BREADCRUMB = "Consultant Info";
    public static final FontIcon VIEW_ICON = MaterialIcons.PERSON;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(employeeLayout.init(), VIEW_ICON, MENU_NAME, "It's all about you!", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {}
}
