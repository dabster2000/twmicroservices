package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.server.FontIcon;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import dk.trustworks.invoicewebui.web.client.views.ClientManagerView;
import dk.trustworks.invoicewebui.web.dashboard.DashboardView;
import dk.trustworks.invoicewebui.web.invoice.views.MainWindowImpl;
import dk.trustworks.invoicewebui.web.project.components.ProjectManagerImpl;
import dk.trustworks.invoicewebui.web.project.views.ProjectManagerView;
import dk.trustworks.invoicewebui.web.time.TimeManagerView;
import org.vaadin.alump.materialicons.MaterialIcons;

/**
 * Created by hans on 28/08/2017.
 */

@SpringComponent
@SpringUI
public class LeftMenu extends ResponsiveRow {

    public LeftMenu() {
        this.setSizeFull();
        this.setMargin(false);
        this.setVerticalSpacing(false);
        this.setHorizontalSpacing(false);
        this.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        createItem("Dashboard", "", MaterialIcons.HOME, DashboardView.VIEW_NAME, false);
        createItem("Time", "", MaterialIcons.TIMER, TimeManagerView.VIEW_NAME, false);
        createItem("Clients", "", MaterialIcons.VERIFIED_USER, ClientManagerView.VIEW_NAME, false);
        createItem("Projects", "", MaterialIcons.REPORT, ProjectManagerView.VIEW_NAME, false);
        createItem("Invoice", "+", MaterialIcons.RECEIPT, MainWindowImpl.VIEW_NAME, false);
        createItem("New", "", null, MainWindowImpl.VIEW_NAME, true);
    }

    private void createItem(String caption, String parentIndicator, FontIcon icon, String nagivateTo, boolean isChild) {
        this.addColumn()
                .withDisplayRules(12,3,12,12)
                .withVisibilityRules(false, false, true, true)
                .withComponent(new MenuItemImpl()
                        .withCaption(caption)
                        .withParentIndicator(parentIndicator)
                        .withIcon(icon)
                        .isChild(isChild)
                        .addClickListener(event -> getUI().getNavigator().navigateTo(nagivateTo)));
    }
}
