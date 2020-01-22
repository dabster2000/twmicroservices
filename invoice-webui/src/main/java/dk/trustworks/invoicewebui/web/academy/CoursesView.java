package dk.trustworks.invoicewebui.web.academy;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.academy.layout.CoursesLayout;
import dk.trustworks.invoicewebui.web.knowledge.components.KnowledgeCanvas;
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
@SpringView(name = CoursesView.VIEW_NAME)
public class CoursesView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(CoursesView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private CoursesLayout coursesLayout;

    public static final String VIEW_NAME = "courses";
    public static final String MENU_NAME = "COURSES";
    public static final String VIEW_BREADCRUMB = "Courses";
    public static final FontIcon VIEW_ICON = MaterialIcons.QUESTION_ANSWER;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(coursesLayout.init(), VIEW_ICON, MENU_NAME, "Get smarter", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
