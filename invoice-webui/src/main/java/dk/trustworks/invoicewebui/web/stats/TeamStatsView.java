package dk.trustworks.invoicewebui.web.stats;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.stats.components.TeamStatsLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

import static dk.trustworks.invoicewebui.model.enums.RoleType.*;

/**
 * Created by hans on 16/08/2017.
 */
@AccessRules(roleTypes = {TEAMLEAD, ADMIN, PARTNER})
@SpringView(name = TeamStatsView.VIEW_NAME)
public class TeamStatsView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(TeamStatsView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private TeamStatsLayout teamStatsLayout;

    public static final String VIEW_NAME = "team_management";
    public static final String MENU_NAME = "Team Management";
    public static final String VIEW_BREADCRUMB = "Team Management";
    public static final FontIcon VIEW_ICON = MaterialIcons.GRADE;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(teamStatsLayout.init(), VIEW_ICON, MENU_NAME, "We don't manage, we define structures and processes", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {}
}
