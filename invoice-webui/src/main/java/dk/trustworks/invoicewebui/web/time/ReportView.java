package dk.trustworks.invoicewebui.web.time;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.time.components.TimeReportImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 16/08/2017.
 */
@AccessRules(roleTypes = {RoleType.USER, RoleType.EXTERNAL})
@SpringView(name = ReportView.VIEW_NAME)
public class ReportView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(ReportView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private TimeReportImpl timeReport;

    public static final String VIEW_NAME = "timereport";
    public static final String MENU_NAME = "Report";
    public static final String VIEW_BREADCRUMB = "TimeManager / Report";
    public static final FontIcon VIEW_ICON = MaterialIcons.ACCESS_TIME;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(timeReport, VIEW_ICON, MENU_NAME, "You are probably doing this late...", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {    }
}
