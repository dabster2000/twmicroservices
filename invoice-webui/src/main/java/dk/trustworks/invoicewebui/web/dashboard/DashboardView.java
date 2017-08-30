package dk.trustworks.invoicewebui.web.dashboard;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.web.dashboard.cards.ConsultantLocationCardDesign;
import dk.trustworks.invoicewebui.web.dashboard.cards.TopCardDesign;
import dk.trustworks.invoicewebui.web.dashboard.cards.MenuItemCardImpl;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 12/08/2017.
 */
@SpringView(name = DashboardView.VIEW_NAME)
public class DashboardView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "mainmenu";

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        ResponsiveLayout board = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);
        board.addStyleName("bg-grey");
        board.setSizeFull();

        MenuItemCardImpl timeCard = new MenuItemCardImpl(
                "time-card.jpg",
                "Time Manager",
                "NOT IMPLEMENTED YET!!!"
        );
        timeCard.getCardHolder().addLayoutClickListener(layoutClickEvent -> {
            getUI().getNavigator().navigateTo("time");
        });
        timeCard.setHeight("100%");

        MenuItemCardImpl invoiceCard = new MenuItemCardImpl(
                "invoice-card.jpg",
                "Invoice Manager",
                "Invoice Manager is a simple and powerful online invoicing solution for Trustworks. Create PDF invoices, (eventually) send over email, and register online payments."
        );
        invoiceCard.getCardHolder().addLayoutClickListener(layoutClickEvent -> {
            getUI().getNavigator().navigateTo("invoice");
        });

        MenuItemCardImpl resourcePlanningCard = new MenuItemCardImpl(
                "resource-planning-card.jpg",
                "Resource Planner",
                "Get the ultimate birds eye view of our team with Resource Planner . View availability, capacity and schedule our resources on projects."
        );
        resourcePlanningCard.getCardHolder().addLayoutClickListener(layoutClickEvent -> {
            getUI().getNavigator().navigateTo("resourceplanning");
            //getUI().getPage().setLocation("http://stats.trustworks.dk:9098");
        });

        MenuItemCardImpl clientCard = new MenuItemCardImpl(
                "client-card.jpg",
                "Client Manager",
                "Client Manager is where all Trustworks clients live, including their contact information and logos."
        );

        clientCard.getCardHolder().addLayoutClickListener(layoutClickEvent -> {
            getUI().getNavigator().navigateTo("client");
        });

        MenuItemCardImpl projectCard = new MenuItemCardImpl(
                "project-card.png",
                "Project Manager",
                "Project Manager is to projects what Client Manager is to clients. Create and manage projects, budgets, and rates. NOT IMPLEMENTED YET."
        );
        projectCard.getCardHolder().addLayoutClickListener(layoutClickEvent -> {
            getUI().getNavigator().navigateTo("project");
        });

        MenuItemCardImpl mapCard = new MenuItemCardImpl("map-card.jpg",
                "Map Manager",
                "With Map Manager you can follow your fellow colegues. Which customers are they working with and where on earth (probably Copenhagen) are they located.");
        mapCard.getCardHolder().addLayoutClickListener(
                layoutClickEvent -> getUI().getPage().setLocation("http://map.trustworks.dk:9096/map"));
        mapCard.setHeight("100%");

        ConsultantLocationCardDesign locationCardDesign = new ConsultantLocationCardDesign();
        locationCardDesign.setWidth("100%");
        BrowserFrame browser = new BrowserFrame(null,
                new ExternalResource("http://map.trustworks.dk:9096/map"));
        browser.setHeight("400px");
        browser.setWidth("100%");
        locationCardDesign.getIframeHolder().addComponent(browser);

        ConsultantLocationCardDesign monthNewsCardDesign = new ConsultantLocationCardDesign();
        monthNewsCardDesign.setWidth("100%");
        BrowserFrame browser2 = new BrowserFrame(null,
                new ExternalResource("https://player.vimeo.com/video/180634606?title=0&byline=0&portrait=0"));
        browser2.setHeight("400px");
        browser2.setWidth("100%");
        monthNewsCardDesign.getIframeHolder().addComponent(browser2);
        /*
        <iframe src="https://player.vimeo.com/video/180634606?title=0&byline=0&portrait=0" width="640" height="479" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>
         */

        TopCardDesign consultantsCard = new TopCardDesign();
        consultantsCard.getImgIcon().setSource(new ThemeResource("images/ic_people_black_48dp_2x.png"));
        consultantsCard.getLblNumber().setValue("18");
        consultantsCard.getLblTitle().setValue("Trustworks Consultants");
        consultantsCard.getLblSubtitle().setValue("10% more than last year");
        consultantsCard.getCardHolder().addStyleName("medium-blue");
        ResponsiveRow row0 = board.addRow();
        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(consultantsCard);

        TopCardDesign consultantsCard2 = new TopCardDesign();
        consultantsCard2.getImgIcon().setSource(new ThemeResource("images/ic_people_black_48dp_2x.png"));
        consultantsCard2.getLblNumber().setValue("9");
        consultantsCard2.getLblTitle().setValue("Active Projects");
        consultantsCard2.getLblSubtitle().setValue("15% more than last year");
        consultantsCard2.getCardHolder().addStyleName("dark-green");
        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(consultantsCard2);

        TopCardDesign consultantsCard3 = new TopCardDesign();
        consultantsCard3.getImgIcon().setSource(new ThemeResource("images/ic_people_black_48dp_2x.png"));
        consultantsCard3.getLblNumber().setValue("1565");
        consultantsCard3.getLblTitle().setValue("Billable Hours");
        consultantsCard3.getLblSubtitle().setValue("5% more than last year");
        consultantsCard3.getCardHolder().addStyleName("orange");
        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(consultantsCard3);

        TopCardDesign consultantsCard4 = new TopCardDesign();
        consultantsCard4.getImgIcon().setSource(new ThemeResource("images/ic_people_black_48dp_2x.png"));
        consultantsCard4.getLblNumber().setValue("18");
        consultantsCard4.getLblTitle().setValue("Trustworks Consultants");
        consultantsCard4.getLblSubtitle().setValue("10% more than last year");
        consultantsCard4.getCardHolder().addStyleName("dark-grey");
        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(consultantsCard4);

        ResponsiveRow row1 = board.addRow();
        ResponsiveRow row2 = board.addRow();
        ResponsiveRow row3 = board.addRow();


        row1.addColumn()
                .withDisplayRules(12, 12, 4, 4)
                .withComponent(monthNewsCardDesign);
        row1.addColumn()
                .withDisplayRules(12, 12, 8, 8)
                .withComponent(locationCardDesign);

        row2.addColumn()
                .withDisplayRules(12, 6, 6, 3)
                .withComponent(timeCard);
        row2.addColumn()
                .withDisplayRules(12, 6, 6, 3)
                .withComponent(resourcePlanningCard);
        row2.addColumn()
                .withDisplayRules(12, 6, 6, 3)
                .withComponent(mapCard);
        row2.addColumn()
                .withDisplayRules(12, 6, 6, 3)
                .withComponent(invoiceCard);

        row3.addColumn()
                .withDisplayRules(12, 6, 6, 3)
                .withComponent(projectCard);
        row3.addColumn()
                .withDisplayRules(12, 6, 6, 3)
                .withComponent(clientCard);

        mainTemplate.setMainContent(board);
        //this.addComponent(board);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //Authorizer.authorize(this);
    }
}