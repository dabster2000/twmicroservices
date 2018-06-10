package dk.trustworks.invoicewebui.web.dashboard;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontIcon;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import dk.trustworks.invoicewebui.jobs.DashboardPreloader;
import dk.trustworks.invoicewebui.model.RoleType;
import dk.trustworks.invoicewebui.repositories.BubbleMemberRepository;
import dk.trustworks.invoicewebui.repositories.BubbleRepository;
import dk.trustworks.invoicewebui.repositories.NewsRepository;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.EmailSender;
import dk.trustworks.invoicewebui.services.PhotoService;
import dk.trustworks.invoicewebui.web.dashboard.cards.*;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.stats.components.Card;
import dk.trustworks.invoicewebui.web.stats.components.RevenuePerMonthChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.materialicons.MaterialIcons;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hans on 12/08/2017.
 */
@AccessRules(roleTypes = {RoleType.USER, RoleType.EXTERNAL})
@SpringView(name = DashboardView.VIEW_NAME)
public class DashboardView extends VerticalLayout implements View {

    private static final Logger logger = LoggerFactory.getLogger(DashboardView.class);
    /**
     * Templates
     * Apps i Trustworks
     * Reminder til projektowners at en client ikke har et logo
     * Reminder om at der ikke er en projektejer til sales team
     * Reminder til projektejer at budgettet er ved at løbe ud på projekt/konsulent
     * Employee page med cv, billede, etc.
     * Project siden skal indeholde en graf over budgetterne
     * Man skal kunne ændre på nyhederne/events
     * Brugerne skal have tilsendt nye passwords
     * Man skal have overblik over sit eget IT budget
     * Overblik over projekter
     */

    public static final String VIEW_NAME = "mainmenu";
    public static final String VIEW_BREADCRUMB = "Dashboard";
    public static final FontIcon VIEW_ICON = MaterialIcons.DASHBOARD;
    public static final String MENU_NAME = "Dashboard";

    private final TopMenu topMenu;

    private final MainTemplate mainTemplate;

    private final BubbleRepository bubbleRepository;

    private final BubbleMemberRepository bubbleMemberRepository;

    private final NewsRepository newsRepository;

    private final UserRepository userRepository;

    private final PhotoService photoService;

    private final DashboardPreloader dashboardPreloader;

    private final DashboardBoxCreator dashboardBoxCreator;

    private final EmailSender emailSender;

    private final RevenuePerMonthChart revenuePerMonthChart;

    @Autowired
    public DashboardView(TopMenu topMenu, MainTemplate mainTemplate, BubbleRepository bubbleRepository, BubbleMemberRepository bubbleMemberRepository, NewsRepository newsRepository, UserRepository userRepository, PhotoService photoService, DashboardPreloader dashboardPreloader, DashboardBoxCreator dashboardBoxCreator, EmailSender emailSender, RevenuePerMonthChart revenuePerMonthChart) {
        this.topMenu = topMenu;
        this.mainTemplate = mainTemplate;
        this.bubbleRepository = bubbleRepository;
        this.bubbleMemberRepository = bubbleMemberRepository;
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.photoService = photoService;
        this.dashboardPreloader = dashboardPreloader;
        this.dashboardBoxCreator = dashboardBoxCreator;
        this.emailSender = emailSender;
        this.revenuePerMonthChart = revenuePerMonthChart;
    }

    @Transactional
    @PostConstruct
    void init() {
        this.setMargin(false);
        this.setSpacing(false);
        this.addComponent(topMenu);
        this.addComponent(mainTemplate);
        ResponsiveLayout board = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID).withFlexible();
        board.setSizeFull();
        board.setScrollable(true);

        //BirthdayCardImpl birthdayCard = new BirthdayCardImpl(trustworksEventRepository, 1, 6, "birthdayCard");
        PhotosCardImpl photoCard = new PhotosCardImpl(dashboardPreloader, 1, 6, "photoCard");
        NewsImpl newsCard = new NewsImpl(userRepository, newsRepository, 1, 12, "newsCard");
        DnaCardImpl dnaCard = new DnaCardImpl(10, 4, "dnaCard");
        CateringCardImpl cateringCard = new CateringCardImpl(userRepository.findByActiveTrueOrderByUsername(), emailSender,3, 4, "cateringCard");
        cateringCard.init();
        //ConsultantLocationCardImpl locationCardDesign = new ConsultantLocationCardImpl(projectRepository, photoRepository, 2, 6, "locationCardDesign");
        VideoCardImpl monthNewsCardDesign = new VideoCardImpl(2, 6 , "monthNewsCardDesign");
        VideoCardImpl tripVideosCardDesign = new VideoCardImpl(3, 6, "tripVideosCardDesign");
        BubblesCardImpl bubblesCardDesign = new BubblesCardImpl(bubbleRepository, bubbleMemberRepository, photoService, 1, 6, "bubblesCard");
        VacationCard vacationCard = new VacationCard();
        //ProjectTimelineImpl projectTimeline = new ProjectTimelineImpl(projectRepository, 2, 6, "projectTimeline");

        //projectTimeline.init();

        //locationCardDesign.init();
        //locationCardDesign.setWidth("100%");

        monthNewsCardDesign.setWidth("100%");
        BrowserFrame browser2 = new BrowserFrame(null, new ExternalResource(dashboardPreloader.getTrustworksStatus()));
        browser2.setHeight("300px");
        browser2.setWidth("100%");
        monthNewsCardDesign.getIframeHolder().addComponent(browser2);

