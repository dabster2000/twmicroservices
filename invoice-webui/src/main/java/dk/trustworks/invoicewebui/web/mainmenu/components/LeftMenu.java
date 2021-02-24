package dk.trustworks.invoicewebui.web.mainmenu.components;

import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.security.Authorizer;
import dk.trustworks.invoicewebui.web.academy.BasicSkillsView;
import dk.trustworks.invoicewebui.web.academy.CoursesView;
import dk.trustworks.invoicewebui.web.admin.UserManagerView;
import dk.trustworks.invoicewebui.web.bubbles.BubblesView;
import dk.trustworks.invoicewebui.web.client.views.ClientManagerView;
import dk.trustworks.invoicewebui.web.contracts.views.ContractManagerView;
import dk.trustworks.invoicewebui.web.dashboard.DashboardView;
import dk.trustworks.invoicewebui.web.economy.ExpenseView;
import dk.trustworks.invoicewebui.web.employee.EmployeeView;
import dk.trustworks.invoicewebui.web.faq.FaqView;
import dk.trustworks.invoicewebui.web.invoice.views.InvoiceListView;
import dk.trustworks.invoicewebui.web.invoice.views.NewInvoiceView;
import dk.trustworks.invoicewebui.web.knowledge.BusinessArchitectureView;
import dk.trustworks.invoicewebui.web.knowledge.CkoAdminView;
import dk.trustworks.invoicewebui.web.knowledge.ConferencesView;
import dk.trustworks.invoicewebui.web.knowledge.KnowledgeView;
import dk.trustworks.invoicewebui.web.profile.ProfileView;
import dk.trustworks.invoicewebui.web.project.views.ProjectManagerView;
import dk.trustworks.invoicewebui.web.projectdescriptions.ProjectDescriptionView;
import dk.trustworks.invoicewebui.web.resourceplanning.ResourcePlanningView;
import dk.trustworks.invoicewebui.web.stats.TrustworksStatsView;
import dk.trustworks.invoicewebui.web.time.ReportView;
import dk.trustworks.invoicewebui.web.time.TimeManagerViewSecond;
import dk.trustworks.invoicewebui.web.trips.SalesVideoView;
import dk.trustworks.invoicewebui.web.trips.TripsView;
import dk.trustworks.invoicewebui.web.vtv.SalesView;
import dk.trustworks.invoicewebui.web.vtv.TenderManagementView;
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
        MenuItemContainer time = new MenuItemContainer(order++).createItem(TimeManagerViewSecond.MENU_NAME, false, TimeManagerViewSecond.VIEW_ICON, TimeManagerViewSecond.VIEW_NAME, false, RoleType.USER, RoleType.EXTERNAL);
        menuItems.put(TimeManagerViewSecond.VIEW_NAME, time);
        MenuItemContainer reportView = new MenuItemContainer(order++).createItem(ReportView.MENU_NAME, false, ReportView.VIEW_ICON, ReportView.VIEW_NAME, false, RoleType.ADMIN, RoleType.PARTNER, RoleType.USER, RoleType.EXTERNAL);
        menuItems.put(ReportView.VIEW_NAME, reportView);

        MenuItemContainer knowledge = new MenuItemContainer(order++).createItem("Knowledge ---", true, null, null, false, RoleType.USER);
        menuItems.put("knowledge", knowledge);
        MenuItemContainer businessArchitecture = new MenuItemContainer(order++).createItem(BusinessArchitectureView.MENU_NAME, false, BusinessArchitectureView.VIEW_ICON, BusinessArchitectureView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(BusinessArchitectureView.VIEW_NAME, businessArchitecture);
        MenuItemContainer bubbles = new MenuItemContainer(order++).createItem(BubblesView.MENU_NAME, false, BubblesView.VIEW_ICON, BubblesView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(BubblesView.VIEW_NAME, bubbles);
        MenuItemContainer conferences = new MenuItemContainer(order++).createItem(ConferencesView.MENU_NAME, false, ConferencesView.VIEW_ICON, ConferencesView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(ConferencesView.VIEW_NAME, conferences);
        MenuItemContainer keynotes = new MenuItemContainer(order++).createItem(KnowledgeView.MENU_NAME, false, KnowledgeView.VIEW_ICON, KnowledgeView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(KnowledgeView.VIEW_NAME, keynotes);
        MenuItemContainer projectdescriptions = new MenuItemContainer(order++).createItem(ProjectDescriptionView.MENU_NAME, false, ProjectDescriptionView.VIEW_ICON, ProjectDescriptionView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(ProjectDescriptionView.VIEW_NAME, projectdescriptions);
        MenuItemContainer ckoadmin = new MenuItemContainer(order++).createItem(CkoAdminView.MENU_NAME, false, CkoAdminView.VIEW_ICON, CkoAdminView.VIEW_NAME, false, RoleType.MANAGER);
        menuItems.put(CkoAdminView.VIEW_NAME, ckoadmin);

        MenuItemContainer academy = new MenuItemContainer(order++).createItem("Trustworks Academy ---", true, null, null, false, RoleType.USER);
        menuItems.put("academy", academy);
        MenuItemContainer basicskills = new MenuItemContainer(order++).createItem(BasicSkillsView.MENU_NAME, false, BasicSkillsView.VIEW_ICON, BasicSkillsView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(BasicSkillsView.VIEW_NAME, basicskills);
        MenuItemContainer courses = new MenuItemContainer(order++).createItem(CoursesView.MENU_NAME, false, CoursesView.VIEW_ICON, CoursesView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(CoursesView.VIEW_NAME, courses);


        MenuItemContainer invoice = new MenuItemContainer(order++).createItem("Economy ---", true, null, null, false, RoleType.ACCOUNTING);
        menuItems.put("Economy", invoice);
        MenuItemContainer newInvoice = new MenuItemContainer(order++).createItem(NewInvoiceView.MENU_NAME, false, NewInvoiceView.VIEW_ICON, NewInvoiceView.VIEW_NAME, false, RoleType.ACCOUNTING);
        menuItems.put(NewInvoiceView.VIEW_NAME, newInvoice);

        MenuItemContainer questions = new MenuItemContainer(order++).createItem("Questions ---", true, null, null, false, RoleType.USER);
        menuItems.put("Questions", questions);
        MenuItemContainer faq = new MenuItemContainer(order++).createItem(FaqView.MENU_NAME, false, FaqView.VIEW_ICON, FaqView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(FaqView.VIEW_NAME, faq);
        MenuItemContainer travel = new MenuItemContainer(order++).createItem(TripsView.MENU_NAME, false, TripsView.VIEW_ICON, TripsView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(TripsView.VIEW_NAME, travel);

        MenuItemContainer coOps = new MenuItemContainer(order++).createItem("Sales ---", true, null, null, false, RoleType.USER);
        menuItems.put("Sales", coOps);
        MenuItemContainer sales = new MenuItemContainer(order++).createItem(SalesView.MENU_NAME, false, SalesView.VIEW_ICON, SalesView.VIEW_NAME, false, RoleType.VTV);
        menuItems.put(SalesView.VIEW_NAME, sales);
        MenuItemContainer tendermanagement = new MenuItemContainer(order++).createItem(TenderManagementView.MENU_NAME, false, TenderManagementView.VIEW_ICON, TenderManagementView.VIEW_NAME, false, RoleType.VTV);
        menuItems.put(TenderManagementView.VIEW_NAME, tendermanagement);
        MenuItemContainer warStories = new MenuItemContainer(order++).createItem(SalesVideoView.MENU_NAME, false, SalesVideoView.VIEW_ICON, SalesVideoView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(SalesVideoView.VIEW_NAME, warStories);


        MenuItemContainer employeeinfo = new MenuItemContainer(order++).createItem("Consultant ---", true, null, null, false, RoleType.USER);
        menuItems.put("employeeinfo", employeeinfo);
        MenuItemContainer employee = new MenuItemContainer(order++).createItem(EmployeeView.MENU_NAME, false, EmployeeView.VIEW_ICON, EmployeeView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(EmployeeView.VIEW_NAME, employee);
        MenuItemContainer profile = new MenuItemContainer(order++).createItem(ProfileView.MENU_NAME, false, ProfileView.VIEW_ICON, ProfileView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(ProfileView.VIEW_NAME, profile);
        MenuItemContainer availabilityplanning = new MenuItemContainer(order++).createItem(ResourcePlanningView.MENU_NAME, false, ResourcePlanningView.VIEW_ICON, ResourcePlanningView.VIEW_NAME, false, RoleType.USER);
        menuItems.put(ResourcePlanningView.VIEW_NAME, availabilityplanning);


        MenuItemContainer admin = new MenuItemContainer(order++).createItem("Management ---", true, null, null, false, RoleType.ADMIN, RoleType.EDITOR, RoleType.PARTNER);
        menuItems.put("admin", admin);
        MenuItemContainer employeeManager = new MenuItemContainer(order++).createItem(UserManagerView.MENU_NAME, false, UserManagerView.VIEW_ICON, UserManagerView.VIEW_NAME, false, RoleType.ADMIN, RoleType.PARTNER, RoleType.CXO);
        menuItems.put(UserManagerView.VIEW_NAME, employeeManager);
        MenuItemContainer trustworksStatistics = new MenuItemContainer(order++).createItem(TrustworksStatsView.MENU_NAME, false, TrustworksStatsView.VIEW_ICON, TrustworksStatsView.VIEW_NAME, false, RoleType.ADMIN);
        menuItems.put(TrustworksStatsView.VIEW_NAME, trustworksStatistics);
        MenuItemContainer expenses = new MenuItemContainer(order++).createItem(ExpenseView.MENU_NAME, false, ExpenseView.VIEW_ICON, ExpenseView.VIEW_NAME, false, RoleType.ADMIN);
        menuItems.put(ExpenseView.VIEW_NAME, expenses);
    }

    public Map<String, MenuItemContainer> getMenuItems() {
        return menuItems;
    }


}
