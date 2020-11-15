package dk.trustworks.invoicewebui.web.knowledge;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.knowledge.layout.CkoAdministrationLayout;
import dk.trustworks.invoicewebui.web.knowledge.layout.ConferencesLayout;
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
@SpringView(name = ConferencesView.VIEW_NAME)
public class ConferencesView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(ConferencesView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private ConferencesLayout conferencesLayout;

    public static final String VIEW_NAME = "conferencesview";
    public static final String MENU_NAME = "Conferences";
    public static final String VIEW_BREADCRUMB = "Conferences & Courses";
    public static final FontIcon VIEW_ICON = MaterialIcons.INFO;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(conferencesLayout.init(), VIEW_ICON, MENU_NAME, "Knowledge is Power", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