        tripVideosCardDesign.setWidth("100%");
        BrowserFrame tripVideoBrowser = new BrowserFrame(null, new ExternalResource(dashboardPreloader.getTrips()[0]));
        tripVideoBrowser.setHeight("300px");
        tripVideoBrowser.setWidth("100%");
        //tripVideosCardDesign.getLblTitle().setValue("Trustworks Travel Videos");
        tripVideosCardDesign.getIframeHolder().addComponent(tripVideoBrowser);

        dnaCard.getBoxComponent().setHeight("600px");
        cateringCard.getBoxComponent().setHeight("600px");

        photoCard.loadPhoto();

        createTopBoxes(board);

        Card revenuePerMonthCard = new Card();
        revenuePerMonthCard.setHeight(300, Unit.PIXELS);
        revenuePerMonthCard.getLblTitle().setValue("Revenue Per Month");
        int adjustStartYear = 0;
        if(LocalDate.now().getMonthValue() >= 1 && LocalDate.now().getMonthValue() <=6)  adjustStartYear = 1;
        LocalDate localDateStart = LocalDate.now().withMonth(7).withDayOfMonth(1).minusYears(adjustStartYear);
        LocalDate localDateEnd = localDateStart.plusYears(1);
        revenuePerMonthCard.getContent().addComponent(revenuePerMonthChart.createRevenuePerMonthChart(localDateStart, localDateEnd, false));

        List<Box> boxes = new ArrayList<>();
        //boxes.add(birthdayCard);
        boxes.add(newsCard);
        boxes.add(photoCard);
        boxes.add(bubblesCardDesign);
        //boxes.add(locationCardDesign);
        boxes.add(cateringCard);
        boxes.add(monthNewsCardDesign);
        boxes.add(tripVideosCardDesign);
        boxes.add(dnaCard);

        //boxes.add(revenuePerMonthCard);
        //boxes.add(projectTimeline);
        //boxes.add(statusCard);

        //createRows(board, boxes);
        ResponsiveRow mainRow = board.addRow().withGrow(true);
        ResponsiveColumn mainComponentColumn = mainRow.addColumn().withDisplayRules(12, 12, 9, 9);
        ResponsiveColumn leftColumn = mainRow.addColumn().withDisplayRules(12, 12, 3, 3);
        leftColumn.withComponent(newsCard);
        Resource res = new ThemeResource("images/hans.png");

// Display the image without caption
        Image image = new Image(null, res);
        image.setStyleName("img-circle");
        board.addRow().addColumn().withComponent(image);

        ResponsiveLayout mainLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID).withFlexible();
        mainComponentColumn.withComponent(mainLayout);
        ResponsiveRow row1 = mainLayout.addRow();
        //ResponsiveRow row1 = board.addRow().withGrow(true);
        //row1.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(newsCard);
        row1.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(photoCard);
        row1.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(bubblesCardDesign);

        ResponsiveRow row2 = mainLayout.addRow();
        row2.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(monthNewsCardDesign);
        row2.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(tripVideosCardDesign);

        ResponsiveRow row3 = mainLayout.addRow();
        row3.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(cateringCard);
        row3.addColumn().withDisplayRules(12, 12, 6 ,6).withComponent(revenuePerMonthCard);

        ResponsiveRow row4 = mainLayout.addRow();
        row4.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(vacationCard);
        row4.addColumn().withDisplayRules(12, 12, 6, 6).withComponent(dnaCard);

        mainTemplate.setMainContent(board, DashboardView.VIEW_ICON, DashboardView.MENU_NAME, "World of Trustworks", DashboardView.VIEW_BREADCRUMB);
    }

    private void createTopBoxes(ResponsiveLayout board) {
        ResponsiveRow row0 = board.addRow();
        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.getGoodPeopleBox()));

        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.createActiveProjectsBox()));

        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.createBillableHoursBox()));

        row0.addColumn()
                .withDisplayRules(12, 6, 3, 3)
                .withComponent(new TopCardImpl(dashboardBoxCreator.createConsultantsPerProjectBox()));
    }

    private void createRows(ResponsiveLayout board, List<Box> boxes) {
        boxes.sort(Comparator.comparing(Box::getPriority).thenComparing(Box::getBoxWidth, Comparator.reverseOrder()));

        int maxRows = 0;
        int[] rowSize = new int[10];
        Map<Box, Integer> boxIntegerMap = new HashMap<>();
        for (Box box : boxes) {
            for (int i = 0; i < rowSize.length; i++) {
                if(rowSize[i] + box.getBoxWidth() <= 12) {
                    boxIntegerMap.put(box, i);
                    rowSize[i] += box.getBoxWidth();
                    if(maxRows < i) maxRows = i;
                    break;
                }
            }
        }

        ResponsiveRow[] responsiveRows = new ResponsiveRow[maxRows+1];
        for (int i = 0; i < maxRows + 1; i++) {
            responsiveRows[i] = board.addRow().withGrow(true);
        }

        Map<Box, Integer> result = boxIntegerMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));


        for (Box box : result.keySet()) {
            Integer row = boxIntegerMap.get(box);
            int[] widths = new int[4];
            switch (box.getBoxWidth()) {
                case 3:
                    widths[0] = 3;
                    widths[1] = 4;
                    widths[2] = 6;
                    widths[3] = 12;
                    break;
                case 4:
                    widths[0] = 4;
                    widths[1] = 6;
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
                    widths[1] = 12;
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
            responsiveRows[0].addColumn()
                    .withDisplayRules(widths[3], widths[2], widths[1], widths[0])
                    .withComponent(box.getBoxComponent());
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        topMenu.init();
    }

}
