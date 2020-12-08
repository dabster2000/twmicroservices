package dk.trustworks.invoicewebui.web.dashboard;

import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontIcon;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import dk.trustworks.invoicewebui.jobs.DashboardPreloader;
import dk.trustworks.invoicewebui.model.*;
import dk.trustworks.invoicewebui.model.Notification;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.NotificationType;
import dk.trustworks.invoicewebui.model.enums.ReminderType;
import dk.trustworks.invoicewebui.model.enums.RoleType;
import dk.trustworks.invoicewebui.network.clients.VimeoAPI;
import dk.trustworks.invoicewebui.repositories.*;
import dk.trustworks.invoicewebui.security.AccessRules;
import dk.trustworks.invoicewebui.services.*;
import dk.trustworks.invoicewebui.utils.SpriteSheet;
import dk.trustworks.invoicewebui.web.common.BoxImpl;
import dk.trustworks.invoicewebui.web.contexts.UserSession;
import dk.trustworks.invoicewebui.web.dashboard.cards.*;
import dk.trustworks.invoicewebui.web.dashboard.components.ConfirmSpeedDateImpl;
import dk.trustworks.invoicewebui.web.dashboard.components.NotificationPopupDesign;
import dk.trustworks.invoicewebui.web.dashboard.components.ReleaseNotesPopupDesign;
import dk.trustworks.invoicewebui.web.mainmenu.components.MainTemplate;
import dk.trustworks.invoicewebui.web.mainmenu.components.TopMenu;
import dk.trustworks.invoicewebui.web.profile.components.KnowledgeChart;
import dk.trustworks.invoicewebui.web.stats.components.RevenuePerMonthChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.alump.materialicons.MaterialIcons;
import org.vaadin.viritin.label.MLabel;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

/**
 * Created by hans on 12/08/2017.
 */
@AccessRules(roleTypes = {RoleType.USER, RoleType.EXTERNAL})
@SpringView(name = DashboardView.VIEW_NAME)
public class   DashboardView extends VerticalLayout implements View {

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
     * Man skal have overblik over sit eget IT budget
     * Overblik over projekter
     */

    public static final String VIEW_NAME = "mainmenu";
    public static final String VIEW_BREADCRUMB = "Dashboard";
    public static final FontIcon VIEW_ICON = MaterialIcons.DASHBOARD;
    public static final String MENU_NAME = "Dashboard";

    private final TopMenu topMenu;

    private final MainTemplate mainTemplate;

    private final ContractService contractService;

    private final BubbleService bubbleService;

    private final NewsRepository newsRepository;

    private final ReminderHistoryRepository reminderHistoryRepository;

    private final NotificationRepository notificationRepository;

    private final UserService userService;

    private final PhotoService photoService;

    private final DashboardPreloader dashboardPreloader;

    private final DashboardBoxCreator dashboardBoxCreator;

    private final RevenuePerMonthChart revenuePerMonthChart;

    private final SpriteSheet spriteSheet;

    private final KnowledgeChart knowledgeChart;

    private final BudgetService budgetService;

    private final AvailabilityService availabilityService;

