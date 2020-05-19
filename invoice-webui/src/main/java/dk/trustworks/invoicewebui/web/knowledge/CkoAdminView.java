package dk.trustworks.invoicewebui.web.knowledge;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.knowledge.layout.CkoAdministrationLayout;
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
@AccessRules(roleTypes = {RoleType.MANAGER})
@SpringView(name = CkoAdminView.VIEW_NAME)
public class CkoAdminView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(CkoAdminView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private CkoAdministrationLayout ckoAdministrationLayout;

    public static final String VIEW_NAME = "ckoadminview";
    public static final String MENU_NAME = "ADMINISTRATION";
    public static final String VIEW_BREADCRUMB = "Knowledge Manager Administration";
    public static final FontIcon VIEW_ICON = MaterialIcons.INFO;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(ckoAdministrationLayout.init(), VIEW_ICON, MENU_NAME, "Knowledge is Power", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
