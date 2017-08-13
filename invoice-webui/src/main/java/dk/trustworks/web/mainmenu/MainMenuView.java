package dk.trustworks.web.mainmenu;

import com.vaadin.board.Board;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.web.mainmenu.cards.MenuItemCardImpl;

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
                "Time Manager is a simple and powerful online invoicing solution for Joomla. Create PDF invoices, send over email, manage contacts and collect online payments."
        );
        timeCard.getCardHolder().addLayoutClickListener(layoutClickEvent -> {
            System.out.println("layoutClickEvent = " + layoutClickEvent.toString());
        });

        MenuItemCardImpl invoiceCard = new MenuItemCardImpl(
                "invoice-card.jpg",
                "Invoice Manager",
                "Time Manager is a simple and powerful online invoicing solution for Joomla. Create PDF invoices, send over email, manage contacts and collect online payments."
        );
        invoiceCard.getCardHolder().addLayoutClickListener(layoutClickEvent -> {
            getUI().getNavigator().navigateTo("invoice");
        });

        MenuItemCardImpl clientCard = new MenuItemCardImpl(
                "client-card.jpg",
                "Client Manager",
                "Time Manager is a simple and powerful online invoicing solution for Joomla. Create PDF invoices, send over email, manage contacts and collect online payments."
        );
        clientCard.getCardHolder().addLayoutClickListener(layoutClickEvent -> {
            getUI().getNavigator().navigateTo("client");
        });


        board.addRow(
                timeCard,
                clientCard,
                new MenuItemCardImpl("map-card.jpg", "Map Manager", "Time Manager is a simple and powerful online invoicing solution for Joomla. Create PDF invoices, send over email, manage contacts and collect online payments."),
                invoiceCard
                );
        this.addComponent(board);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }
}