    @Autowired
    public DashboardView(TopMenu topMenu, MainTemplate mainTemplate, ContractService contractService, BubbleService bubbleService, NewsRepository newsRepository, ReminderHistoryRepository reminderHistoryRepository, NotificationRepository notificationRepository, UserService userService, PhotoService photoService, DashboardPreloader dashboardPreloader, DashboardBoxCreator dashboardBoxCreator, RevenuePerMonthChart revenuePerMonthChart, SpriteSheet spriteSheet, KnowledgeChart knowledgeChart, BudgetService budgetService, AvailabilityService availabilityService) {
        this.topMenu = topMenu;
        this.mainTemplate = mainTemplate;
        this.contractService = contractService;
        this.bubbleService = bubbleService;
        this.newsRepository = newsRepository;
        this.reminderHistoryRepository = reminderHistoryRepository;
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.photoService = photoService;
        this.dashboardPreloader = dashboardPreloader;
        this.dashboardBoxCreator = dashboardBoxCreator;
        this.revenuePerMonthChart = revenuePerMonthChart;
        this.spriteSheet = spriteSheet;
        this.knowledgeChart = knowledgeChart;
        this.budgetService = budgetService;
        this.availabilityService = availabilityService;
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

        User randomFocusUser = userService.findCurrentlyEmployedUsers(true, ConsultantType.CONSULTANT).get(new Random(System.currentTimeMillis()).nextInt(userService.findCurrentlyEmployedUsers(true, ConsultantType.CONSULTANT).size() - 1));

        BoxImpl knowledgeChartCard = new BoxImpl();
        knowledgeChartCard.getContent().setDefaultComponentAlignment(Alignment.TOP_CENTER);

        knowledgeChartCard.instance(photoService.getRoundImage(randomFocusUser.getUuid(), false, 25, Unit.PERCENTAGE));

        knowledgeChartCard.instance(
                new MLabel(randomFocusUser.getFirstname()+" "+randomFocusUser.getLastname()+" is working at...")
                        .withStyleName("h5 bold"));

        List<Contract> contractList = contractService.findTimeActiveConsultantContracts(randomFocusUser, LocalDate.now());
        int i = 0;
        HorizontalLayout hlayout = null;
        for (Contract contract : contractList) {
            if(i==0) {
                hlayout = new HorizontalLayout();
                hlayout.setSizeFull();
                knowledgeChartCard.instance(hlayout);
            }
            Image image = new Image("", photoService.getRelatedPhotoResource(contract.getClientuuid()));
            image.setWidth(100, Unit.PERCENTAGE);
            hlayout.addComponent(image);
            i++;
            if(i==2) i=0;
        }
        knowledgeChartCard.instance(
                new MLabel("")
                        .withStyleName("h5 bold"));
        knowledgeChartCard.instance(
                new MLabel("Using this set of skills...")
                        .withStyleName("h5 bold"));

        knowledgeChartCard.instance(knowledgeChart.getChart(randomFocusUser));

        PhotosCardImpl photoCard = new PhotosCardImpl(dashboardPreloader, 1, 6, "photoCard");
        PhotosCardImpl knowledgeWheelPhoto = new PhotosCardImpl(dashboardPreloader, 1, 6, "photoCard").loadResourcePhoto((Math.random()<0.5)?"images/cards/knowledge/lifecycle.png":"images/cards/knowledge/pejlemaerker.png");
        NewsImpl newsCard = new NewsImpl(userService, newsRepository, 1, 12, "newsCard");
        DnaCardImpl dnaCard = new DnaCardImpl(10, 4, "dnaCard");
        VideoCardImpl monthNewsCardDesign = new VideoCardImpl(2, 6 , "monthNewsCardDesign");
        VideoCardImpl tripVideosCardDesign = new VideoCardImpl(3, 6, "tripVideosCardDesign");
        BubblesCardImpl bubblesCardDesign = new BubblesCardImpl(bubbleService, photoService, Optional.empty());
        ConsultantAllocationCardImpl consultantAllocationCard = new ConsultantAllocationCardImpl(availabilityService, budgetService, 2, 6, "consultantAllocationCardDesign");

        monthNewsCardDesign.setWidth("100%");
        BrowserFrame browser2 = new BrowserFrame(null, new ExternalResource(dashboardPreloader.getTrustworksStatus()));
        browser2.setHeight("300px");
        browser2.setWidth("100%");
        monthNewsCardDesign.getCardHolder().addStyleName("dark-grey");
        monthNewsCardDesign.getIframeHolder().addComponent(browser2);

        tripVideosCardDesign.setWidth("100%");
        BrowserFrame tripVideoBrowser = new BrowserFrame(null, new ExternalResource(dashboardPreloader.getTrips()[0]));
        tripVideoBrowser.setHeight("300px");
        tripVideoBrowser.setWidth("100%");
        tripVideosCardDesign.getIframeHolder().addComponent(tripVideoBrowser);

        //dnaCard.getBoxComponent().setHeight("600px");
        //cateringCard.getBoxComponent().setHeight("600px");

        photoCard.loadRandomPhoto();

        createTopBoxes(board);

        //Card revenuePerMonthCard = new Card();
        //revenuePerMonthCard.setHeight(300, PIXELS);
        //revenuePerMonthCard.getLblTitle().setValue("Revenue Per Month");
        int adjustStartYear = 0;
        if(LocalDate.now().getMonthValue() <= 6)  adjustStartYear = 1;
        LocalDate localDateStart = LocalDate.now().withMonth(7).withDayOfMonth(1).minusYears(adjustStartYear);
        LocalDate localDateEnd = localDateStart.plusYears(1);
        //revenuePerMonthCard.getContent().addComponent(revenuePerMonthChart.createRevenuePerMonthChart(localDateStart, localDateEnd, false));
        BoxImpl revenuePerMonthCard = new BoxImpl().instance(revenuePerMonthChart.createRevenuePerMonthChart(localDateStart, localDateEnd, false));

        ResponsiveRow mainRow = board.addRow().withGrow(true);
        ResponsiveColumn mainComponentColumn = mainRow.addColumn().withDisplayRules(12, 12, 9, 9);
        ResponsiveColumn leftColumn = mainRow.addColumn().withDisplayRules(12, 12, 3, 3);
        leftColumn.withComponent(newsCard);

        ResponsiveLayout mainLayout = new ResponsiveLayout(ResponsiveLayout.ContainerType.FLUID).withFlexible();
        mainComponentColumn.withComponent(mainLayout);
        ResponsiveRow row0 = mainLayout.addRow();

        List<News> ckoDashboardNewsList = newsRepository.findByNewstypeIn("CKO_DASHBOARD");
        Random r = new Random();
        ckoDashboardNewsList = ckoDashboardNewsList.stream().filter(news -> news.getDescription().length() > 0).collect(Collectors.toList());
        if(ckoDashboardNewsList.size()>0) {
            News ckoNews = ckoDashboardNewsList.get(r.nextInt(ckoDashboardNewsList.size()));
            row0.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(new BoxImpl().withBgStyle("dark-blue").instance(new MLabel(ckoNews.getDescription()).withStyleName("white-font", "center-label", "huge").withFullWidth()));
        }

        row0.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(consultantAllocationCard);

        ResponsiveRow row1 = mainLayout.addRow();

        row1.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(knowledgeWheelPhoto);

        ResponsiveLayout responsiveColumn1Layout = new ResponsiveLayout();
        ResponsiveLayout responsiveColumn2Layout = new ResponsiveLayout();
        row1.addColumn().withDisplayRules(12,12,6,6).withComponent(responsiveColumn1Layout);
        row1.addColumn().withDisplayRules(12,12,6,6).withComponent(responsiveColumn2Layout);

        ResponsiveRow leftRow = responsiveColumn1Layout.addRow();
        ResponsiveRow rightRow = responsiveColumn2Layout.addRow();

        // *** LEFT COLUMN ***
        if(!VimeoAPI.videoAge.isBefore(LocalDate.now().minusDays(7))) {
            leftRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(monthNewsCardDesign);
        } else {
            leftRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(photoCard);
        }

        leftRow.addColumn().withDisplayRules(12, 12, 12 ,12).withComponent(revenuePerMonthCard);
        leftRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(bubblesCardDesign);

        if(VimeoAPI.videoAge.isBefore(LocalDate.now().minusDays(7))) {
            leftRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(monthNewsCardDesign);
        } else {
            leftRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(photoCard);
        }

        // *** RIGHT COLUMN ***
        if(Math.random()>0.5) {
            rightRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(knowledgeChartCard);
            rightRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(dnaCard);
        } else {
            rightRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(dnaCard);
            rightRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(knowledgeChartCard);
        }
        // rightRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(cateringCard);
        // rightRow.addColumn().withDisplayRules(12, 12, 12, 12).withComponent(vacationCard);




        mainTemplate.setMainContent(board, DashboardView.VIEW_ICON, DashboardView.MENU_NAME, "World of Trustworks", DashboardView.VIEW_BREADCRUMB);

        createNotifications();
    }

