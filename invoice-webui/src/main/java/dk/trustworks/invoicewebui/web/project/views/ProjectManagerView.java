package dk.trustworks.invoicewebui.web.project.views;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.services.InvoiceService;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.project.components.ProjectManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 16/08/2017.
 */
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

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(projectManager.init());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //Authorizer.authorize(this);
    }
}
