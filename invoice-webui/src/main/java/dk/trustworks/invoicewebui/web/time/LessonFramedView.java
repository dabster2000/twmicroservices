package dk.trustworks.invoicewebui.web.time;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.security.Authorizer;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.time.layouts.LessonFramedLayout;
import dk.trustworks.invoicewebui.web.time.layouts.TimeManagerLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 16/08/2017.
 */
@AccessRules(roleTypes = {RoleType.USER})
@SpringView(name = LessonFramedView.VIEW_NAME)
public class LessonFramedView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(LessonFramedView.class.getName());

    @Autowired
    private Authorizer authorizer;

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private LessonFramedLayout lessonFramedLayout;

    private String projectuuid;
    private String reason;

    public static final String VIEW_NAME = "lessonframed";
    public static final String MENU_NAME = "Lesson Framed";
    public static final String VIEW_BREADCRUMB = "Lesson Framed";
    public static final FontIcon VIEW_ICON = MaterialIcons.LIGHTBULB_OUTLINE;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(lessonFramedLayout, VIEW_ICON, MENU_NAME, "How are You doing...", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        authorizer.authorize(this, RoleType.USER);
        if(event.getParameters() != null){
            String[] params = event.getParameters().split("/");
            projectuuid = params.length>0?params[0]:"";
            if(projectuuid.equals("")) UI.getCurrent().getNavigator().navigateTo(TimeManagerViewSecond.VIEW_NAME);
            reason = params.length>1? params[1]:"";
            lessonFramedLayout.init(projectuuid, reason);
        }
    }
}
