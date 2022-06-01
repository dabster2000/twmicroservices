package dk.trustworks.invoicewebui.web.stats;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.stats.components.TrustworksStatsLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

import static dk.trustworks.invoicewebui.model.enums.RoleType.ADMIN;
import static dk.trustworks.invoicewebui.model.enums.RoleType.PARTNER;

/**
 * Created by hans on 16/08/2017.
 */
@AccessRules(roleTypes = {ADMIN, PARTNER})
@SpringView(name = TrustworksStatsView.VIEW_NAME)
public class TrustworksStatsView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(TrustworksStatsView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private TrustworksStatsLayout trustworksStatsLayout;

    public static final String VIEW_NAME = "trustworks_stats";
    public static final String MENU_NAME = "Trustworks Stats";
    public static final String VIEW_BREADCRUMB = "Trustworks Statistics";
    public static final FontIcon VIEW_ICON = MaterialIcons.GRADE;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(trustworksStatsLayout.init(), VIEW_ICON, MENU_NAME, "Great statistics!", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {}
}
