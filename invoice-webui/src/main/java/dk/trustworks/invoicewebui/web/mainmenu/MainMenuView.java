package dk.trustworks.invoicewebui.web.mainmenu;

import com.vaadin.board.Board;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.web.mainmenu.cards.MenuItemCardImpl;
import dk.trustworks.invoicewebui.web.security.Authorizer;

import javax.annotation.PostConstruct;

/**
 * Created by hans on 12/08/2017.
 */
@SpringView(name = MainMenuView.VIEW_NAME)
public class MainMenuView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "mainmenu";

    @PostConstruct
    void init() {
        Board board = new Board();
        board.setSizeFull();
        Image image = new Image("", new ThemeResource("images/top-bar.png"));
        image.setResponsive(true);
        image.setWidth(100, Unit.PERCENTAGE);
        image.setHeightUndefined();
        board.addRow(image);

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
        mapCard.getCardHolder().addLayoutClickListener(layoutClickEvent -> {
            getUI().getPage().setLocation("http://map.trustworks.dk:9096/map");
        });
        mapCard.setHeight("100%");

        board.addRow(
                timeCard,
                clientCard,
                projectCard,
                mapCard);

        board.addRow(invoiceCard, resourcePlanningCard, new Label(""), new Label(""));
        this.addComponent(board);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Authorizer.authorize(this);
    }
}