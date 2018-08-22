package dk.trustworks.invoicewebui.web.project.views;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.project.components.ProjectManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 16/08/2017.
 */
@AccessRules(roleTypes = {RoleType.SALES})
@SpringView(name = ProjectManagerView.VIEW_NAME)
public class ProjectManagerView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(ProjectManagerView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private ProjectManagerImpl projectManager;

    public static final String VIEW_NAME = "project";
    public static final String MENU_NAME = "Projects";
    public static final String VIEW_BREADCRUMB = "Projects";
    public static final FontIcon VIEW_ICON = MaterialIcons.DATE_RANGE;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(projectManager.init(), VIEW_ICON, MENU_NAME, "Complete list of projects", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (event.getParameters() != null
                && !event.getParameters().isEmpty()) {
            projectManager.setCurrentProject(event.getParameters());
        }
    }
}
