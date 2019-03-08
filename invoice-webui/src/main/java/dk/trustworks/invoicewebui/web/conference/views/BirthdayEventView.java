package dk.trustworks.invoicewebui.web.conference.views;

import com.vaadin.navigator.View;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.conference.layout.BirthdayEventLayout;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 19/12/2016.
 */

@SpringView(name = BirthdayEventView.VIEW_NAME)
public class BirthdayEventView extends VerticalLayout implements View {

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private BirthdayEventLayout layout;

    public static final String VIEW_NAME = "birthdayevent";
    public static final String MENU_NAME = "Birthday";
    public static final String VIEW_BREADCRUMB = "Trustworks 5 Years";
    public static final FontIcon VIEW_ICON = MaterialIcons.TIMELINE;


    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        //this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        mainTemplate.setMainContent(layout.init(), VIEW_ICON, MENU_NAME, "Trustworks 5 Years", VIEW_BREADCRUMB);
        mainTemplate.hideLeftMenu();
    }
}
