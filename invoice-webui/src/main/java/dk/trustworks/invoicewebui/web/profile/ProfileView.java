package dk.trustworks.invoicewebui.web.profile;

import com.vaadin.navigator.View;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.profile.components.ProfileCanvas;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 19/12/2016.
 */

@SpringView(name = ProfileView.VIEW_NAME)
public class ProfileView extends VerticalLayout implements View {

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    public static final String VIEW_NAME = "profile";
    public static final String MENU_NAME = "Profile";
    public static final String VIEW_BREADCRUMB = "User Profile";
    public static final FontIcon VIEW_ICON = MaterialIcons.VERIFIED_USER;

    @Autowired
    private ProfileCanvas profileTemplate;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        mainTemplate.setMainContent(profileTemplate.init(), VIEW_ICON, MENU_NAME, "It's All About You", VIEW_BREADCRUMB);

    }
}
