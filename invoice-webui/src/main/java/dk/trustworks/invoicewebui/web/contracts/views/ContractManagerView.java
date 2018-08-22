package dk.trustworks.invoicewebui.web.contracts.views;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.web.contracts.layouts.ContractListLayout;
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
@AccessRules(roleTypes = {RoleType.SALES})
@SpringView(name = ContractManagerView.VIEW_NAME)
public class ContractManagerView extends VerticalLayout implements View {

    protected static Logger logger = LoggerFactory.getLogger(ContractManagerView.class.getName());

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private ContractListLayout contractListLayout;

    public static final String VIEW_NAME = "contract";
    public static final String MENU_NAME = "Contracts";
    public static final String VIEW_BREADCRUMB = "Contracts";
    public static final FontIcon VIEW_ICON = MaterialIcons.PAGES;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        mainTemplate.setMainContent(contractListLayout, VIEW_ICON, MENU_NAME, "Complete list of contracts", VIEW_BREADCRUMB);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
