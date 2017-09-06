package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import dk.trustworks.invoicewebui.web.client.views.ClientManagerView;
import dk.trustworks.invoicewebui.web.dashboard.DashboardView;
import dk.trustworks.invoicewebui.web.invoice.views.DraftListView;
import dk.trustworks.invoicewebui.web.invoice.views.InvoiceListView;
import dk.trustworks.invoicewebui.web.invoice.views.NewInvoiceView;
import dk.trustworks.invoicewebui.web.project.views.ProjectManagerView;
import dk.trustworks.invoicewebui.web.time.TimeManagerView;
import org.vaadin.alump.materialicons.MaterialIcons;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;

/**
 * Created by hans on 28/08/2017.
 */

@SpringComponent
@SpringUI
public class LeftMenu extends ResponsiveRow {

    private final Map<String, MenuItemContainer> menuItems;

    public LeftMenu() {
        menuItems = new HashMap<>();
        this.setSizeFull();
        this.setMargin(false);
        this.setVerticalSpacing(false);
        this.setHorizontalSpacing(false);
        this.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        createMenuItems();

        List<MenuItemContainer> menuItemContainerList = newArrayList(menuItems.values());
        menuItemContainerList.sort(Comparator.comparingInt(MenuItemContainer::getOrder));
        for (MenuItemContainer menuItemContainer : menuItemContainerList) {
            if(menuItemContainer.isChild()) continue;
            addColumn(menuItemContainer.getMenuItem());
            for (MenuItemContainer itemContainer : menuItemContainer.getChildItems()) {
                addColumn(itemContainer.getMenuItem());
            }
        }
    }

    private void createMenuItems() {
        MenuItemContainer dashboard = new MenuItemContainer(1).createItem("Dashboard", null, MaterialIcons.DASHBOARD, DashboardView.VIEW_NAME, false);
        menuItems.put(DashboardView.VIEW_NAME, dashboard);
        MenuItemContainer time = new MenuItemContainer(2).createItem("Time", null, MaterialIcons.ACCESS_TIME, TimeManagerView.VIEW_NAME, false);
        menuItems.put(TimeManagerView.VIEW_NAME, time);
        MenuItemContainer clients = new MenuItemContainer(3).createItem("Clients", null, MaterialIcons.CONTACT_MAIL, ClientManagerView.VIEW_NAME, false);
        menuItems.put(ClientManagerView.VIEW_NAME, clients);
        MenuItemContainer projects = new MenuItemContainer(4).createItem("Projects", null, MaterialIcons.DATE_RANGE, ProjectManagerView.VIEW_NAME, false);
        menuItems.put(ProjectManagerView.VIEW_NAME, projects);
        MenuItemContainer invoice = new MenuItemContainer(5).createItem("Invoice", MenuItemImpl.PLUS_INDICATOR, MaterialIcons.RECEIPT, NewInvoiceView.VIEW_NAME, false);
        menuItems.put(NewInvoiceView.VIEW_NAME, invoice);
        MenuItemContainer newInvoice = new MenuItemContainer(6).createItem("New invoice", null, null, NewInvoiceView.VIEW_NAME, true).setParentMenuItem(invoice);
        //menuItems.put(NewInvoiceView.VIEW_NAME, invoice);
        MenuItemContainer drafts = new MenuItemContainer(6).createItem(DraftListView.MENU_NAME, null, null, DraftListView.VIEW_NAME, true).setParentMenuItem(invoice);
        menuItems.put(DraftListView.VIEW_NAME, drafts);
        MenuItemContainer invoices = new MenuItemContainer(6).createItem("Invoices", null, null, InvoiceListView.VIEW_NAME, true).setParentMenuItem(invoice);
        menuItems.put(InvoiceListView.VIEW_NAME, invoices);
    }

    public Map<String, MenuItemContainer> getMenuItems() {
        return menuItems;
    }
}
