package dk.trustworks.invoicewebui.web.faq;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.faq.components.FaqBoard;
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
@AccessRules(roleTypes = {RoleType.CXO})
@SpringView(name = FaqView.VIEW_NAME)
public class FaqView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(FaqView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private FaqBoard faqBoard;

    public static final String VIEW_NAME = "faq";
    public static final String MENU_NAME = "FAQ";
    public static final String VIEW_BREADCRUMB = "FAQ";
    public static final FontIcon VIEW_ICON = MaterialIcons.QUESTION_ANSWER;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(faqBoard.init(), VIEW_ICON, MENU_NAME, "Trustworks Knowledge", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