    private void createNotifications() {
        User user = VaadinSession.getCurrent().getAttribute(UserSession.class).getUser();

        List<Notification> notificationList = notificationRepository.findByUseruuidAndAndExpirationdateAfter(user.getUuid(), LocalDate.now());
        for (Notification notification : notificationList) {
            logger.info("notification = " + notification);
            Window window = new Window();
            if(notification.getNotificationType().equals(NotificationType.ACHIEVEMENT)) {
                NotificationPopupDesign notificationDesign = new NotificationPopupDesign();
                notificationDesign.getImgNotification().setSource(spriteSheet.getSprite(Integer.parseInt(notification.getThemeimage())));
                notificationDesign.getImgNotification().setHeight(100, PIXELS);
                notificationDesign.getImgNotification().setWidth(100, PIXELS);
                notificationDesign.getLblDetails().setValue(notification.getLink());
                notificationDesign.getLblDetails().setVisible(true);
                notificationDesign.getLblDescription().setValue(notification.getContent());
                notificationDesign.getBtndismiss().addClickListener(event -> {
                    window.close();
                    UI.getCurrent().removeWindow(window);
                    notificationRepository.delete(notification.getUuid());
                });
                window.setContent(notificationDesign);
            } else if (notification.getNotificationType().equals(NotificationType.RELEASENOTE)) {
                ReleaseNotesPopupDesign releaseNotesPopupDesign = new ReleaseNotesPopupDesign();
                releaseNotesPopupDesign.getLblReleaseNote().setValue(notification.getContent());
                releaseNotesPopupDesign.getImgNotification().setSource(new ThemeResource("images/icons/rocket-icon.png"));
                releaseNotesPopupDesign.getBtndismiss().addClickListener(event -> {
                    window.close();
                    UI.getCurrent().removeWindow(window);
                    notificationRepository.delete(notification.getUuid());
                });
                window.setContent(releaseNotesPopupDesign);
            }
            window.setModal(true);
            UI.getCurrent().addWindow(window);
            break;
        }

        List<ReminderHistory> otherReminderHistories = reminderHistoryRepository.findByTargetuuidAndType(user.getUuid(), ReminderType.SPEEDDATE);
        List<ReminderHistory> myReminderHistories = reminderHistoryRepository.findByTypeAndUseruuidOrderByTransmissionDateDesc(ReminderType.SPEEDDATE, user.getUuid());
        for (ReminderHistory otherReminderHistory : otherReminderHistories) {
            boolean found = false;
            for (ReminderHistory myReminderHistory : myReminderHistories) {
                if(otherReminderHistory.getUser().getUuid().equals(myReminderHistory.getTargetuuid())) found = true;
            }
            if(!found) {
                Window window = new Window();
                ConfirmSpeedDateImpl confirmSpeedDate = new ConfirmSpeedDateImpl(user, otherReminderHistory.getUser(), window, reminderHistoryRepository);
                window.setContent(confirmSpeedDate);
                window.setModal(true);
                UI.getCurrent().addWindow(window);
            }
        }
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


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        topMenu.init();
    }

}
