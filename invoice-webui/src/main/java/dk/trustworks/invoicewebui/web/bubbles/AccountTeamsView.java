package dk.trustworks.invoicewebui.web.bubbles;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.BubbleType;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
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
@AccessRules(roleTypes = {RoleType.USER})
@SpringView(name = AccountTeamsView.VIEW_NAME)
public class AccountTeamsView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(AccountTeamsView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private BubblesLayout bubblesLayout;

    public static final String VIEW_NAME = "accountteams";
    public static final String MENU_NAME = "Customer Engagement";
    public static final String VIEW_BREADCRUMB = "Customer Engagement";
    public static final FontIcon VIEW_ICON = MaterialIcons.PEOPLE;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(bubblesLayout.init(BubbleType.FOCUS, BubbleType.ACCOUNT_TEAM), VIEW_ICON, MENU_NAME, "This is how we engage our customers!", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {}
}
