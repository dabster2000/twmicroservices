package dk.trustworks.invoicewebui.web;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hans on 02/07/2017.
 */
@Push
@SpringUI
@SpringViewDisplay
@Theme("invoice")
public class VaadinUI extends UI implements Broadcaster.BroadcastListener, ViewDisplay {

    //private final InvoiceListView invoiceView;
    //private final InvoiceListImpl invoiceList;
    //private final ProjectListImpl projectList;
    //private final MainWindowImpl mainWindow;

    private Panel springViewDisplay;

    private VerticalLayout mainLayout;


    @Autowired
    public VaadinUI() {
        //this.projectList = projectList;
        //this.mainWindow = mainWindow;
    }


    @Override
    protected void init(VaadinRequest request) {
        //Broadcaster.register(this);

        final VerticalLayout root = new VerticalLayout();
        root.setSpacing(false);
        root.setMargin(false);
        root.setSizeFull();
        setContent(root);

        springViewDisplay = new Panel();
        springViewDisplay.setSizeFull();
        root.addComponent(springViewDisplay);
        root.setExpandRatio(springViewDisplay, 1.0f);

/*
        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();


        springViewDisplay = new Panel();
        springViewDisplay.setSizeFull();
        mainLayout.addComponent(springViewDisplay);
*/
        //setContent(mainWindow);
        //mainWindow.init();
        //receiveBroadcast("");
        //setTheme("invoice");
        //RootWindow rootWindow = new RootWindow();
        //rootWindow.get
        /*
        Board board = new Board();
        board.setSizeFull();
        Image image = new Image("", new ThemeResource("images/top-bar.png"));
        image.setResponsive(true);
        image.setWidth(100, Unit.PERCENTAGE);
        image.setHeightUndefined();
        board.addRow(image);
        /*
        CssLayout cssLayout = new CssLayout();
        cssLayout.setStyleName("top-bar");
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100");
        board.addRow(cssLayout);
        */
/*
        MenuItemCardImpl timeCard = new MenuItemCardImpl(
                "time-card.jpg",
                "Time Manager",
                "Time Manager is a simple and powerful online invoicing solution for Joomla. Create PDF invoices, send over email, manage contacts and collect online payments."
        );
        timeCard.getCardHolder().addLayoutClickListener(layoutClickEvent -> {
            System.out.println("layoutClickEvent = " + layoutClickEvent.toString());
        });

        board.addRow(
                timeCard,
                new MenuItemCardImpl("client-card.jpg", "Client Manager", "Time Manager is a simple and powerful online invoicing solution for Joomla. Create PDF invoices, send over email, manage contacts and collect online payments."),
                new MenuItemCardImpl("map-card.jpg", "Map Manager", "Time Manager is a simple and powerful online invoicing solution for Joomla. Create PDF invoices, send over email, manage contacts and collect online payments."),
                new MenuItemCardImpl("invoice-card.jpg", "Invoice Manager", "Time Manager is a simple and powerful online invoicing solution for Joomla. Create PDF invoices, send over email, manage contacts and collect online payments."));
        mainLayout.addComponent(board);*/
        //setContent(mainLayout);
    }


    @Override
    public void receiveBroadcast(String message) {
        System.out.println("MainWindowImpl.receiveBroadcast");
        System.out.println("message = " + message);
        //projectList.reloadData();
        //int numberOfDrafts = mainWindow.invoiceClient.findByStatus(DRAFT).getContent().size();
        //System.out.println("numberOfDrafts = " + numberOfDrafts);
        getUI().access(() -> {
            //if(numberOfDrafts>0) mainWindow.getMenuButton4().setCaption("DRAFTS ("+numberOfDrafts+")");
            //else mainWindow.getMenuButton4().setCaption("DRAFTS");
        });

    }


    @Override
    public void showView(View view) {
        springViewDisplay.setContent((Component) view);
    }
}