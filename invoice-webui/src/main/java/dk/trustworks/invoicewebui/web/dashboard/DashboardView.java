package dk.trustworks.invoicewebui.web.dashboard;

import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.network.clients.UserStatusClient;
import dk.trustworks.invoicewebui.web.dashboard.cards.*;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by hans on 12/08/2017.
 */
@SpringView(name = DashboardView.VIEW_NAME)
public class DashboardView extends VerticalLayout implements View {

    /**
     * Fødselsdage
     * Templates
     * Apps i Trustworks
     */

    public static final String VIEW_NAME = "mainmenu";

    @Autowired
    private TopMenu topMenu;

    @Autowired
    private MainTemplate mainTemplate;

    @Autowired
    private UserStatusClient userStatusClient;

    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        ResponsiveLayout board = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID);

        board.setSizeFull();

        BirthdayCardImpl birthdayCard = new BirthdayCardImpl(2, 4, "birthdayCard");
        DnaCardImpl dnaCard = new DnaCardImpl(3, 4, "dnaCard");

        ConsultantLocationCardImpl locationCardDesign = new ConsultantLocationCardImpl(2, 8, "locationCardDesign");
        locationCardDesign.setWidth("100%");
        BrowserFrame browser = new BrowserFrame(null,
                new ExternalResource("http://map.trustworks.dk:9096/map"));
        browser.setHeight("400px");
        browser.setWidth("100%");
        locationCardDesign.getIframeHolder().addComponent(browser);

        ConsultantLocationCardImpl monthNewsCardDesign = new ConsultantLocationCardImpl(1, 4 , "monthNewsCardDesign");
        monthNewsCardDesign.setWidth("100%");
        BrowserFrame browser2 = new BrowserFrame(null,
                new ExternalResource("https://www.youtube-nocookie.com/embed/PWoyyHDRd7U?rel=0&amp;controls=0&amp;showinfo=0"));
        browser2.setHeight("400px");
        browser2.setWidth("100%");
        monthNewsCardDesign.getIframeHolder().addComponent(browser2);
        monthNewsCardDesign.getLblTitle().setValue("State of Trustworks");

        createTopBoxes(board);

        List<Box> boxes = new ArrayList<>();
        boxes.add(birthdayCard);
        boxes.add(locationCardDesign);
        boxes.add(monthNewsCardDesign);
        boxes.add(dnaCard);
        /*
        boxes.add(new Box(4, 3, clientCard, "clientCard"));
        boxes.add(new Box(3, 6, locationCardDesign, "locationCardDesign"));
        boxes.add(new Box(2, 4, birthdayCard, "birthdayCard"));
        boxes.add(new Box(3, 3, invoiceCard, "invoiceCard"));
        boxes.add(new Box(1, 4, monthNewsCardDesign, "monthNewsCardDesign"));
        boxes.add(new Box(4, 3, projectCard, "projectCard"));
        boxes.add(new Box(3, 3, resourcePlanningCard, "resourcePlanningCard"));
        boxes.add(new Box(3, 3, timeCard, "timeCard"));
        */

        createRows(board, boxes);

        mainTemplate.setMainContent(board);
    }

    private void createTopBoxes(ResponsiveLayout board) {
        TopCardDesign consultantsCard = new TopCardDesign();
        float goodPeopleNow = userStatusClient.findAllActive().getContent().size();
        System.out.println("goodPeopleNow = " + goodPeopleNow);
        String date = LocalDate.now().minusYears(1).toString("yyyy-MM-dd");
        System.out.println("date = " + date);
        float goodPeopleLastYear = userStatusClient.findAllActiveByDate(date).getContent().size();
        System.out.println("goodPeopleLastYear = " + goodPeopleLastYear);
        int percent = Math.round((goodPeopleNow / goodPeopleLastYear) * 100) - 100;
        System.out.println("percent = " + percent);
        consultantsCard.getImgIcon().setSource(new ThemeResource("images/ic_people_black_48dp_2x.png"));
        consultantsCard.getLblNumber().setValue(Math.round(goodPeopleNow)+"");
        consultantsCard.getLblTitle().setValue("Good People");
        consultantsCard.getLblSubtitle().setValue(percent + "% more than last year");
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
    }

    private void createRows(ResponsiveLayout board, List<Box> boxes) {
        boxes.sort(Comparator.comparing(Box::getPriority).thenComparing(Box::getBoxWidth, Comparator.reverseOrder()));

        int maxRows = 0;
        int[] rowSize = new int[10];
        Map<Box, Integer> boxIntegerMap = new HashMap<>();
        for (Box box : boxes) {
            for (int i = 0; i < rowSize.length; i++) {
                System.out.println("--- --- ---");
                System.out.println("i = " + i);
                System.out.println("rowSize = " + rowSize[i]);
                System.out.println("box = " + box);
                if(rowSize[i] + box.getBoxWidth() <= 12) {
                    boxIntegerMap.put(box, i);
                    rowSize[i] += box.getBoxWidth();
                    if(maxRows < i) maxRows = i;
                    System.out.println("--- put ---");
                    System.out.println("--- --- ---");
                    break;
                }
            }
        }

        System.out.println("XXX XXX XXX");
        ResponsiveRow[] responsiveRows = new ResponsiveRow[maxRows+1];
        for (int i = 0; i < maxRows + 1; i++) {
            responsiveRows[i] = board.addRow();
        }


        for (Box box : boxIntegerMap.keySet()) {
            Integer row = boxIntegerMap.get(box);
            int[] widths = new int[4];
            switch (box.getBoxWidth()) {
                case 3:
                    widths[0] = 3;
                    widths[1] = 6;
                    widths[2] = 6;
                    widths[3] = 12;
                    break;
                case 4:
                    widths[0] = 4;
                    widths[1] = 4;
                    widths[2] = 12;
                    widths[3] = 12;
                    break;
                case 6:
                    widths[0] = 6;
                    widths[1] = 6;
                    widths[2] = 12;
                    widths[3] = 12;
                    break;
                case 8:
                    widths[0] = 8;
                    widths[1] = 8;
                    widths[2] = 12;
                    widths[3] = 12;
                    break;
                case 9:
                    widths[0] = 9;
                    widths[1] = 12;
                    widths[2] = 12;
                    widths[3] = 12;
                    break;
                case 12:
                    widths[0] = 12;
                    widths[1] = 12;
                    widths[2] = 12;
                    widths[3] = 12;
                    break;
            }
            System.out.println(row+": "+box.getPriority()+" "+" "+" "+box.getBoxWidth()+" "+" "+" "+box.getName());
            responsiveRows[row].addColumn()
                    .withDisplayRules(widths[3], widths[2], widths[1], widths[0])
                    .withComponent(box.getBoxComponent());
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        //Authorizer.authorize(this);
    }

}


/*
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

 */