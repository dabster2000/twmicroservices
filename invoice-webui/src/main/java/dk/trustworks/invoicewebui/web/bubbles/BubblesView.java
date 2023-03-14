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
@SpringView(name = BubblesView.VIEW_NAME)
public class BubblesView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(BubblesView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private BubblesLayout bubblesLayout;

    public static final String VIEW_NAME = "bubbles";
    public static final String MENU_NAME = "Bubbles";
    public static final String VIEW_BREADCRUMB = "Bubbles";
    public static final FontIcon VIEW_ICON = MaterialIcons.BUBBLE_CHART;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(bubblesLayout.init(BubbleType.KNOWLEDGE), VIEW_ICON, MENU_NAME, "BLOW THEM BUBBLES!!!", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {}
}
