package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.Authorizer;
import dk.trustworks.invoicewebui.web.admin.UserManagerView;
import dk.trustworks.invoicewebui.web.bubbles.BubblesView;
import dk.trustworks.invoicewebui.web.client.views.ClientManagerView;
import dk.trustworks.invoicewebui.web.contracts.views.ContractManagerView;
import dk.trustworks.invoicewebui.web.dashboard.DashboardView;
import dk.trustworks.invoicewebui.web.economy.ExpenseView;
import dk.trustworks.invoicewebui.web.employee.EmployeeView;
import dk.trustworks.invoicewebui.web.faq.FaqView;
import dk.trustworks.invoicewebui.web.invoice.views.DraftListView;
import dk.trustworks.invoicewebui.web.invoice.views.InvoiceListView;
import dk.trustworks.invoicewebui.web.invoice.views.NewInvoiceView;
import dk.trustworks.invoicewebui.web.knowledge.KnowledgeView;
import dk.trustworks.invoicewebui.web.project.views.ProjectManagerView;
import dk.trustworks.invoicewebui.web.projectdescriptions.ProjectDescriptionView;
import dk.trustworks.invoicewebui.web.resourceplanning.ResourcePlanningView;
import dk.trustworks.invoicewebui.web.stats.StatsManagerView;
import dk.trustworks.invoicewebui.web.stats.TrustworksStatsView;
import dk.trustworks.invoicewebui.web.time.ReportView;
import dk.trustworks.invoicewebui.web.time.TimeManagerViewSecond;
import dk.trustworks.invoicewebui.web.trips.TripsView;
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
            if(authorizer.hasAccess(menuItemContainer.getRoles())) addColumn(menuItemContainer.getMenuItemColumn());
            for (MenuItemContainer itemContainer : menuItemContainer.getChildItems()) {
                if(authorizer.hasAccess(itemContainer.getRoles())) addColumn(itemContainer.getMenuItemColumn());
            }
        }
    }

    private void createMenuItems() {
        int order = 1;
        MenuItemContainer mainNavigation = new MenuItemContainer(order++).createItem("Main Navigation ---", true, null, null, false);
        menuItems.put("MainNavigation", mainNavigation);
        MenuItemContainer dashboard = new MenuItemContainer(order++).createItem(DashboardView.MENU_NAME, false, DashboardView.VIEW_ICON, DashboardView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(DashboardView.VIEW_NAME, dashboard);
        MenuItemContainer clients = new MenuItemContainer(order++).createItem(ClientManagerView.MENU_NAME, false, ClientManagerView.VIEW_ICON, ClientManagerView.VIEW_NAME, false, RoleType.SALES);
        menuItems.put(ClientManagerView.VIEW_NAME, clients);
        MenuItemContainer contracts = new MenuItemContainer(order++).createItem(ContractManagerView.MENU_NAME, false, ContractManagerView.VIEW_ICON, ContractManagerView.VIEW_NAME, false, RoleType.SALES);
        menuItems.put(ContractManagerView.VIEW_NAME, contracts);
        MenuItemContainer projects = new MenuItemContainer(order++).createItem(ProjectManagerView.MENU_NAME, false, ProjectManagerView.VIEW_ICON, ProjectManagerView.VIEW_NAME, false, RoleType.SALES);
        menuItems.put(ProjectManagerView.VIEW_NAME, projects);

        MenuItemContainer timemanager = new MenuItemContainer(order++).createItem("TimeManager ---", true, null, null, false, RoleType.USER, RoleType.EXTERNAL);
        menuItems.put("timemanager", timemanager);
        //MenuItemContainer time = new MenuItemContainer(order++).createItem(TimeManagerView.MENU_NAME, false, TimeManagerView.VIEW_ICON, TimeManagerView.VIEW_NAME, false, RoleType.USER, RoleType.EXTERNAL);
        //menuItems.put(TimeManagerView.VIEW_NAME, time);
        MenuItemContainer time = new MenuItemContainer(order++).createItem(TimeManagerViewSecond.MENU_NAME, false, TimeManagerViewSecond.VIEW_ICON, TimeManagerViewSecond.VIEW_NAME, false, RoleType.USER, RoleType.EXTERNAL);
        menuItems.put(TimeManagerViewSecond.VIEW_NAME, time);
        MenuItemContainer reportView = new MenuItemContainer(order++).createItem(ReportView.MENU_NAME, false, ReportView.VIEW_ICON, ReportView.VIEW_NAME, false, RoleType.ADMIN, RoleType.PARTNER, RoleType.USER, RoleType.EXTERNAL);
        menuItems.put(ReportView.VIEW_NAME, reportView);

        MenuItemContainer knowledge = new MenuItemContainer(order++).createItem("Knowledge ---", true, null, null, false, RoleType.USER);
        menuItems.put("knowledge", knowledge);
        MenuItemContainer keynotes = new MenuItemContainer(order++).createItem(KnowledgeView.MENU_NAME, false, KnowledgeView.VIEW_ICON, KnowledgeView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(KnowledgeView.VIEW_NAME, keynotes);
        MenuItemContainer bubbles = new MenuItemContainer(order++).createItem(BubblesView.MENU_NAME, false, BubblesView.VIEW_ICON, BubblesView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(BubblesView.VIEW_NAME, bubbles);
        MenuItemContainer projectdescriptions = new MenuItemContainer(order++).createItem(ProjectDescriptionView.MENU_NAME, false, ProjectDescriptionView.VIEW_ICON, ProjectDescriptionView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(ProjectDescriptionView.VIEW_NAME, projectdescriptions);

        MenuItemContainer invoice = new MenuItemContainer(order++).createItem("Invoice ---", true, null, null, false, RoleType.ACCOUNTING);
        menuItems.put("Invoice", invoice);
        MenuItemContainer newInvoice = new MenuItemContainer(order++).createItem(NewInvoiceView.MENU_NAME, false, NewInvoiceView.VIEW_ICON, NewInvoiceView.VIEW_NAME, false, RoleType.ACCOUNTING);
        menuItems.put(NewInvoiceView.VIEW_NAME, newInvoice);
        MenuItemContainer drafts = new MenuItemContainer(order++).createItem(DraftListView.MENU_NAME, false, DraftListView.VIEW_ICON, DraftListView.VIEW_NAME, false, RoleType.ACCOUNTING);
        menuItems.put(DraftListView.VIEW_NAME, drafts);
        MenuItemContainer invoices = new MenuItemContainer(order++).createItem(InvoiceListView.MENU_NAME, false, InvoiceListView.VIEW_ICON, InvoiceListView.VIEW_NAME, false, RoleType.ACCOUNTING);
        menuItems.put(InvoiceListView.VIEW_NAME, invoices);

        MenuItemContainer sales = new MenuItemContainer(order++).createItem("Sales ---", true, null, null, false, RoleType.USER);
        menuItems.put("Sales", sales);
        MenuItemContainer availabilityplanning = new MenuItemContainer(order++).createItem(ResourcePlanningView.MENU_NAME, false, ResourcePlanningView.VIEW_ICON, ResourcePlanningView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(ResourcePlanningView.VIEW_NAME, availabilityplanning);

        MenuItemContainer economy = new MenuItemContainer(order++).createItem("Economy ---", true, null, null, false, RoleType.ADMIN);
        menuItems.put("Economy", economy);
        MenuItemContainer expenses = new MenuItemContainer(order++).createItem(ExpenseView.MENU_NAME, false, ExpenseView.VIEW_ICON, ExpenseView.VIEW_NAME, false, RoleType.ADMIN);
        menuItems.put(ExpenseView.VIEW_NAME, expenses);

        MenuItemContainer questions = new MenuItemContainer(order++).createItem("Questions ---", true, null, null, false, RoleType.USER);
        menuItems.put("Questions", questions);
        MenuItemContainer faq = new MenuItemContainer(order++).createItem(FaqView.MENU_NAME, false, FaqView.VIEW_ICON, FaqView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(FaqView.VIEW_NAME, faq);
        MenuItemContainer travel = new MenuItemContainer(order++).createItem(TripsView.MENU_NAME, false, TripsView.VIEW_ICON, TripsView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(TripsView.VIEW_NAME, travel);

        MenuItemContainer stats = new MenuItemContainer(order++).createItem("Statistics ---", true, null, null, false, RoleType.ADMIN);
        menuItems.put("Stats", stats);
        MenuItemContainer projectStatistics = new MenuItemContainer(order++).createItem(StatsManagerView.MENU_NAME, false, StatsManagerView.VIEW_ICON, StatsManagerView.VIEW_NAME, false, RoleType.ADMIN);
        menuItems.put(StatsManagerView.VIEW_NAME, projectStatistics);
        MenuItemContainer trustworksStatistics = new MenuItemContainer(order++).createItem(TrustworksStatsView.MENU_NAME, false, TrustworksStatsView.VIEW_ICON, TrustworksStatsView.VIEW_NAME, false, RoleType.ADMIN);
        menuItems.put(TrustworksStatsView.VIEW_NAME, trustworksStatistics);

        MenuItemContainer employeeinfo = new MenuItemContainer(order++).createItem("Consultant ---", true, null, null, false, RoleType.USER);
        menuItems.put("employeeinfo", employeeinfo);
        MenuItemContainer employee = new MenuItemContainer(order++).createItem(EmployeeView.MENU_NAME, false, EmployeeView.VIEW_ICON, EmployeeView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(EmployeeView.VIEW_NAME, employee);

        MenuItemContainer admin = new MenuItemContainer(order++).createItem("Administration ---", true, null, null, false, RoleType.ADMIN, RoleType.EDITOR, RoleType.PARTNER);
        menuItems.put("admin", admin);
        MenuItemContainer employeeManager = new MenuItemContainer(order++).createItem(UserManagerView.MENU_NAME, false, UserManagerView.VIEW_ICON, UserManagerView.VIEW_NAME, false, RoleType.ADMIN, RoleType.PARTNER, RoleType.CXO);
        menuItems.put(UserManagerView.VIEW_NAME, employeeManager);

        /*
        MenuItemContainer fun = new MenuItemContainer(order++).createItem("Fun ---", true, null, null, false, RoleType.USER);
        menuItems.put("fun", fun);
        MenuItemContainer blockchain = new MenuItemContainer(order++).createItem(BlockchainView.MENU_NAME, false, BlockchainView.VIEW_ICON, BlockchainView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(BlockchainView.VIEW_NAME, blockchain);
        */
    }

    public Map<String, MenuItemContainer> getMenuItems() {
        return menuItems;
    }


}
