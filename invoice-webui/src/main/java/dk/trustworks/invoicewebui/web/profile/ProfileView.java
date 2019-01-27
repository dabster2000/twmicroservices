package dk.trustworks.invoicewebui.web.profile;

import com.vaadin.navigator.View;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.profile.layout.ProfileLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 19/12/2016.
 */

@AccessRules(roleTypes = {RoleType.USER})
@SpringView(name = ProfileView.VIEW_NAME)
public class ProfileView extends VerticalLayout implements View {

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    public static final String VIEW_NAME = "profiles";
    public static final String MENU_NAME = "Profiles";
    public static final String VIEW_BREADCRUMB = "User Profiles";
    public static final FontIcon VIEW_ICON = MaterialIcons.VERIFIED_USER;

    @Autowired
    private ProfileLayout profileTemplate;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);

        mainTemplate.setMainContent(profileTemplate.init(), VIEW_ICON, MENU_NAME, "It's All About You", VIEW_BREADCRUMB);

    }
}
