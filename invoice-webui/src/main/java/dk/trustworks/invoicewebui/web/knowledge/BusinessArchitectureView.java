package dk.trustworks.invoicewebui.web.knowledge;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.knowledge.layout.BusinessArchitectureLayout;
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
@SpringView(name = BusinessArchitectureView.VIEW_NAME)
public class BusinessArchitectureView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(BusinessArchitectureView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private BusinessArchitectureLayout businessArchitectureLayout;

    public static final String VIEW_NAME = "ba";
    public static final String MENU_NAME = "ARCHITECTURE";
    public static final String VIEW_BREADCRUMB = "Business Architecture";
    public static final FontIcon VIEW_ICON = MaterialIcons.YOUTUBE_SEARCHED_FOR;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(businessArchitectureLayout.init(), VIEW_ICON, MENU_NAME, "Business >= Architecture", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
