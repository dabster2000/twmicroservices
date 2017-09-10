package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.security.Authorizer;
import dk.trustworks.invoicewebui.web.admin.AdminManagerView;
import dk.trustworks.invoicewebui.web.client.views.ClientManagerView;
import dk.trustworks.invoicewebui.web.dashboard.DashboardView;
import dk.trustworks.invoicewebui.web.invoice.views.DraftListView;
import dk.trustworks.invoicewebui.web.invoice.views.InvoiceListView;
import dk.trustworks.invoicewebui.web.invoice.views.NewInvoiceView;
import dk.trustworks.invoicewebui.web.project.views.ProjectManagerView;
import dk.trustworks.invoicewebui.web.time.TimeManagerView;
import org.atmosphere.config.service.Post;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;


/**
 * Created by hans on 28/08/2017.
 */

@SpringComponent
@SpringUI
public class LeftMenu extends ResponsiveRow {

    private final Map<String, MenuItemContainer> menuItems;

    @Autowired
    private Authorizer authorizer;

    public LeftMenu() {
        menuItems = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        this.setSizeFull();
        this.setMargin(false);
        this.setVerticalSpacing(false);
        this.setHorizontalSpacing(false);
        this.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        menuItems.clear();
        this.components.clear();
        Label spacer = new Label();
        spacer.setHeight("50px");
        addColumn().withDisplayRules(12,12,12,12).withVisibilityRules(false, false, true, true).withComponent(spacer);
        createMenuItems();

        List<MenuItemContainer> menuItemContainerList = newArrayList(menuItems.values());
        menuItemContainerList.sort(Comparator.comparingInt(MenuItemContainer::getOrder));
        for (MenuItemContainer menuItemContainer : menuItemContainerList) {
            if(menuItemContainer.isChild()) continue;
            if(authorizer.hasAccess(menuItemContainer.getRoles())) addColumn(menuItemContainer.getMenuItem());
            for (MenuItemContainer itemContainer : menuItemContainer.getChildItems()) {
                if(authorizer.hasAccess(itemContainer.getRoles())) addColumn(itemContainer.getMenuItem());
            }
        }
    }

    private void createMenuItems() {
        MenuItemContainer dashboard = new MenuItemContainer(1).createItem(DashboardView.MENU_NAME, null, DashboardView.VIEW_ICON, DashboardView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(DashboardView.VIEW_NAME, dashboard);
        MenuItemContainer time = new MenuItemContainer(2).createItem(TimeManagerView.MENU_NAME, null, TimeManagerView.VIEW_ICON, TimeManagerView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(TimeManagerView.VIEW_NAME, time);
        MenuItemContainer clients = new MenuItemContainer(3).createItem(ClientManagerView.MENU_NAME, null, ClientManagerView.VIEW_ICON, ClientManagerView.VIEW_NAME, false, RoleType.SALES);
        menuItems.put(ClientManagerView.VIEW_NAME, clients);
        MenuItemContainer projects = new MenuItemContainer(4).createItem(ProjectManagerView.MENU_NAME, null, ProjectManagerView.VIEW_ICON, ProjectManagerView.VIEW_NAME, false, RoleType.SALES);
        menuItems.put(ProjectManagerView.VIEW_NAME, projects);
        MenuItemContainer invoice = new MenuItemContainer(5).createItem("Invoice", MenuItemImpl.PLUS_INDICATOR, NewInvoiceView.VIEW_ICON, null, false, RoleType.ACCOUNTING);
        menuItems.put("Invoice", invoice);
        MenuItemContainer newInvoice = new MenuItemContainer(6).createItem(NewInvoiceView.MENU_NAME, null, null, NewInvoiceView.VIEW_NAME, true, RoleType.ACCOUNTING).setParentMenuItem(invoice);
        menuItems.put(NewInvoiceView.VIEW_NAME, newInvoice);
        MenuItemContainer drafts = new MenuItemContainer(6).createItem(DraftListView.MENU_NAME, null, null, DraftListView.VIEW_NAME, true, RoleType.ACCOUNTING).setParentMenuItem(invoice);
        menuItems.put(DraftListView.VIEW_NAME, drafts);
        MenuItemContainer invoices = new MenuItemContainer(6).createItem(InvoiceListView.MENU_NAME, null, null, InvoiceListView.VIEW_NAME, true, RoleType.ACCOUNTING).setParentMenuItem(invoice);
        menuItems.put(InvoiceListView.VIEW_NAME, invoices);
        MenuItemContainer admin = new MenuItemContainer(7).createItem(AdminManagerView.MENU_NAME, null, AdminManagerView.VIEW_ICON, AdminManagerView.VIEW_NAME, false, RoleType.ADMIN, RoleType.EDITOR, RoleType.PARTNER);
        menuItems.put(AdminManagerView.VIEW_NAME, admin);

    }

    public Map<String, MenuItemContainer> getMenuItems() {
        return menuItems;
    }


}
